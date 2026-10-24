package com.example.engine

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import com.example.model.AutoPaySignal
import com.example.model.BankCode
import com.example.model.ParsedBankSms
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * Core Bank SMS AutoPay Parsing Pipeline for Subscriptions.AI
 * Integrates transparent rule-based matching, bank segregation, regex amount extraction,
 * AutoPay mandate lifecycle classification, and AI Studio interpretation fallback.
 */
object BankSmsParsingPipeline {

  private const val TAG = "BankSmsPipeline"

  // Regex matching ₹499, Rs. 499, INR 499.00, Rs 1,499.00, 499 INR, 499/-
  private val AMOUNT_REGEX = Pattern.compile(
    """(?:(?:RS|INR|₹)\.?\s?)(\d+(?:,\d+)*(?:\.\d{1,2})?)|(\d+(?:,\d+)*(?:\.\d{1,2})?)\s*(?:INR|RS|₹|/-)""",
    Pattern.CASE_INSENSITIVE
  )

  // Account suffix regex like A/C XX1234 or Card ending 5678
  private val ACCOUNT_REGEX = Pattern.compile(
    """(?:A/C|Acct|Account|Card)\s*(?:no\.?|XX|ending\s*)?([0-9Xx]{3,8})""",
    Pattern.CASE_INSENSITIVE
  )

  // Date regex matching e.g. "23 Oct", "23-10-2026", "23/10/2026", "12-Oct-2026"
  private val DATE_REGEX = Pattern.compile(
    """(\d{1,2}(?:st|nd|rd|th)?\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*|\d{1,2}[/-](?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*[/-]\d{2,4}|\d{1,2}[/-]\d{1,2}(?:[/-]\d{2,4})?)""",
    Pattern.CASE_INSENSITIVE
  )

  // Entity mapping for corporate/legal entity names found in bank SMS to user-facing brands
  private val LEGAL_ENTITY_MAP = mapOf(
    "netflix entertainment" to "Netflix Premium",
    "netflix" to "Netflix Premium",
    "spotify india" to "Spotify",
    "spotify" to "Spotify",
    "curefit" to "Cult.fit",
    "cultfit" to "Cult.fit",
    "cult.fit" to "Cult.fit",
    "reliance jio" to "Jio Fiber & Mobile",
    "jio infocomm" to "Jio Fiber & Mobile",
    "jio platforms" to "Jio Fiber & Mobile",
    "jiofiber" to "Jio Fiber & Mobile",
    "jio" to "Jio Fiber & Mobile",
    "bharti airtel" to "Airtel Xstream",
    "airtel broadband" to "Airtel Xstream",
    "airtel" to "Airtel Xstream",
    "openai" to "ChatGPT Plus",
    "chatgpt" to "ChatGPT Plus",
    "novi digital" to "Disney+ Hotstar",
    "hotstar" to "Disney+ Hotstar",
    "disney" to "Disney+ Hotstar",
    "the ken media" to "The Ken",
    "ken media" to "The Ken",
    "the ken" to "The Ken",
    "bundl technologies" to "Swiggy One",
    "swiggy" to "Swiggy One",
    "zomato limited" to "Zomato Gold",
    "zomato" to "Zomato Gold",
    "times internet" to "Times Prime",
    "times prime" to "Times Prime",
    "sony pictures" to "SonyLIV",
    "sonyliv" to "SonyLIV",
    "zee entertainment" to "Zee5",
    "zee5" to "Zee5",
    "amazon seller" to "Amazon Prime",
    "amazon digital" to "Amazon Prime",
    "amazon prime" to "Amazon Prime",
    "audible audiobooks" to "Audible",
    "audible" to "Audible",
    "apple services" to "Apple One",
    "apple" to "Apple One",
    "google play" to "Google One",
    "google india" to "Google One",
    "google one" to "Google One",
    "adobe systems" to "Adobe Creative Cloud",
    "adobe" to "Adobe Creative Cloud",
    "whoop" to "Whoop Membership",
    "duolingo" to "Duolingo Super",
    "figma" to "Figma Enterprise",
    "github" to "GitHub Enterprise",
    "slack" to "Slack Pro",
    "notion" to "Notion Team",
    "canva" to "Canva Pro",
    "midjourney" to "Midjourney",
    "datadog" to "Datadog APM",
    "salesforce" to "Salesforce Cloud"
  )

  // Dictionary of known merchants for precise keyword matching
  private val KNOWN_MERCHANTS = listOf(
    "Netflix", "Spotify", "Jio", "JioFiber", "Airtel", "Cult.fit", "Adobe", "ChatGPT", "OpenAI",
    "Amazon Prime", "Prime Video", "Disney+ Hotstar", "Hotstar", "YouTube Premium", "Swiggy One",
    "Zomato Gold", "SonyLIV", "Zee5", "Apple", "Google One", "Audible", "The Ken",
    "Figma", "Slack", "Notion", "Canva", "Midjourney", "GitHub", "Microsoft 365", "Times Prime",
    "Tata Neu", "Blinkit", "Urban Company", "Gaana", "Wynk", "LinkedIn"
  )

  /**
   * Transparent rule-based extraction matching the Subscriptions.AI architecture.
   */
  fun parseSms(smsText: String, senderId: String, idPrefix: String = "sms"): ParsedBankSms {
    val textLower = smsText.lowercase()
    val senderLower = senderId.lowercase()

    // 1. Segregate based on Bank Codes (via Sender ID or keywords)
    var detectedBank = BankCode.OTHER
    for (bank in BankCode.values()) {
      if (bank == BankCode.OTHER) continue
      val matchesSignature = bank.signatures.any { sig ->
        senderLower.contains(sig.lowercase()) || textLower.contains(sig.lowercase())
      }
      if (matchesSignature) {
        detectedBank = bank
        break
      }
    }

    // 2. Extract Amount using flexible Regex
    var detectedAmount = 0.0
    val amountMatcher = AMOUNT_REGEX.matcher(smsText)
    if (amountMatcher.find()) {
      val rawNum = (amountMatcher.group(1) ?: amountMatcher.group(2))?.replace(",", "")
      detectedAmount = rawNum?.toDoubleOrNull() ?: 0.0
    }

    // 3. Rule-based AutoPay Signal Detection (Mandate Lifecycle)
    val autopaySignal = when {
      // Cancellation / Revocation
      textLower.contains("revoked") || textLower.contains("cancelled") ||
          textLower.contains("canceled") || textLower.contains("mandate expired") ||
          textLower.contains("stopped") || textLower.contains("failed") ||
          textLower.contains("pause") -> AutoPaySignal.REVOKED

      // Mandate Registration / Creation
      textLower.contains("mandate registered") || textLower.contains("autopay set") ||
          textLower.contains("e-mandate created") || textLower.contains("standing instruction set") ||
          textLower.contains("si created") || textLower.contains("registered successfully") ||
          textLower.contains("mandate created") || textLower.contains("mandate approved") ||
          textLower.contains("mandate of") || textLower.contains("autopay mandate created") -> AutoPaySignal.CREATED

      // Scheduled / Upcoming Pre-debit Notice
      textLower.contains("scheduled") || textLower.contains("due on") ||
          textLower.contains("upcoming") || textLower.contains("will be debited") ||
          textLower.contains("will be presented") || textLower.contains("presentation date") ||
          textLower.contains("pre-debit") || textLower.contains("will debit") ||
          textLower.contains("autopay due") || textLower.contains("scheduled for") -> AutoPaySignal.SCHEDULED

      // Debited / Execution Completed
      textLower.contains("debited") || textLower.contains("paid to") ||
          textLower.contains("deducted") || textLower.contains("vpa debited") ||
          textLower.contains("standing instruction executed") || textLower.contains("mandate debited") ||
          textLower.contains("executed successfully") || textLower.contains("debit of") -> AutoPaySignal.DEBITED

      else -> AutoPaySignal.DEBITED
    }

    // 4. Detect Mandate Type (UPI AutoPay, e-Mandate, Standing Instruction, NACH)
    val mandateType = when {
      textLower.contains("upi autopay") || textLower.contains("upi mandate") || textLower.contains("umn") || textLower.contains("vpa") -> "UPI AutoPay"
      textLower.contains("e-mandate") || textLower.contains("emandate") || textLower.contains("enach") -> "e-Mandate (NPCI)"
      textLower.contains("standing instruction") || textLower.contains("si ") || textLower.contains("recurring standing") -> "Standing Instruction"
      textLower.contains("nach") || textLower.contains("ach") || textLower.contains("ecs") -> "NACH Mandate"
      textLower.contains("card") || textLower.contains("credit card") || textLower.contains("debit card") -> "Card Recurring Token"
      else -> "AutoPay Mandate"
    }

    // Extract UMN / Reference code
    val umnMatcher = Pattern.compile("""(?:Ref|UMN|ID|URN)[:\s/]+([a-zA-Z0-9_-]{5,24})""", Pattern.CASE_INSENSITIVE).matcher(smsText)
    val detectedUmn = if (umnMatcher.find()) umnMatcher.group(1) else null

    // 5. Keyword & Entity Matching for Merchant
    var detectedMerchant = ""
    for ((entityKey, brandName) in LEGAL_ENTITY_MAP) {
      if (textLower.contains(entityKey)) {
        detectedMerchant = brandName
        break
      }
    }

    if (detectedMerchant.isEmpty()) {
      for (known in KNOWN_MERCHANTS) {
        if (textLower.contains(known.lowercase())) {
          detectedMerchant = known
          break
        }
      }
    }

    // Fallback: If not in known list, parse candidate after prepositions
    if (detectedMerchant.isEmpty()) {
      val prepositions = listOf("towards ", "to ", "for ", "at ", "on vpa ", "created for ")
      for (prep in prepositions) {
        val idx = textLower.indexOf(prep)
        if (idx != -1) {
          val candidate = smsText.substring(idx + prep.length)
            .split(" on ", " ref", " via", " dated", " a/c", " bal", ".", "-", "\n", " umn", " next", " for rs")
            .firstOrNull()?.trim()
          if (!candidate.isNullOrBlank() && candidate.length in 3..35) {
            detectedMerchant = candidate
            break
          }
        }
      }
    }

    if (detectedMerchant.isEmpty()) {
      detectedMerchant = "Recurring Vendor"
    }

    // 6. Extract Date
    var detectedDate = "Today"
    val dateMatcher = DATE_REGEX.matcher(smsText)
    if (dateMatcher.find()) {
      detectedDate = dateMatcher.group(1) ?: "Today"
    }

    // 7. Extract Account Suffix
    var accountSuffix: String? = null
    val acctMatcher = ACCOUNT_REGEX.matcher(smsText)
    if (acctMatcher.find()) {
      accountSuffix = acctMatcher.group(1)
    }

    // 8. Calculate Confidence Score
    var confidenceScore = 0.60
    if (detectedBank != BankCode.OTHER) confidenceScore += 0.15
    if (detectedAmount > 0.0) confidenceScore += 0.15
    if (LEGAL_ENTITY_MAP.values.contains(detectedMerchant) || KNOWN_MERCHANTS.contains(detectedMerchant)) confidenceScore += 0.10
    confidenceScore = confidenceScore.coerceIn(0.60, 0.99)

    return ParsedBankSms(
      id = "${idPrefix}_${System.currentTimeMillis()}_${(100..999).random()}",
      rawSender = senderId,
      rawBody = smsText,
      bankCode = detectedBank,
      merchant = detectedMerchant,
      amount = detectedAmount,
      date = detectedDate,
      autopaySignal = autopaySignal,
      confidence = (confidenceScore * 100).toInt() / 100.0,
      source = "Rule-Engine Regex",
      accountSuffix = accountSuffix,
      mandateType = mandateType,
      umnRef = detectedUmn
    )
  }

  /**
   * Batch parser that processes multi-line feeds, inbox dumps, or concatenated messages.
   */
  fun parseAllMessages(text: String): List<ParsedBankSms> {
    if (text.isBlank()) return emptyList()
    val lines = text.lines()
      .map { it.trim() }
      .filter { it.length > 15 }

    return lines.mapIndexed { idx, line ->
      val sender = when {
        line.startsWith("HDFC", ignoreCase = true) -> "AD-HDFCBK"
        line.startsWith("SBI", ignoreCase = true) -> "VK-SBIBK"
        line.startsWith("ICICI", ignoreCase = true) -> "VM-ICICIB"
        line.startsWith("AXIS", ignoreCase = true) -> "AD-AXISBK"
        line.startsWith("KOTAK", ignoreCase = true) -> "KM-KOTAKB"
        line.startsWith("PAYTM", ignoreCase = true) -> "PAYTM"
        line.startsWith("PHONEPE", ignoreCase = true) -> "PHONEPE"
        line.startsWith("CRED", ignoreCase = true) -> "CRED"
        line.startsWith("GOOGLE", ignoreCase = true) -> "GPAY"
        line.contains(":") && line.substringBefore(":").length in 3..12 -> line.substringBefore(":")
        else -> "UPI-MANDATE"
      }
      parseSms(line, sender, idPrefix = "feed_$idx")
    }
  }

  /**
   * Safely scans the Android Device SMS Inbox (requires READ_SMS permission).
   * Reads only bank messages matching bank sender IDs or financial transaction keywords.
   */
  fun readDeviceSmsInbox(context: Context, limit: Int = 30): List<ParsedBankSms> {
    val results = mutableListOf<ParsedBankSms>()
    try {
      val uri: Uri = Telephony.Sms.Inbox.CONTENT_URI
      val projection = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE
      )

      val cursor = context.contentResolver.query(
        uri,
        projection,
        null,
        null,
        "${Telephony.Sms.DATE} DESC"
      )

      cursor?.use {
        val addressIdx = it.getColumnIndex(Telephony.Sms.ADDRESS)
        val bodyIdx = it.getColumnIndex(Telephony.Sms.BODY)
        val dateIdx = it.getColumnIndex(Telephony.Sms.DATE)

        var count = 0
        while (it.moveToNext() && count < limit) {
          val address = it.getString(addressIdx) ?: ""
          val body = it.getString(bodyIdx) ?: ""
          val dateMillis = it.getLong(dateIdx)

          val lowerBody = body.lowercase()
          val isFinancial = lowerBody.contains("debited") || lowerBody.contains("autopay") ||
              lowerBody.contains("mandate") || lowerBody.contains("standing instruction") ||
              lowerBody.contains("si ") || lowerBody.contains("paid") ||
              lowerBody.contains("₹") || lowerBody.contains("inr") || lowerBody.contains("rs.")

          val isBankSender = BankCode.values().any { b ->
            b.signatures.any { sig -> address.contains(sig, ignoreCase = true) }
          }

          if (isFinancial || isBankSender) {
            val parsed = parseSms(body, address, idPrefix = "device_sms")
            val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(dateMillis))
            results.add(parsed.copy(date = formattedDate))
            count++
          }
        }
      }
    } catch (e: SecurityException) {
      Log.e(TAG, "SMS Permission not granted", e)
    } catch (e: Exception) {
      Log.e(TAG, "Error querying SMS inbox", e)
    }

    return results
  }

  /**
   * High-fidelity realistic Indian Bank SMS Sample Dataset for emulator testing & demo.
   * Covers HDFC, SBI, ICICI, Axis, Kotak across all 4 AutoPay signals.
   */
  val sampleBankSmsMessages: List<ParsedBankSms> = listOf(
    parseSms(
      smsText = "A/C XX4921 debited ₹649.00 towards NETFLIX on 23 Oct via AutoPay mandate. Ref: 429381023. - HDFC Bank",
      senderId = "AD-HDFCBK"
    ),
    parseSms(
      smsText = "Pre-debit alert: ₹1,499.00 scheduled for Cult.fit Elite membership on 27 Oct 2026. A/C XX8812 - ICICI Bank",
      senderId = "VM-ICICIB"
    ),
    parseSms(
      smsText = "Mandate Registered: AutoPay set for Spotify Premium at ₹179.00/month. Mandate ID: UMN-883920. - SBI",
      senderId = "VK-SBIBK"
    ),
    parseSms(
      smsText = "Alert: AutoPay mandate for Adobe Creative Cloud (₹4,230.00) has been REVOKED and cancelled as per your request. - AXIS Bank",
      senderId = "AD-AXISBK"
    ),
    parseSms(
      smsText = "₹1,999.00 debited from A/C XX3019 towards OpenAI ChatGPT Plus subscription on 21 Oct. - Kotak Bank",
      senderId = "KM-KOTAKB"
    ),
    parseSms(
      smsText = "Upcoming payment due: ₹999.00 will be debited towards JioFiber Unlimited on 26 Oct. Ensure sufficient balance. - HDFC Bank",
      senderId = "AD-HDFCBK"
    ),
    parseSms(
      smsText = "Your e-Mandate created for Amazon Prime at ₹299.00 quarterly is approved. - ICICI Bank",
      senderId = "VM-ICICIB"
    ),
    parseSms(
      smsText = "Payment of ₹249.00 to The Ken Annual Subscription executed successfully via UPI AutoPay. - SBI",
      senderId = "VK-SBIBK"
    ),
    parseSms(
      smsText = "Standing instruction for Gym Luxury Membership (₹1,499.00) has failed / revoked due to user stop payment. - Axis Bank",
      senderId = "AD-AXISBK"
    ),
    parseSms(
      smsText = "A/C XX4921 debited ₹199.00 towards Audible Audiobooks monthly subscription on 19 Oct. - HDFC Bank",
      senderId = "AD-HDFCBK"
    )
  )

  /**
   * Google AI Studio System Instructions & Payload representation
   * for Team DEBURGERS Subscriptions.AI pitch presentation.
   */
  const val AI_STUDIO_SYSTEM_INSTRUCTIONS = """You are the core extraction engine for Subscriptions.AI. Your job is to process raw bank SMS messages and turn them into structured data for recurring payment detection.

For every SMS provided, you must apply AI interpretation to understand the messy descriptions and wording. 

Extract and normalize the data into a JSON object with the following fields:
1. "merchant": The cleaned name of the service provider.
2. "amount": The numerical transaction amount in INR (₹).
3. "date": The date of the transaction.
4. "bank_code": The name or code of the bank (e.g., HDFC, SBI, ICICI, AXIS, KOTAK) extracted from the sender ID or message text.
5. "autopay_signal": You must classify the message into one of these four mandate lifecycle stages:
   - "CREATED": If the message indicates a mandate may be registered.
   - "SCHEDULED": If the message indicates a future payment may be due.
   - "DEBITED": If the message indicates a payment appears completed.
   - "REVOKED": If a cancellation signal is detected.
6. "confidence": A score from 0.0 to 1.0 indicating how certain you are of this match, which will be used to flag uncertain matches for review."""

  const val AI_STUDIO_SAMPLE_JSON_RESPONSE = """{
  "merchant": "Netflix",
  "amount": 649.00,
  "date": "23 Oct",
  "bank_code": "HDFC",
  "autopay_signal": "DEBITED",
  "confidence": 0.98
}"""
}

package com.example.model

/**
 * AutoPay mandate lifecycle stage matching the Subscriptions.AI specification:
 * - CREATED: Mandate registered
 * - SCHEDULED: Future payment due / upcoming
 * - DEBITED: Payment completed
 * - REVOKED: Mandate cancelled or failed
 */
enum class AutoPaySignal(
  val label: String,
  val description: String,
  val badgeColorHex: Long
) {
  CREATED("CREATED", "Mandate Registered", 0xFF0284C7),       // Sky Blue
  SCHEDULED("SCHEDULED", "Upcoming Due", 0xFFD97706),         // Amber
  DEBITED("DEBITED", "Payment Debited", 0xFF059669),          // Emerald
  REVOKED("REVOKED", "Cancelled / Revoked", 0xFFDC2626)       // Rose Red
}

/**
 * Bank code and sender signatures for segregation (HDFC, SBI, ICICI, AXIS, KOTAK, etc.)
 */
enum class BankCode(
  val code: String,
  val bankName: String,
  val signatures: List<String>,
  val brandColorHex: Long
) {
  HDFC("HDFC", "HDFC Bank", listOf("HDFC", "HDFCBK", "HDFCBN"), 0xFF004C8F),
  SBI("SBI", "State Bank of India", listOf("SBI", "SBIBK", "SBIN", "SBIPSG"), 0xFF280071),
  ICICI("ICICI", "ICICI Bank", listOf("ICICI", "ICICIB", "ICICIP"), 0xFFB02A30),
  AXIS("AXIS", "Axis Bank", listOf("AXIS", "AXISBK", "AXISBN"), 0xFF97144D),
  KOTAK("KOTAK", "Kotak Mahindra Bank", listOf("KOTAK", "KOTAKB", "KMBL"), 0xFFED1C24),
  PAYTM("PAYTM", "Paytm UPI", listOf("PAYTM", "PYTM", "PAYTMB"), 0xFF002970),
  PHONEPE("PHONEPE", "PhonePe", listOf("PHONEPE", "PHNPE", "PHE"), 0xFF5F259F),
  GPAY("GPAY", "Google Pay", listOf("GPAY", "GOOGLEPAY", "GOOGLE"), 0xFF4285F4),
  CRED("CRED", "CRED", listOf("CRED", "DREAMPLUG"), 0xFF1C1C1C),
  IDFC("IDFC", "IDFC FIRST Bank", listOf("IDFC", "IDFCFB"), 0xFF9E1B32),
  OTHER("OTHER", "Other Indian Bank / UPI", listOf("BANK", "UPI", "VPA", "NPCI", "NACH"), 0xFF475569)
}

/**
 * Structured parsed output from Bank SMS message
 */
data class ParsedBankSms(
  val id: String,
  val rawSender: String,
  val rawBody: String,
  val bankCode: BankCode,
  val merchant: String,
  val amount: Double,
  val date: String,
  val autopaySignal: AutoPaySignal,
  val confidence: Double, // 0.0 to 1.0
  val source: String = "Rule-Engine Regex",
  val timestamp: String = "Oct 24, 2026",
  val isAddedToTracked: Boolean = false,
  val accountSuffix: String? = null,
  val mandateType: String = "UPI AutoPay",
  val umnRef: String? = null
)

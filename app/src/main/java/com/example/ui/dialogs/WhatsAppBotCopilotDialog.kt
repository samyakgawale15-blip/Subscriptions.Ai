package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Subscription
import com.example.ui.components.BrandIcon
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicPrimaryButton
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.SoftAmber
import com.example.ui.theme.SoftAmberBg
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteBorderSubtle
import com.example.ui.theme.WhiteSurface

@Composable
fun WhatsAppBotCopilotDialog(
  subscription: Subscription,
  onApplyPrice: (String, Double) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var hasCopied by remember { mutableStateOf(false) }
  var targetPrice by remember { mutableDoubleStateOf(subscription.targetNegotiatedPrice) }

  // Vendor Chatbot & WhatsApp URLs
  val (chatbotName, chatbotUrl, whatsappNumber) = remember(subscription.merchantName) {
    val clean = subscription.merchantName.lowercase()
    when {
      clean.contains("jio") -> Triple("Jio Chatbot", "https://wa.me/917000770007", "917000770007")
      clean.contains("airtel") -> Triple("Airtel Thanks Chat", "https://wa.me/919810101234", "919810101234")
      clean.contains("netflix") -> Triple("Netflix Help Chat", "https://help.netflix.com/en/contactus", null)
      clean.contains("spotify") -> Triple("Spotify Support Chat", "https://support.spotify.com/contact-spotify-support/", null)
      clean.contains("adobe") -> Triple("Adobe 24/7 Support", "https://helpx.adobe.com/contact.html", null)
      clean.contains("amazon") || clean.contains("prime") -> Triple("Amazon CS Chat", "https://www.amazon.com/gp/help/customer/contact-us", null)
      clean.contains("comcast") || clean.contains("xfinity") -> Triple("Xfinity Assistant", "https://www.xfinity.com/chat/", null)
      else -> Triple("${subscription.merchantName} Web Chatbot", "https://www.google.com/search?q=${Uri.encode(subscription.merchantName + " support live chat")}", null)
    }
  }

  // Pre-crafted message generated by the WhatsApp Bot telling the user exactly what to type
  val copilotScript = remember(subscription, targetPrice) {
    val clean = subscription.merchantName.lowercase()
    when {
      clean.contains("jio") ->
        "Hi Jio Support, I want to review my current fiber plan (Rs 999/mo). Competitors offer similar speed at Rs 699. Can you apply the loyalty retention discount or downgrade options before the next auto-renewal?"
      clean.contains("airtel") ->
        "Hi Airtel Support, I am evaluating my broadband bill before my renewal date. Are there any retention discounts or downgrade options to retain my active line?"
      clean.contains("netflix") ->
        "Hello Netflix Support, I'm reviewing household subscription spend. Before auto-renewing, I'd like to check if any lower-tier retention rates or temporary pause options apply."
      clean.contains("adobe") ->
        "Hello Adobe Support, I need to cancel my Creative Cloud subscription due to budget constraints, unless the ₹2,999/mo customer retention discount can be applied to my plan."
      clean.contains("spotify") ->
        "Hi Spotify Support, I'm auditing my music subscriptions. Can you check if my account is eligible for the annual retention pricing before my auto-debit?"
      else ->
        "Hello ${subscription.merchantName} Support, I am reviewing my subscription cost (₹${"%.2f".format(subscription.currentCost)}/mo). I would like to explore lower-tier plans or retention pricing of ₹${"%.2f".format(targetPrice)}/mo before the next renewal."
    }
  }

  val monthlySavings = (subscription.currentCost - targetPrice).coerceAtLeast(0.0)
  val annualSavings = monthlySavings * 12.0

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .heightIn(max = 680.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(WhiteSurface)
        .border(1.dp, WhiteBorder, RoundedCornerShape(20.dp)),
      color = WhiteSurface
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            BrandIcon(name = subscription.merchantName, size = 36.dp, iconSize = 18.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = subscription.merchantName,
                color = TextPrimary,
                fontSize = 15.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "WhatsApp Bot & Web Chatbot Copilot",
                color = SoftSage,
                fontSize = 10.5.sp,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // WhatsApp Bot Guidance Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SoftSageBg)
            .border(1.dp, SoftSageBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366)), // WhatsApp Green
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "WHATSAPP BOT INSTRUCTIONS",
                color = Color(0xFF0F5132),
                fontSize = 9.5.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Copy the message below, then tap to redirect directly to $chatbotName.",
                color = TextPrimary,
                fontSize = 11.5.sp,
                fontFamily = PlusJakartaSansFontFamily
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // What to Type Box
        Text(
          text = "WHAT TO TYPE IN THE CHATBOT",
          color = TextMuted,
          fontSize = 9.5.sp,
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp)
        ) {
          Column {
            Text(
              text = copilotScript,
              color = TextPrimary,
              fontSize = 12.5.sp,
              fontFamily = PlusJakartaSansFontFamily,
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              NeumorphicButton(
                onClick = {
                  clipboardManager.setText(AnnotatedString(copilotScript))
                  hasCopied = true
                  Toast.makeText(context, "Copied script to clipboard!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = if (hasCopied) SoftSageBg else Color(0xFFF8FAFC),
                elevation = 3.dp,
                testTag = "btn_copy_copilot_script"
              ) {
                Icon(
                  imageVector = if (hasCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                  contentDescription = null,
                  tint = if (hasCopied) SoftSage else TextPrimary,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = if (hasCopied) "Copied!" else "Copy Text",
                  color = if (hasCopied) SoftSage else TextPrimary,
                  fontSize = 11.sp,
                  fontFamily = OutfitFontFamily,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Direct Redirection Actions
        Text(
          text = "DIRECT REDIRECTION",
          color = TextMuted,
          fontSize = 9.5.sp,
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Primary Neumorphic Button: Open Official Chatbot
        NeumorphicPrimaryButton(
          onClick = {
            clipboardManager.setText(AnnotatedString(copilotScript))
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(chatbotUrl))
              context.startActivity(intent)
            } catch (e: Exception) {
              Toast.makeText(context, "Opening browser for $chatbotName", Toast.LENGTH_SHORT).show()
            }
          },
          primaryColor = Color(0xFF1E293B),
          shape = RoundedCornerShape(14.dp),
          elevation = 6.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          testTag = "btn_redirect_chatbot"
        ) {
          Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Open $chatbotName ➔",
            fontSize = 13.sp,
            color = Color.White,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Neumorphic Action: Send on WhatsApp directly
        NeumorphicButton(
          onClick = {
            clipboardManager.setText(AnnotatedString(copilotScript))
            val waUrl = if (whatsappNumber != null) {
              "https://wa.me/$whatsappNumber?text=${Uri.encode(copilotScript)}"
            } else {
              "https://wa.me/?text=${Uri.encode(copilotScript)}"
            }
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
              context.startActivity(intent)
            } catch (e: Exception) {
              Toast.makeText(context, "Opening WhatsApp...", Toast.LENGTH_SHORT).show()
            }
          },
          shape = RoundedCornerShape(14.dp),
          backgroundColor = Color(0xFFF8FAFC),
          elevation = 4.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          testTag = "btn_open_whatsapp_bot"
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Send via WhatsApp Bot",
            color = TextPrimary,
            fontSize = 12.sp,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Price & Applied Savings
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Target Price After Negotiation",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = PlusJakartaSansFontFamily
              )
              Text(
                text = "₹${"%.2f".format(targetPrice)}/mo",
                color = SoftAmber,
                fontSize = 13.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold
              )
            }

            Slider(
              value = targetPrice.toFloat(),
              onValueChange = { targetPrice = it.toDouble() },
              valueRange = 0f..subscription.currentCost.toFloat(),
              colors = SliderDefaults.colors(
                thumbColor = SoftSage,
                activeTrackColor = SoftSage,
                inactiveTrackColor = WhiteBorder
              ),
              modifier = Modifier.height(30.dp)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Current: ₹${"%.2f".format(subscription.currentCost)}/mo",
                color = TextMuted,
                fontSize = 10.sp
              )
              Text(
                text = "+₹${"%.0f".format(annualSavings)}/yr savings",
                color = SoftSage,
                fontSize = 10.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Confirm Rate Button (Neumorphic SoftSage)
        NeumorphicPrimaryButton(
          onClick = {
            onApplyPrice(subscription.id, targetPrice)
            onDismiss()
          },
          primaryColor = SoftSage,
          shape = RoundedCornerShape(14.dp),
          elevation = 6.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          testTag = "btn_lock_copilot_savings"
        ) {
          Icon(Icons.Default.Savings, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Lock Discounted Rate (₹${"%.2f".format(targetPrice)}/mo)",
            fontSize = 13.sp,
            color = Color.White,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

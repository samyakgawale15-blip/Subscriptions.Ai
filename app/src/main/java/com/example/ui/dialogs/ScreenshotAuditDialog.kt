package com.example.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.BillingCadence
import com.example.model.RecommendedAction
import com.example.model.ScreenshotAuditResult
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import com.example.ui.components.BrandIcon
import com.example.ui.components.NeumorphicPrimaryButton
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.SoftAmber
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ScreenshotAuditDialog(
  onSaveSubscription: (Subscription) -> Unit,
  onDismiss: () -> Unit
) {
  val scope = rememberCoroutineScope()
  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var isAuditing by remember { mutableStateOf(false) }
  var auditResult by remember { mutableStateOf<ScreenshotAuditResult?>(null) }

  // System zero-permission photo picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedImageUri = uri
      scope.launch {
        isAuditing = true
        delay(900)
        // Simulated high precision OCR / AI bill extraction
        auditResult = ScreenshotAuditResult(
          id = "audit_" + UUID.randomUUID().toString().take(6),
          merchantName = "Jio Fiber & Mobile",
          startDate = "Oct 14, 2026",
          expiryDate = "Nov 14, 2026",
          currentCost = 12.99,
          billingCadence = BillingCadence.MONTHLY,
          planName = "Jio 100 Mbps Unlimited Fiber + OTT",
          auditStatus = "Audit Verified: Renews in 28 days",
          suggestedChatbotScript = "Hi Jio Support, I would like to review my current fiber plan (Rs 999/mo). Can you apply the loyalty retention discount before the Nov 14 auto-renewal?",
          chatbotUrl = "https://wa.me/917000770007"
        )
        isAuditing = false
      }
    }
  }

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
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SoftSageBg)
                .border(1.dp, SoftSageBorder, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.DocumentScanner,
                contentDescription = "Audit Screenshot",
                tint = SoftSage,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Screenshot Bill Audit",
                color = TextPrimary,
                fontSize = 16.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Audits start date, price & expiry automatically",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = PlusJakartaSansFontFamily
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prominent Squircle Upload Area
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WhiteBackground)
            .border(1.5.dp, if (selectedImageUri != null) SoftSage else WhiteBorder, RoundedCornerShape(16.dp))
            .clickable {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            }
            .padding(20.dp)
            .testTag("btn_select_screenshot_picker"),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (selectedImageUri != null) {
              AsyncImage(
                model = selectedImageUri,
                contentDescription = "Selected screenshot preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(120.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.dp, SoftSage, RoundedCornerShape(12.dp))
              )
            } else {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(SoftSageBg)
                  .border(1.dp, SoftSageBorder, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AddPhotoAlternate,
                  contentDescription = null,
                  tint = SoftSage,
                  modifier = Modifier.size(22.dp)
                )
              }
            }

            Text(
              text = if (selectedImageUri != null) "Screenshot Loaded • Tap to Change" else "Select Bill or Invoice Screenshot",
              color = TextPrimary,
              fontSize = 13.sp,
              fontFamily = OutfitFontFamily,
              fontWeight = FontWeight.SemiBold
            )

            Text(
              text = "Tap to pick from gallery or camera (e.g. Jio, Netflix, Adobe)",
              color = TextMuted,
              fontSize = 10.5.sp,
              fontFamily = PlusJakartaSansFontFamily
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Instant Preset Samples for immediate testing
        Text(
          text = "OR AUDIT PRESET BILL SAMPLES",
          color = TextMuted,
          fontSize = 9.5.sp,
          fontFamily = PlusJakartaSansFontFamily,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        val presets = listOf(
          ScreenshotAuditResult(
            id = "preset_jio",
            merchantName = "Jio Fiber & Mobile",
            startDate = "Oct 14, 2026",
            expiryDate = "Nov 14, 2026",
            currentCost = 999.0,
            billingCadence = BillingCadence.MONTHLY,
            planName = "Jio 100 Mbps Fiber + OTT",
            auditStatus = "Extracted • Renews in 28 days",
            suggestedChatbotScript = "Hi Jio Support, I would like to review my current fiber plan (Rs 999/mo). Can you apply the loyalty retention discount or downgrade options before the Nov 14 auto-renewal?",
            chatbotUrl = "https://wa.me/917000770007"
          ),
          ScreenshotAuditResult(
            id = "preset_netflix",
            merchantName = "Netflix Premium",
            startDate = "Oct 2, 2026",
            expiryDate = "Nov 2, 2026",
            currentCost = 649.0,
            billingCadence = BillingCadence.MONTHLY,
            planName = "Netflix 4K UHD 4 Screens",
            auditStatus = "Extracted • Renews in 16 days",
            suggestedChatbotScript = "Hello Netflix Support, I'm reviewing my active tier before the auto-debit on Nov 2. Is there a promotional rate or can I pause my account?",
            chatbotUrl = "https://help.netflix.com/en/contactus"
          ),
          ScreenshotAuditResult(
            id = "preset_adobe",
            merchantName = "Adobe Creative Cloud",
            startDate = "Oct 24, 2025",
            expiryDate = "Oct 24, 2026",
            currentCost = 4230.0,
            billingCadence = BillingCadence.MONTHLY,
            planName = "All Apps Cloud Suite",
            auditStatus = "Contract ending in 8 days",
            suggestedChatbotScript = "Hello Adobe Support, I need to cancel my subscription before renewal unless the ₹2,999/mo retention price can be applied.",
            chatbotUrl = "https://helpx.adobe.com/contact.html"
          )
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          presets.forEach { preset ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(WhiteBackground)
                .border(1.dp, WhiteBorderSubtle, RoundedCornerShape(10.dp))
                .clickable {
                  scope.launch {
                    isAuditing = true
                    delay(400)
                    auditResult = preset
                    isAuditing = false
                  }
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                BrandIcon(name = preset.merchantName, size = 26.dp, iconSize = 13.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = preset.merchantName,
                    color = TextPrimary,
                    fontSize = 11.5.sp,
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${preset.startDate} ➔ ${preset.expiryDate}",
                    color = TextMuted,
                    fontSize = 9.5.sp,
                    fontFamily = PlusJakartaSansFontFamily
                  )
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "₹${"%.2f".format(preset.currentCost)}/mo",
                  color = TextPrimary,
                  fontSize = 11.sp,
                  fontFamily = OutfitFontFamily,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Audit ➔",
                  color = SoftSage,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Auditing Progress Indicator
        if (isAuditing) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SoftSageBg)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = SoftSage,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Auditing screenshot dates, plan & charges...",
              color = SoftSage,
              fontSize = 11.sp,
              fontFamily = PlusJakartaSansFontFamily,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Extracted Result Card
        auditResult?.let { res ->
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(WhiteBackground)
              .border(1.dp, SoftSageBorder, RoundedCornerShape(14.dp))
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = SoftSage,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "AUDIT RESULT",
                  color = SoftSage,
                  fontSize = 10.sp,
                  fontFamily = OutfitFontFamily,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
              }

              Text(
                text = res.auditStatus,
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = PlusJakartaSansFontFamily
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Extracted Details Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("MERCHANT / PLAN", color = TextMuted, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                Text(res.merchantName, color = TextPrimary, fontSize = 13.sp, fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(res.planName, color = TextSecondary, fontSize = 10.sp, fontFamily = PlusJakartaSansFontFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)
              }

              Column(horizontalAlignment = Alignment.End) {
                Text("RATE", color = TextMuted, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                Text("₹${"%.2f".format(res.currentCost)}/mo", color = TextPrimary, fontSize = 13.sp, fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Start & Expiry Date row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(WhiteSurface)
                .border(0.75.dp, WhiteBorderSubtle, RoundedCornerShape(8.dp))
                .padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SoftSage, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                  Text("START DATE", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                  Text(res.startDate, color = TextPrimary, fontSize = 10.5.sp, fontFamily = PlusJakartaSansFontFamily, fontWeight = FontWeight.Medium)
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EventBusy, contentDescription = null, tint = SoftAmber, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.End) {
                  Text("EXPIRY / RENEWAL", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                  Text(res.expiryDate, color = TextPrimary, fontSize = 10.5.sp, fontFamily = PlusJakartaSansFontFamily, fontWeight = FontWeight.SemiBold)
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Save to Tracker (Neumorphic Style Button)
            NeumorphicPrimaryButton(
              onClick = {
                val newSub = Subscription(
                  id = "sub_" + UUID.randomUUID().toString().take(6),
                  merchantName = res.merchantName,
                  currentCost = res.currentCost,
                  billingCadence = res.billingCadence,
                  classification = SubscriptionClassification.ACTIVE_UTILITY,
                  recommendedAction = RecommendedAction.AI_NEGOTIATE,
                  targetNegotiatedPrice = (res.currentCost * 0.7),
                  negotiationLeverage = "Audited via bill screenshot",
                  category = "Audited Services",
                  startDate = res.startDate,
                  renewalDate = res.expiryDate,
                  chatbotUrl = res.chatbotUrl,
                  status = SubscriptionStatus.ACTIVE,
                  notes = "Verified by Screenshot Audit on ${res.startDate}."
                )
                onSaveSubscription(newSub)
                onDismiss()
              },
              primaryColor = TextPrimary,
              contentColor = Color.White,
              shape = RoundedCornerShape(14.dp),
              elevation = 5.dp,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              testTag = "btn_save_audited_subscription"
            ) {
              Text(
                text = "✓ Confirm & Add to Active Subscriptions",
                fontSize = 12.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}

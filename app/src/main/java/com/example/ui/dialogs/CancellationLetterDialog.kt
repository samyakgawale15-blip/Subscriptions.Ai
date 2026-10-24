package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.BillingCadence
import com.example.model.Subscription
import com.example.ui.components.BrandIcon
import com.example.ui.components.NeumorphicPrimaryButton
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun CancellationLetterDialog(
  subscription: Subscription,
  onDismiss: () -> Unit
) {
  val annualSaved = if (subscription.billingCadence == BillingCadence.MONTHLY) {
    subscription.currentCost * 12.0
  } else subscription.currentCost

  val legalLetterText = """
    NOTICE OF IMMEDIATE SUBSCRIPTION TERMINATION
    Date: October 10, 2026
    To: ${subscription.merchantName} Billing Desk
    Subject: Formal Revocation of Payment Authorization
    
    Pursuant to the FTC 'Click-to-Cancel' Rule (16 CFR Part 425), please be advised that all recurring billing authorizations for this account are revoked effective immediately.
    
    Status: Card merchant token terminated.
    Annual Saved: ₹${"%.2f".format(annualSaved)}/yr
  """.trimIndent()

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(16.dp))
        .background(WhiteSurface)
        .border(1.dp, WhiteBorder, RoundedCornerShape(16.dp)),
      color = WhiteSurface
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            BrandIcon(
              name = subscription.merchantName,
              size = 32.dp,
              iconSize = 16.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Cancelled ${subscription.merchantName}",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Dark-Pattern Bypass Protocol Active",
                color = SoftSage,
                fontSize = 10.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Success Box (White Mode)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SoftSageBg)
            .border(1.dp, SoftSageBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SoftSage,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Recurring Charge Terminated",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Saved ₹${"%.2f".format(annualSaved)}/yr in subscription waste.",
                color = SoftSage,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Letter Preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Text(
            text = legalLetterText,
            color = TextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        NeumorphicPrimaryButton(
          onClick = onDismiss,
          primaryColor = TextPrimary,
          contentColor = androidx.compose.ui.graphics.Color.White,
          shape = RoundedCornerShape(10.dp),
          elevation = 4.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          testTag = "btn_close_cancellation_letter"
        ) {
          Text("Done & Return to Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
        }
      }
    }
  }
}

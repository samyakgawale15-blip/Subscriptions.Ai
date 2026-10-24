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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DiscoveryEngine
import com.example.ui.components.NeumorphicPrimaryButton
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSky
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun DiscoverySheet(
  isDiscovering: Boolean,
  statusMessage: String,
  report: DiscoveryEngine.DiscoveryReport?,
  onTriggerScan: () -> Unit,
  onDismiss: () -> Unit
) {
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
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ElectricBolt,
              contentDescription = null,
              tint = SoftSky,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Automated Discovery Engine",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Plaid Pipeline & Autocorrelation Scanner",
                color = TextMuted,
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Banking Feeds Status
        Text(
          text = "CONNECTED INSTITUTION PIPELINES",
          color = TextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          InstitutionChip("Chase Sapphire", isConnected = true)
          InstitutionChip("Amex Platinum", isConnected = true)
          InstitutionChip("Brex / SVB", isConnected = true)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Engine Status Box (White Mode)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isDiscovering) {
                CircularProgressIndicator(
                  color = SoftSky,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(15.dp)
                )
              } else {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = SoftSage,
                  modifier = Modifier.size(15.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isDiscovering) statusMessage else "Discovery Engine Ready",
                color = if (isDiscovering) SoftSky else SoftSage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Evaluates transaction intervals (28-32 days monthly cadence, 360-366 days annual). Flags recurring billing without user login.",
              color = TextSecondary,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }

        // Report View
        if (report != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SoftSageBg)
              .padding(10.dp)
          ) {
            Column {
              Text(
                text = "AUDIT SCAN REPORT",
                color = SoftSage,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("Transactions Ingested", color = TextSecondary, fontSize = 10.sp)
                  Text("${report.scannedTransactionsCount} charges", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                  Text("Monthly Autopay", color = TextSecondary, fontSize = 10.sp)
                  Text("₹${"%.2f".format(report.monthlyAutopayTotal)}", color = SoftSage, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text("Zombie Leakage", color = SoftRose, fontSize = 10.sp)
                  Text("₹${"%.2f".format(report.zombieLeakageMonthly)}", color = SoftRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Trigger Button (Neumorphic Style)
        NeumorphicPrimaryButton(
          onClick = onTriggerScan,
          enabled = !isDiscovering,
          primaryColor = TextPrimary,
          contentColor = Color.White,
          shape = RoundedCornerShape(10.dp),
          elevation = 4.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          testTag = "btn_trigger_discovery_scan"
        ) {
          Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isDiscovering) "Scanning Bank Streams..." else "Run Bank & Plaid Discovery Scan",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun InstitutionChip(name: String, isConnected: Boolean) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(WhiteBackground)
      .border(1.dp, WhiteBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 7.dp, vertical = 4.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(5.dp)
          .clip(CircleShape)
          .background(if (isConnected) SoftSage else TextMuted)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(name, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
  }
}

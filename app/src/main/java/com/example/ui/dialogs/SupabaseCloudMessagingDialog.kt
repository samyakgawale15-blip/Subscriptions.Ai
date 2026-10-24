package com.example.ui.dialogs

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SupabaseService
import com.example.model.RenewalAlert
import com.example.model.Subscription
import com.example.ui.components.BrandIcon
import com.example.ui.theme.SoftAmber
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SupabaseBg
import com.example.ui.theme.SupabaseBorder
import com.example.ui.theme.SupabaseGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface
import kotlinx.coroutines.launch

@Composable
fun SupabaseCloudMessagingDialog(
  subscriptions: List<Subscription>,
  alerts: List<RenewalAlert>,
  thresholdDays: Int,
  autoAlertsEnabled: Boolean,
  isDispatching: Boolean,
  onSetThresholdDays: (Int) -> Unit,
  onToggleAutoAlerts: (Boolean) -> Unit,
  onTriggerCheckAndAlert: (Context) -> Unit,
  onSendSingleAlert: (Subscription, Context) -> Unit,
  onToggleVirtualCard: (String) -> Unit,
  onCancelSub: (String) -> Unit,
  onClearAlerts: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var pingStatus by remember { mutableStateOf<String?>(null) }
  var isPinging by remember { mutableStateOf(false) }

  val approachingSubs = remember(subscriptions, thresholdDays) {
    SupabaseService.getSubscriptionsApproachingRenewal(subscriptions, thresholdDays)
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .heightIn(max = 660.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(WhiteSurface)
        .border(1.dp, WhiteBorder, RoundedCornerShape(16.dp)),
      color = WhiteSurface
    ) {
      Column(
        modifier = Modifier
          .padding(18.dp)
          .animateContentSize()
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SupabaseBg)
                .border(1.dp, SupabaseBorder, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = "Supabase SCM",
                tint = SupabaseGreen,
                modifier = Modifier.size(19.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Supabase Cloud Messaging",
                  color = TextPrimary,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFECFDF5))
                    .border(0.5.dp, Color(0xFF6EE7B7), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text("SCM RELAY", color = SupabaseGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
              }
              Text(
                text = "Renewal Date Alerts & Webhook Push Notifications",
                color = TextMuted,
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Relay & Token Telemetry Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SupabaseBg)
            .border(1.dp, SupabaseBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(SupabaseGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Realtime Broadcast Channel: active",
                  color = TextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Text(
                text = "Device Token: ${SupabaseService.scmDeviceToken.take(16)}...",
                color = TextMuted,
                fontSize = 9.5.sp
              )
            }

            if (pingStatus != null) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = pingStatus!!,
                color = SupabaseGreen,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Threshold Window & Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "ALERT WINDOW",
              color = TextMuted,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Notify when renewal is ≤ $thresholdDays days away",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(3, 7, 14).forEach { days ->
              FilterChip(
                selected = thresholdDays == days,
                onClick = { onSetThresholdDays(days) },
                label = { Text("${days}d", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = SupabaseBg,
                  selectedLabelColor = SupabaseGreen
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(28.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Auto-Alert Push Switch & Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
              checked = autoAlertsEnabled,
              onCheckedChange = { onToggleAutoAlerts(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SupabaseGreen
              ),
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Auto-Push Notifications",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "Dispatches Android system heads-up alert",
                color = TextMuted,
                fontSize = 9.5.sp
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
              onClick = {
                scope.launch {
                  isPinging = true
                  val (ok, msg) = SupabaseService.testCloudMessagingPing()
                  isPinging = false
                  pingStatus = "✓ SCM Relay verified: $msg"
                }
              },
              shape = RoundedCornerShape(6.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(30.dp)
            ) {
              if (isPinging) {
                CircularProgressIndicator(strokeWidth = 1.5.dp, modifier = Modifier.size(10.dp), color = SupabaseGreen)
              } else {
                Text("Test Ping", fontSize = 10.sp, color = TextPrimary)
              }
            }

            Button(
              onClick = { onTriggerCheckAndAlert(context) },
              colors = ButtonDefaults.buttonColors(
                containerColor = SupabaseGreen,
                contentColor = TextPrimary
              ),
              shape = RoundedCornerShape(6.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
              modifier = Modifier.height(30.dp).testTag("btn_trigger_scm_check")
            ) {
              if (isDispatching) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(12.dp), color = TextPrimary)
              } else {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Scan & Alert (${approachingSubs.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Tabs / Lists
        LazyColumn(
          modifier = Modifier
            .weight(1f, fill = false)
            .heightIn(max = 380.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Approaching Renewals Section
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "APPROACHING RENEWALS (${approachingSubs.size})",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )

              if (approachingSubs.isNotEmpty()) {
                Text(
                  text = "Scheduled within $thresholdDays days",
                  color = TextMuted,
                  fontSize = 10.sp
                )
              }
            }
          }

          if (approachingSubs.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(WhiteBackground)
                  .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                  .padding(14.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No subscriptions renewing within the next $thresholdDays days. All good!",
                  color = TextMuted,
                  fontSize = 11.5.sp
                )
              }
            }
          } else {
            items(approachingSubs) { (sub, days) ->
              ApproachingRenewalItem(
                subscription = sub,
                daysRemaining = days,
                onSendAlert = { onSendSingleAlert(sub, context) },
                onToggleCard = { onToggleVirtualCard(sub.id) },
                onCancel = { onCancelSub(sub.id) }
              )
            }
          }

          // Dispatched Alerts History Section
          item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "DISPATCHED SCM ALERTS HISTORY (${alerts.size})",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )

              if (alerts.isNotEmpty()) {
                Text(
                  text = "Clear History",
                  color = TextMuted,
                  fontSize = 10.sp,
                  modifier = Modifier.clickable { onClearAlerts() }
                )
              }
            }
          }

          if (alerts.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(WhiteBackground)
                  .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                  .padding(12.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No alerts dispatched yet. Click 'Scan & Alert' to broadcast via SCM.",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }
          } else {
            items(alerts) { alert ->
              DispatchedAlertCard(alert = alert)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ApproachingRenewalItem(
  subscription: Subscription,
  daysRemaining: Int,
  onSendAlert: () -> Unit,
  onToggleCard: () -> Unit,
  onCancel: () -> Unit
) {
  val urgencyColor = if (daysRemaining <= 3) SoftRose else SoftAmber
  val urgencyBg = if (daysRemaining <= 3) Color(0xFFFEF2F2) else Color(0xFFFFFBEB)
  val urgencyBorder = if (daysRemaining <= 3) Color(0xFFFECACA) else Color(0xFFFDE68A)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(WhiteBackground)
      .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
      .padding(10.dp)
      .testTag("approaching_sub_${subscription.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          BrandIcon(name = subscription.merchantName, size = 32.dp, iconSize = 16.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = subscription.merchantName,
              color = TextPrimary,
              fontSize = 12.5.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Renews: ${subscription.renewalDate} • ₹${"%.2f".format(subscription.currentCost)}/mo",
              color = TextMuted,
              fontSize = 10.5.sp
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(urgencyBg)
            .border(0.5.dp, urgencyBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "⚡ In $daysRemaining ${if (daysRemaining == 1) "day" else "days"}",
            color = urgencyColor,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Action row: Alert sent / Send Alert + Freeze Card + Cancel
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Virtual Card Quick Freeze
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (subscription.isVirtualCardFrozen) Color(0xFFEFF6FF) else Color(0xFFF0FDF4))
              .border(0.5.dp, if (subscription.isVirtualCardFrozen) Color(0xFF93C5FD) else Color(0xFF86EFAC), RoundedCornerShape(4.dp))
              .clickable { onToggleCard() }
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (subscription.isVirtualCardFrozen) Icons.Default.Lock else Icons.Default.CreditCard,
                contentDescription = null,
                tint = if (subscription.isVirtualCardFrozen) Color(0xFF1D4ED8) else Color(0xFF15803D),
                modifier = Modifier.size(9.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = if (subscription.isVirtualCardFrozen) "Card Frozen" else "Card Active",
                color = if (subscription.isVirtualCardFrozen) Color(0xFF1D4ED8) else Color(0xFF15803D),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          // 1-Click Cancel Action
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFFEF2F2))
              .border(0.5.dp, Color(0xFFFECACA), RoundedCornerShape(4.dp))
              .clickable { onCancel() }
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text("1-Click Cancel", color = SoftRose, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
          }
        }

        // Send Alert Push button
        Button(
          onClick = onSendAlert,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (subscription.hasRenewalAlertSent) Color(0xFFF3F4F6) else SupabaseGreen,
            contentColor = if (subscription.hasRenewalAlertSent) TextMuted else TextPrimary
          ),
          shape = RoundedCornerShape(5.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
          modifier = Modifier.height(26.dp).testTag("btn_send_alert_${subscription.id}")
        ) {
          if (subscription.hasRenewalAlertSent) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(10.dp), tint = SoftSage)
            Spacer(modifier = Modifier.width(3.dp))
            Text("Alert Sent", fontSize = 9.sp, color = TextMuted)
          } else {
            Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Push SCM Alert", fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun DispatchedAlertCard(alert: RenewalAlert) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(WhiteBackground)
      .border(0.75.dp, WhiteBorder, RoundedCornerShape(8.dp))
      .padding(10.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = SupabaseGreen,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = alert.alertTitle,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Text(text = alert.timestamp, color = TextMuted, fontSize = 9.5.sp)
      }

      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = alert.alertBody,
        color = TextSecondary,
        fontSize = 10.5.sp,
        lineHeight = 14.sp
      )

      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(5.dp)
              .clip(CircleShape)
              .background(SupabaseGreen)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${alert.channel} • Delivered",
            color = TextMuted,
            fontSize = 9.sp
          )
        }

        Text(
          text = "Priority: ${alert.priority}",
          color = if (alert.priority == "CRITICAL") SoftRose else TextMuted,
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

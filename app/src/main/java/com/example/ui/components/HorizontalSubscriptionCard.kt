package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.model.BillingCadence
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import com.example.ui.theme.SoftAmber
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftRoseBg
import com.example.ui.theme.SoftRoseBorder
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteBorderSubtle
import com.example.ui.theme.WhiteSurface

@Composable
fun HorizontalSubscriptionCard(
  subscription: Subscription,
  onCancelClick: (String) -> Unit,
  onNegotiateClick: (Subscription) -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  val isCancelled = subscription.status == SubscriptionStatus.CANCELLED
  val isZombie = subscription.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION
  val isNegotiated = subscription.status == SubscriptionStatus.NEGOTIATED_SAVED

  val borderColor = when {
    isCancelled -> WhiteBorderSubtle
    isZombie -> SoftRoseBorder
    isNegotiated -> SoftSageBorder
    else -> WhiteBorder
  }

  Box(
    modifier = modifier
      .width(155.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(WhiteSurface)
      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
      .padding(12.dp)
      .testTag("horizontal_sub_${subscription.id}")
  ) {
    Column {
      // Top Row: Logo & Options Menu ("•••")
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        BrandIcon(
          name = subscription.merchantName,
          size = 38.dp,
          iconSize = 19.dp
        )

        Box {
          NeumorphicIconButton(
            icon = Icons.Default.MoreVert,
            onClick = { menuExpanded = true },
            size = 28.dp,
            iconSize = 14.dp,
            elevation = 2.dp
          )

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
              .background(WhiteSurface)
              .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
          ) {
            if (!isCancelled) {
              DropdownMenuItem(
                text = { Text("WhatsApp Copilot", fontSize = 12.sp, color = SoftSage, fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftSage, modifier = Modifier.size(14.dp))
                },
                onClick = {
                  menuExpanded = false
                  onNegotiateClick(subscription)
                }
              )

              DropdownMenuItem(
                text = { Text("1-Click Cancel", fontSize = 12.sp, color = SoftRose) },
                leadingIcon = {
                  Icon(Icons.Default.Close, contentDescription = null, tint = SoftRose, modifier = Modifier.size(14.dp))
                },
                onClick = {
                  menuExpanded = false
                  onCancelClick(subscription.id)
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Merchant Name (Clean single-line)
      Text(
        text = subscription.merchantName,
        color = if (isCancelled) TextMuted else TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      // Category / Last Used
      Text(
        text = if (isZombie) "Inactive ${subscription.daysSinceLastUse}d" else subscription.category,
        color = if (isZombie) SoftRose else TextMuted,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Price & Status Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isCancelled) "₹0" else "₹${"%.2f".format(subscription.currentCost)}",
            color = if (isCancelled) TextMuted else TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (subscription.billingCadence == BillingCadence.MONTHLY) "/mo" else "/yr",
            color = TextMuted,
            fontSize = 9.sp
          )
        }

        // Status Indicator
        if (isCancelled) {
          Column(horizontalAlignment = Alignment.End) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SoftSageBg)
                .border(0.5.dp, SoftSageBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text("SAVED", color = SoftSage, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
            if (subscription.gdprWipeRequested) {
              Spacer(modifier = Modifier.height(2.dp))
              Text("GDPR WIPE", color = Color(0xFF6D28D9), fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else if (subscription.isVirtualCardFrozen) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFEFF6FF))
              .border(0.5.dp, Color(0xFF93C5FD), RoundedCornerShape(4.dp))
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text("FROZEN", color = Color(0xFF2563EB), fontSize = 8.sp, fontWeight = FontWeight.Bold)
          }
        } else if (isZombie) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SoftRoseBg)
              .border(0.5.dp, SoftRoseBorder, RoundedCornerShape(4.dp))
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text("ZOMBIE", color = SoftRose, fontSize = 8.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(SoftSage)
          )
        }
      }

      if (!isCancelled) {
        Spacer(modifier = Modifier.height(8.dp))
        NeumorphicButton(
          onClick = { onNegotiateClick(subscription) },
          shape = RoundedCornerShape(8.dp),
          backgroundColor = Color(0xFFF8FAFC),
          elevation = 2.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(26.dp),
          testTag = "btn_card_copilot_${subscription.id}"
        ) {
          Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SoftSage, modifier = Modifier.size(10.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Copilot",
            color = TextPrimary,
            fontSize = 9.sp,
            fontFamily = com.example.ui.theme.OutfitFontFamily,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun HorizontalSubscriptionRowItem(
  subscription: Subscription,
  onCancelClick: (String) -> Unit,
  onNegotiateClick: (Subscription) -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }
  val isCancelled = subscription.status == SubscriptionStatus.CANCELLED
  val isZombie = subscription.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION
  val isNegotiated = subscription.status == SubscriptionStatus.NEGOTIATED_SAVED

  val borderColor = when {
    isCancelled -> WhiteBorderSubtle
    isZombie -> SoftRoseBorder
    isNegotiated -> SoftSageBorder
    else -> WhiteBorder
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(WhiteSurface)
      .border(1.dp, borderColor, RoundedCornerShape(10.dp))
      .clickable { onNegotiateClick(subscription) }
      .padding(horizontal = 12.dp, vertical = 9.dp)
      .testTag("row_sub_${subscription.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        BrandIcon(
          name = subscription.merchantName,
          size = 36.dp,
          iconSize = 18.dp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = subscription.merchantName,
            color = if (isCancelled) TextMuted else TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = subscription.category,
              color = TextMuted,
              fontSize = 10.sp
            )
            if (isZombie) {
              Text(
                text = " • Inactive ${subscription.daysSinceLastUse}d",
                color = SoftRose,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (isCancelled) "₹0" else "₹${"%.2f".format(subscription.currentCost)}",
            color = if (isCancelled) TextMuted else TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (subscription.billingCadence == BillingCadence.MONTHLY) "/mo" else "/yr",
            color = TextMuted,
            fontSize = 9.sp
          )
        }

        if (isCancelled) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SoftSageBg)
              .border(0.5.dp, SoftSageBorder, RoundedCornerShape(4.dp))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text("SAVED", color = SoftSage, fontSize = 8.sp, fontWeight = FontWeight.Bold)
          }
        } else if (isZombie) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SoftRoseBg)
              .border(0.5.dp, SoftRoseBorder, RoundedCornerShape(4.dp))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text("ZOMBIE", color = SoftRose, fontSize = 8.sp, fontWeight = FontWeight.Bold)
          }
        }

        Box {
          NeumorphicIconButton(
            icon = Icons.Default.MoreVert,
            onClick = { menuExpanded = true },
            size = 28.dp,
            iconSize = 15.dp,
            elevation = 2.dp
          )

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
              .background(WhiteSurface)
              .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
          ) {
            if (!isCancelled) {
              DropdownMenuItem(
                text = { Text("AI Negotiate Price", fontSize = 12.sp, color = SoftAmber) },
                leadingIcon = {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftAmber, modifier = Modifier.size(14.dp))
                },
                onClick = {
                  menuExpanded = false
                  onNegotiateClick(subscription)
                }
              )

              DropdownMenuItem(
                text = { Text("1-Click Cancel", fontSize = 12.sp, color = SoftRose) },
                leadingIcon = {
                  Icon(Icons.Default.Close, contentDescription = null, tint = SoftRose, modifier = Modifier.size(14.dp))
                },
                onClick = {
                  menuExpanded = false
                  onCancelClick(subscription.id)
                }
              )
            }
          }
        }
      }
    }
  }
}

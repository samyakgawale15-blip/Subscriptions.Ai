package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteBorderSubtle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Shield
import com.example.ui.theme.WhiteSurface

@Composable
fun SubscriptionItemCard(
  subscription: Subscription,
  onCancelClick: (String) -> Unit,
  onNegotiateClick: (Subscription) -> Unit,
  modifier: Modifier = Modifier,
  onToggleVirtualCard: (String) -> Unit = {},
  onToggleGdprWipe: (String) -> Unit = {}
) {
  var menuExpanded by remember { mutableStateOf(false) }
  var detailsExpanded by remember { mutableStateOf(false) }

  val isCancelled = subscription.status == SubscriptionStatus.CANCELLED
  val isNegotiated = subscription.status == SubscriptionStatus.NEGOTIATED_SAVED
  val isZombie = subscription.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION

  val cardBg = if (isCancelled) WhiteBackground.copy(alpha = 0.5f) else WhiteSurface
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
      .background(cardBg)
      .border(1.dp, borderColor, RoundedCornerShape(10.dp))
      .animateContentSize()
      .clickable { detailsExpanded = !detailsExpanded }
      .padding(horizontal = 12.dp, vertical = 9.dp)
      .testTag("subscription_card_${subscription.id}")
  ) {
    Column {
      // Main Compact Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left: Vendor Icon & Mobile Single-Line Info
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          BrandIcon(
            name = subscription.merchantName,
            size = 36.dp,
            iconSize = 17.dp
          )

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f, fill = false)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = subscription.merchantName,
                color = if (isCancelled) TextMuted else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
              )

              if (isZombie && !isCancelled) {
                Spacer(modifier = Modifier.width(6.dp))
                SoftPill("Zombie", SoftRose, SoftRoseBg)
              } else if (isNegotiated) {
                Spacer(modifier = Modifier.width(6.dp))
                SoftPill("Saved", SoftSage, SoftSageBg)
              }
            }

            Spacer(modifier = Modifier.height(1.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = subscription.category,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (subscription.daysSinceLastUse > 0 && !isCancelled) {
                Text(
                  text = " • ${subscription.daysSinceLastUse}d idle",
                  color = if (subscription.daysSinceLastUse > 30) SoftRose else TextMuted,
                  fontSize = 10.sp
                )
              }
            }

            // Virtual Card & Privacy Badges
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(top = 3.dp)
            ) {
              VirtualCardTogglePill(
                isFrozen = subscription.isVirtualCardFrozen,
                onToggle = { onToggleVirtualCard(subscription.id) }
              )

              if (isCancelled || subscription.gdprWipeRequested) {
                GdprWipeBadge(
                  onClick = { onToggleGdprWipe(subscription.id) }
                )
              }

              if (subscription.recommendedAction == com.example.model.RecommendedAction.AI_NEGOTIATE && !isCancelled) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SoftSageBg)
                    .border(0.5.dp, SoftSageBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.5.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.Chat,
                      contentDescription = null,
                      tint = SoftSage,
                      modifier = Modifier.size(8.5.dp)
                    )
                    Text("WhatsApp Bot", color = SoftSage, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              val daysLeft = remember(subscription.renewalDate) { com.example.data.SupabaseService.calculateDaysRemaining(subscription.renewalDate) }
              if (daysLeft != null && daysLeft <= 7 && !isCancelled) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (daysLeft <= 3) Color(0xFFFEF2F2) else Color(0xFFFFFBEB))
                    .border(0.5.dp, if (daysLeft <= 3) Color(0xFFFECACA) else Color(0xFFFDE68A), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.5.dp, vertical = 1.5.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.NotificationsActive,
                      contentDescription = null,
                      tint = if (daysLeft <= 3) SoftRose else SoftAmber,
                      modifier = Modifier.size(8.5.dp)
                    )
                    Text(
                      text = "Renews: ${daysLeft}d",
                      color = if (daysLeft <= 3) SoftRose else SoftAmber,
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right: Pricing & Dropdown Menu Anchor
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.End
        ) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = if (isCancelled) "₹0" else "₹${"%.2f".format(subscription.currentCost)}",
              color = if (isCancelled) TextMuted else if (isNegotiated) SoftSage else TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (subscription.billingCadence == BillingCadence.MONTHLY) "/mo" else "/yr",
              color = TextMuted,
              fontSize = 9.sp
            )
          }

          // Options Menu Button ("•••")
          Box {
            IconButton(
              onClick = { menuExpanded = true },
              modifier = Modifier
                .size(32.dp)
                .testTag("btn_menu_${subscription.id}")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
              )
            }

            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false },
              modifier = Modifier
                .background(WhiteSurface)
                .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
            ) {
              DropdownMenuItem(
                text = {
                  Text(
                    text = if (subscription.isVirtualCardFrozen) "Unfreeze Virtual Card" else "Freeze Virtual Card",
                    fontSize = 12.sp,
                    color = if (subscription.isVirtualCardFrozen) SoftSage else Color(0xFF2563EB)
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = if (subscription.isVirtualCardFrozen) Icons.Default.CreditCard else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (subscription.isVirtualCardFrozen) SoftSage else Color(0xFF2563EB),
                    modifier = Modifier.size(15.dp)
                  )
                },
                onClick = {
                  menuExpanded = false
                  onToggleVirtualCard(subscription.id)
                }
              )

              if (!isCancelled) {
                DropdownMenuItem(
                  text = { Text("AI Negotiate Rate", fontSize = 12.sp, color = SoftAmber) },
                  leadingIcon = {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftAmber, modifier = Modifier.size(15.dp))
                  },
                  onClick = {
                    menuExpanded = false
                    onNegotiateClick(subscription)
                  }
                )

                DropdownMenuItem(
                  text = { Text("1-Click Cancel", fontSize = 12.sp, color = SoftRose) },
                  leadingIcon = {
                    Icon(Icons.Default.Close, contentDescription = null, tint = SoftRose, modifier = Modifier.size(15.dp))
                  },
                  onClick = {
                    menuExpanded = false
                    onCancelClick(subscription.id)
                  }
                )
              }

              if (isCancelled) {
                DropdownMenuItem(
                  text = {
                    Text(
                      text = if (subscription.gdprWipeRequested) "GDPR Erasure Active" else "Dispatch GDPR Data Wipe",
                      fontSize = 12.sp,
                      color = Color(0xFF6D28D9)
                    )
                  },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Shield,
                      contentDescription = null,
                      tint = Color(0xFF6D28D9),
                      modifier = Modifier.size(15.dp)
                    )
                  },
                  onClick = {
                    menuExpanded = false
                    onToggleGdprWipe(subscription.id)
                  }
                )
              }

              DropdownMenuItem(
                text = { Text("Toggle Details", fontSize = 12.sp, color = TextSecondary) },
                leadingIcon = {
                  Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(15.dp))
                },
                onClick = {
                  menuExpanded = false
                  detailsExpanded = !detailsExpanded
                }
              )
            }
          }
        }
      }

      // Minimalist Tap-to-Expand Submenu Info
      AnimatedVisibility(visible = detailsExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorderSubtle, RoundedCornerShape(6.dp))
            .padding(8.dp)
        ) {
          if (subscription.darkPatternWarning != null && !isCancelled) {
            Text(
              text = "Trap: ${subscription.darkPatternWarning}",
              color = SoftRose,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
          }
          if (subscription.negotiationLeverage != null) {
            Text(
              text = "Leverage: ${subscription.negotiationLeverage}",
              color = TextSecondary,
              fontSize = 10.sp,
              lineHeight = 14.sp
            )
          }
          Text(
            text = "Renewal: ${subscription.renewalDate} • Target: ₹${"%.2f".format(subscription.targetNegotiatedPrice)}/mo",
            color = TextMuted,
            fontSize = 9.sp,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
fun SoftPill(text: String, color: Color, bg: Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(3.dp))
      .background(bg)
      .border(0.5.dp, color.copy(alpha = 0.25f), RoundedCornerShape(3.dp))
      .padding(horizontal = 4.dp, vertical = 1.dp)
  ) {
    Text(
      text = text,
      color = color,
      fontSize = 8.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BillingCadence
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import com.example.ui.theme.SoftAmber
import com.example.ui.theme.SoftAmberBg
import com.example.ui.theme.SoftAmberBorder
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftRoseBg
import com.example.ui.theme.SoftRoseBorder
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.SoftSky
import com.example.ui.theme.SoftSkyBg
import com.example.ui.theme.SoftSkyBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPlaceholder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteBorderSubtle
import com.example.ui.theme.WhiteSurface
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicIconButton

/**
 * Status badge styling details.
 */
data class StatusBadgeConfig(
  val label: String,
  val textColor: Color,
  val backgroundColor: Color,
  val borderColor: Color,
  val icon: ImageVector? = null
)

/**
 * A dedicated, reusable Status Badge Composable for subscriptions.
 * Displays a styled pill with text and color according to the subscription's status and classification.
 */
@Composable
fun SubscriptionStatusBadge(
  subscription: Subscription,
  modifier: Modifier = Modifier
) {
  val config = when (subscription.status) {
    SubscriptionStatus.CANCELLED -> StatusBadgeConfig(
      label = "Cancelled",
      textColor = TextMuted,
      backgroundColor = WhiteBorderSubtle,
      borderColor = WhiteBorder,
      icon = Icons.Default.Close
    )
    SubscriptionStatus.CANCELLING_OPTIMISTIC -> StatusBadgeConfig(
      label = "Cancelling",
      textColor = SoftRose,
      backgroundColor = SoftRoseBg,
      borderColor = SoftRoseBorder,
      icon = Icons.Default.Close
    )
    SubscriptionStatus.NEGOTIATING -> StatusBadgeConfig(
      label = "Negotiating",
      textColor = SoftAmber,
      backgroundColor = SoftAmberBg,
      borderColor = SoftAmberBorder,
      icon = Icons.Default.AutoAwesome
    )
    SubscriptionStatus.NEGOTIATED_SAVED -> StatusBadgeConfig(
      label = "Negotiated",
      textColor = SoftSage,
      backgroundColor = SoftSageBg,
      borderColor = SoftSageBorder,
      icon = Icons.Default.CheckCircle
    )
    SubscriptionStatus.ACTIVE -> {
      if (subscription.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION) {
        StatusBadgeConfig(
          label = "Zombie",
          textColor = SoftRose,
          backgroundColor = SoftRoseBg,
          borderColor = SoftRoseBorder,
          icon = Icons.Default.Warning
        )
      } else {
        StatusBadgeConfig(
          label = "Active",
          textColor = SoftSage,
          backgroundColor = SoftSageBg,
          borderColor = SoftSageBorder,
          icon = Icons.Default.CheckCircle
        )
      }
    }
  }

  SubscriptionStatusBadge(
    label = config.label,
    textColor = config.textColor,
    backgroundColor = config.backgroundColor,
    borderColor = config.borderColor,
    icon = config.icon,
    modifier = modifier.testTag("status_badge_${subscription.id}")
  )
}

/**
 * Standalone Status Badge Composable that accepts custom labels and styling.
 */
@Composable
fun SubscriptionStatusBadge(
  label: String,
  textColor: Color,
  backgroundColor: Color,
  borderColor: Color,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(backgroundColor)
      .border(0.75.dp, borderColor, RoundedCornerShape(6.dp))
      .padding(horizontal = 7.dp, vertical = 3.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(3.5.dp)
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = textColor,
          modifier = Modifier.size(10.dp)
        )
      } else {
        Box(
          modifier = Modifier
            .size(5.dp)
            .clip(CircleShape)
            .background(textColor)
        )
      }
      Text(
        text = label,
        color = textColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.2.sp
      )
    }
  }
}

/**
 * Virtual Card Active / Frozen Control Pill.
 * Allows users to toggle single-use card controls next to subscriptions.
 */
@Composable
fun VirtualCardTogglePill(
  isFrozen: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isFrozen) Color(0xFFEFF6FF) else Color(0xFFF0FDF4)
  val contentColor = if (isFrozen) Color(0xFF1D4ED8) else Color(0xFF15803D)
  val label = if (isFrozen) "Card: Frozen" else "Card: Active"

  NeumorphicButton(
    onClick = onToggle,
    shape = RoundedCornerShape(7.dp),
    backgroundColor = bgColor,
    elevation = 2.dp,
    modifier = modifier.height(23.dp),
    testTag = "virtual_card_toggle"
  ) {
    Icon(
      imageVector = if (isFrozen) Icons.Default.Lock else Icons.Default.CreditCard,
      contentDescription = label,
      tint = contentColor,
      modifier = Modifier.size(9.5.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = label,
      color = contentColor,
      fontSize = 8.5.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.2.sp
    )
  }
}

/**
 * GDPR Data Wipe Requested Badge for canceled subscriptions.
 * Highlights Article 17 Right to Erasure request status.
 */
@Composable
fun GdprWipeBadge(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null
) {
  NeumorphicButton(
    onClick = { onClick?.invoke() },
    shape = RoundedCornerShape(7.dp),
    backgroundColor = Color(0xFFF5F3FF),
    elevation = 2.dp,
    modifier = modifier.height(23.dp),
    testTag = "gdpr_wipe_badge"
  ) {
    Icon(
      imageVector = Icons.Default.Shield,
      contentDescription = "GDPR Data Wipe Requested",
      tint = Color(0xFF6D28D9),
      modifier = Modifier.size(9.5.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = "GDPR Wipe",
      color = Color(0xFF6D28D9),
      fontSize = 8.5.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.2.sp
    )
  }
}

/**
 * Individual subscription list item displaying:
 * - Brand logo & Subscription Name
 * - Current Cost (with cadence e.g. /mo)
 * - Status Badge
 * - Virtual Card Active/Frozen Controls
 * - GDPR Data Wipe Requested badge (on canceled subs)
 * - Category / metadata
 * - Optional action menu ("•••")
 */
@Composable
fun SubscriptionListItem(
  subscription: Subscription,
  modifier: Modifier = Modifier,
  onItemClick: ((Subscription) -> Unit)? = null,
  onCancelClick: ((String) -> Unit)? = null,
  onNegotiateClick: ((Subscription) -> Unit)? = null,
  onToggleVirtualCard: ((String) -> Unit)? = null,
  onToggleGdprWipe: ((String) -> Unit)? = null
) {
  var menuExpanded by remember { mutableStateOf(false) }

  val isCancelled = subscription.status == SubscriptionStatus.CANCELLED
  val isZombie = subscription.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION && !isCancelled
  val isNegotiated = subscription.status == SubscriptionStatus.NEGOTIATED_SAVED

  val cardBg = if (isCancelled) WhiteBackground else WhiteSurface
  val cardBorderColor = when {
    isCancelled -> WhiteBorderSubtle
    isZombie -> SoftRoseBorder
    isNegotiated -> SoftSageBorder
    else -> WhiteBorder
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 56.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(cardBg)
      .border(1.dp, cardBorderColor, RoundedCornerShape(10.dp))
      .clickable(enabled = onItemClick != null) {
        onItemClick?.invoke(subscription)
      }
      .testTag("subscription_item_${subscription.id}"),
    color = cardBg,
    shape = RoundedCornerShape(10.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Section: Brand Icon + Subscription Name + Category / Secondary Info
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        BrandIcon(
          name = subscription.merchantName,
          size = 38.dp,
          iconSize = 18.dp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f, fill = false)) {
          // Name and Status Badge row
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = subscription.merchantName,
              color = if (isCancelled) TextMuted else TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              textDecoration = if (isCancelled) TextDecoration.LineThrough else null,
              modifier = Modifier
                .weight(1f, fill = false)
                .testTag("subscription_name_${subscription.id}")
            )

            SubscriptionStatusBadge(
              subscription = subscription
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          // Subtitle row (Category & cadence or idle duration)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = subscription.category,
              color = TextMuted,
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            if (isZombie && subscription.daysSinceLastUse > 0) {
              Text(
                text = "• ${subscription.daysSinceLastUse}d inactive",
                color = SoftRose,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            } else if (!isCancelled && subscription.renewalDate.isNotBlank()) {
              Text(
                text = "• Renews ${subscription.renewalDate}",
                color = TextPlaceholder,
                fontSize = 10.sp
              )
            }
          }

          // Differentiation Controls Row: Virtual Card Active/Frozen & Privacy Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(top = 4.dp)
          ) {
            VirtualCardTogglePill(
              isFrozen = subscription.isVirtualCardFrozen,
              onToggle = { onToggleVirtualCard?.invoke(subscription.id) }
            )

            if (isCancelled || subscription.gdprWipeRequested) {
              GdprWipeBadge(
                onClick = { onToggleGdprWipe?.invoke(subscription.id) }
              )
            }

            if (subscription.recommendedAction == com.example.model.RecommendedAction.AI_NEGOTIATE && !isCancelled) {
              NeumorphicButton(
                onClick = { onNegotiateClick?.invoke(subscription) },
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color(0xFFF0FDF4),
                elevation = 2.dp,
                modifier = Modifier.height(24.dp),
                testTag = "btn_copilot_item_${subscription.id}"
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Chat,
                  contentDescription = null,
                  tint = SoftSage,
                  modifier = Modifier.size(9.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "Copilot",
                  color = SoftSage,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            val daysLeft = remember(subscription.renewalDate) { com.example.data.SupabaseService.calculateDaysRemaining(subscription.renewalDate) }
            if (daysLeft != null && daysLeft <= 7 && !isCancelled) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(5.dp))
                  .background(if (daysLeft <= 3) Color(0xFFFEF2F2) else Color(0xFFFFFBEB))
                  .border(0.5.dp, if (daysLeft <= 3) Color(0xFFFECACA) else Color(0xFFFDE68A), RoundedCornerShape(5.dp))
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = if (daysLeft <= 3) SoftRose else SoftAmber,
                    modifier = Modifier.size(9.dp)
                  )
                  Text(
                    text = "Renews: ${daysLeft}d",
                    color = if (daysLeft <= 3) SoftRose else SoftAmber,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Right Section: Current Cost + Options Menu
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
      ) {
        Column(
          horizontalAlignment = Alignment.End,
          modifier = Modifier.testTag("subscription_cost_${subscription.id}")
        ) {
          Text(
            text = if (isCancelled) "₹0.00" else "₹${"%.2f".format(subscription.currentCost)}",
            color = when {
              isCancelled -> TextMuted
              isNegotiated -> SoftSage
              isZombie -> SoftRose
              else -> TextPrimary
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = if (isCancelled) TextDecoration.LineThrough else null
          )
          Text(
            text = if (subscription.billingCadence == BillingCadence.MONTHLY) "/mo" else "/yr",
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Action Menu (Cancel / Negotiate / Virtual Card / GDPR)
        Box {
          NeumorphicIconButton(
            icon = Icons.Default.MoreVert,
            onClick = { menuExpanded = true },
            size = 30.dp,
            iconSize = 16.dp,
            elevation = 2.dp,
            testTag = "action_menu_${subscription.id}"
          )

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
                  color = if (subscription.isVirtualCardFrozen) SoftSage else Color(0xFF2563EB),
                  fontWeight = FontWeight.Medium
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = if (subscription.isVirtualCardFrozen) Icons.Default.CreditCard else Icons.Default.Lock,
                  contentDescription = null,
                  tint = if (subscription.isVirtualCardFrozen) SoftSage else Color(0xFF2563EB),
                  modifier = Modifier.size(14.dp)
                )
              },
              onClick = {
                menuExpanded = false
                onToggleVirtualCard?.invoke(subscription.id)
              }
            )

            if (onNegotiateClick != null && !isCancelled) {
              DropdownMenuItem(
                text = {
                  Text(
                    text = "AI Negotiate Rate",
                    fontSize = 12.sp,
                    color = SoftAmber,
                    fontWeight = FontWeight.Medium
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SoftAmber,
                    modifier = Modifier.size(14.dp)
                  )
                },
                onClick = {
                  menuExpanded = false
                  onNegotiateClick(subscription)
                }
              )
            }

            if (onCancelClick != null && !isCancelled) {
              DropdownMenuItem(
                text = {
                  Text(
                    text = "1-Click Cancel",
                    fontSize = 12.sp,
                    color = SoftRose,
                    fontWeight = FontWeight.Medium
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = SoftRose,
                    modifier = Modifier.size(14.dp)
                  )
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
                    color = Color(0xFF6D28D9),
                    fontWeight = FontWeight.Medium
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF6D28D9),
                    modifier = Modifier.size(14.dp)
                  )
                },
                onClick = {
                  menuExpanded = false
                  onToggleGdprWipe?.invoke(subscription.id)
                }
              )
            }
          }
        }
      }
    }
  }
}

/**
 * A Composable UI component that lists user subscriptions, displaying:
 * - Subscription name
 * - Current cost
 * - Status badge
 *
 * This version uses a LazyColumn for standalone scrollable views.
 */
@Composable
fun UserSubscriptionsList(
  subscriptions: List<Subscription>,
  modifier: Modifier = Modifier,
  title: String? = null,
  subtitle: String? = null,
  contentPadding: PaddingValues = PaddingValues(16.dp),
  onSubscriptionClick: ((Subscription) -> Unit)? = null,
  onCancelClick: ((String) -> Unit)? = null,
  onNegotiateClick: ((Subscription) -> Unit)? = null,
  onToggleVirtualCard: ((String) -> Unit)? = null,
  onToggleGdprWipe: ((String) -> Unit)? = null
) {
  val totalMonthlyCost = subscriptions
    .filter { it.status != SubscriptionStatus.CANCELLED }
    .sumOf {
      if (it.billingCadence == BillingCadence.MONTHLY) it.currentCost else it.currentCost / 12.0
    }

  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .testTag("user_subscriptions_list"),
    contentPadding = contentPadding,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Optional Header
    if (title != null || subtitle != null) {
      item(key = "header_section") {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            if (title != null) {
              Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
            if (subtitle != null) {
              Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
              )
            }
          }

          // Summary Chip with active count & total spend
          if (subscriptions.isNotEmpty()) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(WhiteBorderSubtle)
                .border(0.5.dp, WhiteBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "${subscriptions.size} subs • ₹${"%.0f".format(totalMonthlyCost)}/mo",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }

    // Empty state
    if (subscriptions.isEmpty()) {
      item(key = "empty_state") {
        EmptySubscriptionsState()
      }
    } else {
      items(
        items = subscriptions,
        key = { it.id }
      ) { subscription ->
        SubscriptionListItem(
          subscription = subscription,
          onItemClick = onSubscriptionClick,
          onCancelClick = onCancelClick,
          onNegotiateClick = onNegotiateClick,
          onToggleVirtualCard = onToggleVirtualCard,
          onToggleGdprWipe = onToggleGdprWipe
        )
      }
    }
  }
}

/**
 * Non-scrollable Column variant of the subscription list, suitable for embedding
 * inside existing scrollable parents (such as an existing LazyColumn).
 */
@Composable
fun UserSubscriptionsColumn(
  subscriptions: List<Subscription>,
  modifier: Modifier = Modifier,
  title: String? = null,
  subtitle: String? = null,
  onSubscriptionClick: ((Subscription) -> Unit)? = null,
  onCancelClick: ((String) -> Unit)? = null,
  onNegotiateClick: ((Subscription) -> Unit)? = null,
  onToggleVirtualCard: ((String) -> Unit)? = null,
  onToggleGdprWipe: ((String) -> Unit)? = null
) {
  val totalMonthlyCost = subscriptions
    .filter { it.status != SubscriptionStatus.CANCELLED }
    .sumOf {
      if (it.billingCadence == BillingCadence.MONTHLY) it.currentCost else it.currentCost / 12.0
    }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("user_subscriptions_column"),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    if (title != null || subtitle != null) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          if (title != null) {
            Text(
              text = title,
              color = TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
          if (subtitle != null) {
            Text(
              text = subtitle,
              color = TextMuted,
              fontSize = 11.sp
            )
          }
        }

        if (subscriptions.isNotEmpty()) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(WhiteBorderSubtle)
              .border(0.5.dp, WhiteBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "${subscriptions.size} subs • ₹${"%.0f".format(totalMonthlyCost)}/mo",
              color = TextSecondary,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    if (subscriptions.isEmpty()) {
      EmptySubscriptionsState()
    } else {
      subscriptions.forEach { subscription ->
        SubscriptionListItem(
          subscription = subscription,
          onItemClick = onSubscriptionClick,
          onCancelClick = onCancelClick,
          onNegotiateClick = onNegotiateClick,
          onToggleVirtualCard = onToggleVirtualCard,
          onToggleGdprWipe = onToggleGdprWipe
        )
      }
    }
  }
}

/**
 * Empty state Composable when no user subscriptions are present.
 */
@Composable
private fun EmptySubscriptionsState(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(WhiteSurface)
      .border(1.dp, WhiteBorder, RoundedCornerShape(10.dp))
      .padding(24.dp)
      .testTag("empty_subscriptions_state"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(WhiteBorderSubtle),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CreditCard,
          contentDescription = null,
          tint = TextMuted,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "No Subscriptions Found",
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Connect Plaid or sync Supabase to discover subscriptions.",
        color = TextMuted,
        fontSize = 11.sp
      )
    }
  }
}

/**
 * Alias Composable for standard naming preferences.
 */
@Composable
fun SubscriptionList(
  subscriptions: List<Subscription>,
  modifier: Modifier = Modifier,
  title: String? = null,
  subtitle: String? = null,
  contentPadding: PaddingValues = PaddingValues(16.dp),
  onSubscriptionClick: ((Subscription) -> Unit)? = null,
  onCancelClick: ((String) -> Unit)? = null,
  onNegotiateClick: ((Subscription) -> Unit)? = null
) {
  UserSubscriptionsList(
    subscriptions = subscriptions,
    modifier = modifier,
    title = title,
    subtitle = subtitle,
    contentPadding = contentPadding,
    onSubscriptionClick = onSubscriptionClick,
    onCancelClick = onCancelClick,
    onNegotiateClick = onNegotiateClick
  )
}

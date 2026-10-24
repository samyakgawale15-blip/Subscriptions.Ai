package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RecommendedAction
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import com.example.ui.components.HorizontalSubscriptionCard
import com.example.ui.components.HorizontalSubscriptionRowItem
import com.example.ui.components.HorizontalSubscriptionSection
import com.example.ui.components.NeumorphicFilterPill
import com.example.ui.components.SubscriptionListItem
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftRoseBg
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
fun ConsumerProScreen(
  subscriptions: List<Subscription>,
  onCancelSub: (String) -> Unit,
  onNegotiateSub: (Subscription) -> Unit,
  modifier: Modifier = Modifier,
  onToggleVirtualCard: (String) -> Unit = {},
  onToggleGdprWipe: (String) -> Unit = {},
  onOpenSmsPipeline: () -> Unit = {}
) {
  var selectedFilter by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }

  val filteredSubs = subscriptions.filter { sub ->
    val matchesSearch = sub.merchantName.contains(searchQuery, ignoreCase = true) ||
                        sub.category.contains(searchQuery, ignoreCase = true)
    val matchesFilter = when (selectedFilter) {
      "Zombies" -> sub.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION && sub.status != SubscriptionStatus.CANCELLED
      "Active" -> sub.classification == SubscriptionClassification.ACTIVE_UTILITY
      "Negotiable" -> sub.recommendedAction == RecommendedAction.AI_NEGOTIATE && sub.status != SubscriptionStatus.CANCELLED
      "Cancelled" -> sub.status == SubscriptionStatus.CANCELLED
      else -> true
    }
    matchesSearch && matchesFilter
  }

  val activeZombiesCount = subscriptions.count {
    it.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION && it.status != SubscriptionStatus.CANCELLED
  }
  val zombieLeakageMonthly = subscriptions
    .filter { it.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION && it.status != SubscriptionStatus.CANCELLED }
    .sumOf { it.currentCost }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(WhiteBackground),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Single-Line Protection Header (White Mode)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(WhiteSurface)
          .border(1.dp, WhiteBorder, RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = SoftSage,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Consumer Pro (₹299/mo)",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFEFF6FF))
              .border(0.5.dp, Color(0xFFBAE6FD), RoundedCornerShape(4.dp))
              .clickable { onOpenSmsPipeline() }
              .padding(horizontal = 6.dp, vertical = 2.5.dp)
              .testTag("btn_open_upi_mandates_consumer")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Sms, contentDescription = null, tint = SoftSky, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("UPI Mandates", color = SoftSky, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }

          if (activeZombiesCount > 0) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SoftRoseBg)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "$activeZombiesCount zombies (₹${zombieLeakageMonthly.toInt()}/mo)",
                color = SoftRose,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Compact Squircle Search Bar
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = "Search by service name...",
              color = TextMuted,
              fontSize = 12.sp,
              fontFamily = com.example.ui.theme.PlusJakartaSansFontFamily
            )
          },
          leadingIcon = {
            Icon(
              Icons.Default.Search,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(16.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              androidx.compose.material3.IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  Icons.Default.Close,
                  contentDescription = "Clear",
                  tint = TextMuted,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WhiteSurface,
            unfocusedContainerColor = WhiteSurface,
            focusedBorderColor = SoftSage,
            unfocusedBorderColor = WhiteBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          shape = RoundedCornerShape(16.dp), // Squircle
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("input_search_subscriptions")
        )
      }
    }

    // Filter Chips Row
    item {
      val filterOptions = listOf(
        "All" to subscriptions.size,
        "Zombies" to activeZombiesCount,
        "Negotiable" to subscriptions.count { it.recommendedAction == RecommendedAction.AI_NEGOTIATE && it.status != SubscriptionStatus.CANCELLED },
        "Active" to subscriptions.count { it.classification == SubscriptionClassification.ACTIVE_UTILITY },
        "Cancelled" to subscriptions.count { it.status == SubscriptionStatus.CANCELLED }
      )

      LazyRow(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(filterOptions) { (label, count) ->
          NeumorphicFilterPill(
            selected = selectedFilter == label,
            onClick = { selectedFilter = label },
            label = label,
            count = count,
            testTag = "filter_chip_$label"
          )
        }
      }
    }

    if (searchQuery.isNotBlank() || selectedFilter != "All") {
      // Filtered view: Horizontal carousel + clean horizontal items
      if (filteredSubs.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(WhiteSurface)
              .border(1.dp, WhiteBorder, RoundedCornerShape(10.dp))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("No subscriptions matching filter.", color = TextMuted, fontSize = 11.sp)
          }
        }
      } else {
        item {
          HorizontalSubscriptionSection(
            title = if (selectedFilter == "Zombies") "Zombie Subscriptions" else "Matching Subscriptions",
            subtitle = "${filteredSubs.size} found",
            badgeText = if (selectedFilter == "Zombies") "ACTION" else "FILTERED",
            badgeColor = if (selectedFilter == "Zombies") SoftRose else SoftSage,
            badgeBg = if (selectedFilter == "Zombies") SoftRoseBg else SoftSageBg,
            subscriptions = filteredSubs,
            onCancelClick = onCancelSub,
            onNegotiateClick = onNegotiateSub
          )
        }

        item {
          Text(
            text = "SERVICES LIST",
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
        }

        items(filteredSubs, key = { "row_${it.id}" }) { sub ->
          Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            SubscriptionListItem(
              subscription = sub,
              onCancelClick = onCancelSub,
              onNegotiateClick = onNegotiateSub
            )
          }
        }
      }
    } else {
      // Default All view: Grouped neatly by horizontal category carousels + clean compact list
      val zombieList = subscriptions.filter { it.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION && it.status != SubscriptionStatus.CANCELLED }
      val negotiableList = subscriptions.filter { it.recommendedAction == RecommendedAction.AI_NEGOTIATE && it.status != SubscriptionStatus.CANCELLED && it.classification != SubscriptionClassification.ZOMBIE_SUBSCRIPTION }
      val activeList = subscriptions.filter { it.classification == SubscriptionClassification.ACTIVE_UTILITY }
      val cancelledList = subscriptions.filter { it.status == SubscriptionStatus.CANCELLED }

      if (zombieList.isNotEmpty()) {
        item {
          HorizontalSubscriptionSection(
            title = "Zombie Leakage",
            subtitle = "₹${zombieList.sumOf { it.currentCost }.toInt()}/mo wasted",
            badgeText = "HIGH PRIORITY",
            badgeColor = SoftRose,
            badgeBg = SoftRoseBg,
            subscriptions = zombieList,
            onCancelClick = onCancelSub,
            onNegotiateClick = onNegotiateSub
          )
        }
      }

      if (negotiableList.isNotEmpty()) {
        item {
          HorizontalSubscriptionSection(
            title = "AI Negotiable Plans",
            subtitle = "Lower rates detected",
            badgeText = "OPPORTUNITY",
            badgeColor = com.example.ui.theme.SoftAmber,
            badgeBg = com.example.ui.theme.SoftAmberBg,
            subscriptions = negotiableList,
            onCancelClick = onCancelSub,
            onNegotiateClick = onNegotiateSub
          )
        }
      }

      if (activeList.isNotEmpty()) {
        item {
          HorizontalSubscriptionSection(
            title = "Active Utilities",
            subtitle = "${activeList.size} recurring services",
            badgeText = "HEALTHY",
            badgeColor = SoftSage,
            badgeBg = SoftSageBg,
            subscriptions = activeList,
            onCancelClick = onCancelSub,
            onNegotiateClick = onNegotiateSub
          )
        }
      }

      if (cancelledList.isNotEmpty()) {
        item {
          HorizontalSubscriptionSection(
            title = "Reclaimed & Saved",
            subtitle = "Cancelled subscriptions",
            badgeText = "SAVED",
            badgeColor = SoftSage,
            badgeBg = SoftSageBg,
            subscriptions = cancelledList,
            onCancelClick = onCancelSub,
            onNegotiateClick = onNegotiateSub
          )
        }
      }

      item {
        Text(
          text = "ALL ACTIVE SERVICES (${subscriptions.size})",
          color = TextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }

      items(subscriptions, key = { "row_all_${it.id}" }) { sub ->
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
          SubscriptionListItem(
            subscription = sub,
            onCancelClick = onCancelSub,
            onNegotiateClick = onNegotiateSub,
            onToggleVirtualCard = onToggleVirtualCard,
            onToggleGdprWipe = onToggleGdprWipe
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

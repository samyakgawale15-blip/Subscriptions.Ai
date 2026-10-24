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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EnterpriseSeat
import com.example.model.FinancialAudit
import com.example.model.RenewalAlert
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.ui.components.BrandIcon
import com.example.ui.components.HorizontalBrandLogosBar
import com.example.ui.components.HorizontalEnterpriseCard
import com.example.ui.components.HorizontalSubscriptionSection
import com.example.ui.components.NeumorphicActionTile
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicHeroCardButton
import com.example.ui.components.StatCard
import com.example.ui.components.WasteBreakdownBar
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.SoftAmber
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftRoseBg
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.SoftSky
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteBorderSubtle
import com.example.ui.theme.WhiteSurface
import com.example.viewmodel.AppTab
import com.example.viewmodel.AuditLogEntry

@Composable
fun OverviewScreen(
  audit: FinancialAudit,
  subscriptions: List<Subscription>,
  enterpriseSeats: List<EnterpriseSeat> = emptyList(),
  auditLogs: List<AuditLogEntry>,
  onOpenDiscovery: () -> Unit,
  onOpenExtraction: () -> Unit,
  onOpenScreenshotAudit: () -> Unit,
  onCancelSub: (String) -> Unit,
  onNegotiateSub: (Subscription) -> Unit,
  onReclaimSeat: (String) -> Unit = {},
  onNavigateTab: (AppTab) -> Unit,
  modifier: Modifier = Modifier,
  onToggleVirtualCard: (String) -> Unit = {},
  onToggleGdprWipe: (String) -> Unit = {},
  onOpenCloudMessaging: () -> Unit = {},
  onOpenSmsPipeline: () -> Unit = {},
  renewalAlerts: List<RenewalAlert> = emptyList()
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredSubs = remember(subscriptions, searchQuery) {
    if (searchQuery.isBlank()) subscriptions
    else subscriptions.filter {
      it.merchantName.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }
  }

  val zombieSubs = filteredSubs.filter { it.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION }
  val activeSubs = filteredSubs.filter { it.classification == SubscriptionClassification.ACTIVE_UTILITY }

  // Subscriptions approaching renewal in <= 7 days
  val approachingRenewals = remember(subscriptions) {
    subscriptions.filter { sub ->
      val days = com.example.data.SupabaseService.calculateDaysRemaining(sub.renewalDate)
      days != null && days in 0..7
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(WhiteBackground),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Shorter Squircle Search Bar (Minimalist Aesthetic)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = "Search services (e.g. Jio, Netflix)...",
              color = TextMuted,
              fontSize = 12.sp,
              fontFamily = PlusJakartaSansFontFamily
            )
          },
          leadingIcon = {
            Icon(
              Icons.Default.Search,
              contentDescription = "Search",
              tint = TextMuted,
              modifier = Modifier.size(16.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  Icons.Default.Clear,
                  contentDescription = "Clear search",
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
          shape = RoundedCornerShape(16.dp), // Refined Squircle
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("input_search_overview")
        )
      }
    }

    // 2. Upcoming Renewal Defense (Clean & Minimalist, No raw SQL or dev tags)
    if (approachingRenewals.isNotEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(WhiteSurface)
            .border(1.dp, WhiteBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag("card_renewal_defense")
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftSageBg)
                    .border(1.dp, SoftSageBorder, RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = SoftSage,
                    modifier = Modifier.size(15.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Renewal Date Alerts",
                    color = TextPrimary,
                    fontSize = 12.5.sp,
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${approachingRenewals.size} subscriptions due in ≤ 7 days",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = PlusJakartaSansFontFamily
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(SoftSageBg)
                  .clickable { onOpenCloudMessaging() }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "View All ➔",
                  color = SoftSage,
                  fontSize = 10.sp,
                  fontFamily = OutfitFontFamily,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              approachingRenewals.take(2).forEach { sub ->
                val days = com.example.data.SupabaseService.calculateDaysRemaining(sub.renewalDate) ?: 3
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WhiteBackground)
                    .border(0.75.dp, WhiteBorderSubtle, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    BrandIcon(name = sub.merchantName, size = 24.dp, iconSize = 12.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = sub.merchantName,
                        color = TextPrimary,
                        fontSize = 11.5.sp,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "Renews: ${sub.renewalDate} • ₹${"%.2f".format(sub.currentCost)}/mo",
                        color = TextMuted,
                        fontSize = 9.5.sp,
                        fontFamily = PlusJakartaSansFontFamily
                      )
                    }
                  }

                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (days <= 3) Color(0xFFFEF2F2) else Color(0xFFFFFBEB))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "In $days days",
                        color = if (days <= 3) SoftRose else SoftAmber,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    NeumorphicButton(
                      onClick = { onNegotiateSub(sub) },
                      shape = RoundedCornerShape(8.dp),
                      backgroundColor = Color(0xFFF8FAFC),
                      elevation = 3.dp,
                      testTag = "btn_copilot_renewal_${sub.id}"
                    ) {
                      Icon(
                        Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = SoftSage,
                        modifier = Modifier.size(10.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "Copilot",
                        color = TextPrimary,
                        fontSize = 9.sp,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // 3. Horizontal Brand Logos Row (Instant tap to launch WhatsApp Copilot)
    item {
      HorizontalBrandLogosBar(
        subscriptions = subscriptions,
        onSelectSubscription = { onNegotiateSub(it) }
      )
    }

    // 4. PROMINENT AUDIT ACTIONS: Bill Screenshot & UPI AutoPay Mandates
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        NeumorphicHeroCardButton(
          title = "Audit Screenshot",
          subtitle = "Dates & rate detection",
          badgeText = "Audit ➔",
          icon = Icons.Default.AddPhotoAlternate,
          onClick = onOpenScreenshotAudit,
          modifier = Modifier.weight(1f),
          testTag = "btn_upload_screenshot_middle"
        )

        NeumorphicHeroCardButton(
          title = "UPI Mandates",
          subtitle = "AutoPay & bank SMS",
          badgeText = "UPI ➔",
          icon = Icons.Default.Sms,
          onClick = onOpenSmsPipeline,
          modifier = Modifier.weight(1f),
          testTag = "btn_open_upi_mandates_overview"
        )
      }
    }

    // 5. Metric Stat Cards (Minimalist 2x2 Grid)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatCard(
            title = "Monthly Spend",
            value = "₹${audit.totalMonthlySpend.toInt()}",
            subtitle = "${subscriptions.size} Total Services",
            accentColor = TextSecondary,
            icon = Icons.Default.CreditCard,
            badgeText = "Verified",
            badgeColor = SoftSage,
            badgeBgColor = SoftSageBg,
            modifier = Modifier.weight(1f),
            testTag = "stat_monthly_spend"
          )

          StatCard(
            title = "Zombie Leakage",
            value = "₹${audit.identifiedWaste.toInt()}",
            subtitle = "${audit.wastePercentage.toInt()}% Spend Leakage",
            accentColor = SoftRose,
            icon = Icons.Default.DeleteOutline,
            badgeText = "${zombieSubs.size} Flagged",
            badgeColor = SoftRose,
            badgeBgColor = SoftRoseBg,
            modifier = Modifier.weight(1f),
            testTag = "stat_identified_waste"
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatCard(
            title = "Saved YTD",
            value = "₹${audit.realizedSavingsYtd.toInt()}",
            subtitle = "ARR Protected",
            accentColor = SoftSage,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            badgeText = "+ARR",
            badgeColor = SoftSage,
            badgeBgColor = SoftSageBg,
            modifier = Modifier.weight(1f),
            testTag = "stat_realized_savings"
          )

          StatCard(
            title = "Idle Seats",
            value = "${audit.idleSeatsCount}",
            subtitle = "Inactive users",
            accentColor = SoftSky,
            icon = Icons.Default.Group,
            badgeText = "SaaS",
            badgeColor = SoftSky,
            badgeBgColor = Color(0x153B82F6),
            modifier = Modifier.weight(1f),
            testTag = "stat_idle_seats"
          )
        }
      }
    }

    // 6. Compact Waste Breakdown Bar
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        WasteBreakdownBar(
          totalSpend = audit.totalMonthlySpend,
          wasteAmount = audit.identifiedWaste,
          wastePercentage = audit.wastePercentage,
          zombieAmount = audit.identifiedWaste * 0.42,
          idleSeatsAmount = audit.identifiedWaste * 0.58
        )
      }
    }

    // 7. Horizontal Subscriptions Row: Active Utilities
    item {
      HorizontalSubscriptionSection(
        title = "Active Subscriptions",
        subtitle = "${activeSubs.size} services",
        badgeText = "HEALTHY",
        badgeColor = SoftSage,
        badgeBg = SoftSageBg,
        subscriptions = activeSubs,
        onCancelClick = onCancelSub,
        onNegotiateClick = onNegotiateSub
      )
    }

    // 8. Horizontal Subscriptions Row: Zombie Leakage
    if (zombieSubs.isNotEmpty()) {
      item {
        HorizontalSubscriptionSection(
          title = "Zombie Subscriptions",
          subtitle = "₹${audit.identifiedWaste.toInt()}/mo reclaimable",
          badgeText = "TAKE ACTION",
          badgeColor = SoftRose,
          badgeBg = SoftRoseBg,
          subscriptions = zombieSubs,
          onCancelClick = onCancelSub,
          onNegotiateClick = onNegotiateSub
        )
      }
    }

    // 9. Horizontal Enterprise SaaS Row
    if (enterpriseSeats.isNotEmpty()) {
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Enterprise SaaS",
                color = TextPrimary,
                fontSize = 14.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(com.example.ui.theme.SoftSkyBg)
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "SSO TRACKED",
                  color = com.example.ui.theme.SoftSky,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Text(
              text = "${enterpriseSeats.size} apps",
              color = TextMuted,
              fontSize = 11.sp,
              fontFamily = PlusJakartaSansFontFamily
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          androidx.compose.foundation.lazy.LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(enterpriseSeats, key = { "overview_ent_${it.id}" }) { seat ->
              HorizontalEnterpriseCard(
                seat = seat,
                onReclaimClick = onReclaimSeat
              )
            }
          }
        }
      }
    }

    // 10. Quick Action Neumorphic Tiles
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        NeumorphicActionTile(
          title = "Plaid Feeds",
          icon = Icons.Default.ElectricBolt,
          tint = SoftSky,
          onClick = onOpenDiscovery,
          modifier = Modifier.weight(1f),
          testTag = "tile_plaid_feeds"
        )

        NeumorphicActionTile(
          title = "AI Extract",
          icon = Icons.Default.DataArray,
          tint = TextSecondary,
          onClick = onOpenExtraction,
          modifier = Modifier.weight(1f),
          testTag = "tile_ai_extract"
        )

        NeumorphicActionTile(
          title = "Audit Bill",
          icon = Icons.Default.DocumentScanner,
          tint = SoftSage,
          onClick = onOpenScreenshotAudit,
          modifier = Modifier.weight(1f),
          testTag = "tile_audit_bill"
        )
      }
    }

    // 11. Activity Feed (Minimalist Clean Feed)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(WhiteSurface)
          .border(1.dp, WhiteBorder, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "AUDIT ACTIVITY",
            color = TextMuted,
            fontSize = 9.5.sp,
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "Live",
            color = SoftSage,
            fontSize = 9.5.sp,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        auditLogs.take(3).forEach { log ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = log.title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = log.timestamp,
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = PlusJakartaSansFontFamily
              )
            }
            if (log.amountDelta > 0) {
              Text(
                text = "+₹${log.amountDelta.toInt()}/yr",
                color = SoftSage,
                fontSize = 11.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

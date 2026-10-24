package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EnterpriseSeat
import com.example.ui.components.BrandIcon
import com.example.ui.components.EnterpriseSeatCard
import com.example.ui.components.HorizontalEnterpriseCard
import com.example.ui.components.NeumorphicButton
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun EnterpriseScreen(
  enterpriseSeats: List<EnterpriseSeat>,
  onReclaimSeat: (String) -> Unit,
  onReclaimAllSeats: () -> Unit,
  modifier: Modifier = Modifier
) {
  val unReclaimedSeats = enterpriseSeats.filter { !it.isReclaimed }
  val totalIdleSeats = unReclaimedSeats.sumOf { it.idleSeats }
  val totalMonthlyWaste = unReclaimedSeats.sumOf { it.monthlyWaste }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(WhiteBackground),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Single-Line Enterprise Action Header (White Mode)
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
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = SoftSky,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "SSO License Reclaimer",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        if (totalIdleSeats > 0) {
          NeumorphicButton(
            onClick = onReclaimAllSeats,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFFFF1F2), // Soft Rose Neumorphic
            elevation = 3.dp,
            modifier = Modifier.height(30.dp),
            testTag = "btn_reclaim_all_seats"
          ) {
            Icon(Icons.Default.GroupRemove, contentDescription = null, tint = SoftRose, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Reclaim All (${totalIdleSeats})",
              color = SoftRose,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SoftSageBg)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text("All Reclaimed", color = SoftSage, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Enterprise Differentiation: Team Sentiment Radial Chart & SSO Login Utilization Metric
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        EnterpriseSentimentAndSsoCard()
      }
    }

    // Horizontal Enterprise SaaS Carousel
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ENTERPRISE SAAS SEATS",
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "${enterpriseSeats.size} Apps",
            color = TextMuted,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(enterpriseSeats, key = { "h_seat_${it.id}" }) { seat ->
            HorizontalEnterpriseCard(
              seat = seat,
              onReclaimClick = onReclaimSeat
            )
          }
        }
      }
    }

    // SSO Sync Status Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        SSOPill("Okta SSO", true)
        SSOPill("Google Workspace", true)
        SSOPill("Azure AD", true)
      }
    }

    // List of Enterprise App Seats with Brand Icons
    items(enterpriseSeats, key = { it.id }) { seat ->
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        EnterpriseSeatCard(
          seat = seat,
          onReclaimClick = onReclaimSeat
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun SSOPill(name: String, isSynced: Boolean) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(WhiteSurface)
      .border(1.dp, WhiteBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = "• $name",
      color = if (isSynced) SoftSky else TextMuted,
      fontSize = 9.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

/**
 * Enterprise Differentiation Component:
 * Displays a "Team Sentiment" radial chart (85% Positive) alongside the SSO Login utilization metric.
 */
@Composable
fun EnterpriseSentimentAndSsoCard(
  modifier: Modifier = Modifier,
  sentimentScore: Float = 0.85f,
  ssoUtilizationScore: Float = 0.72f,
  totalProvisionedSeats: Int = 478,
  activeLoginSeats: Int = 344,
  dormantSeatsCount: Int = 90
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(WhiteSurface)
      .border(1.dp, WhiteBorder, RoundedCornerShape(12.dp))
      .padding(14.dp)
      .testTag("enterprise_sentiment_sso_card")
  ) {
    Column {
      // Card Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.SentimentSatisfiedAlt,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Workforce Sentiment & SSO Signals",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SoftSkyBg)
            .border(0.5.dp, SoftSkyBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "TELEMETRY AUDIT",
            color = SoftSky,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Dual Metrics: Team Sentiment Radial Chart on Left + SSO Utilization on Right
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Column: Team Sentiment Radial Gauge
        Row(
          modifier = Modifier.weight(1.1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          TeamSentimentRadialGauge(
            percentage = sentimentScore,
            modifier = Modifier.testTag("sentiment_radial_chart")
          )

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Team Sentiment",
              color = TextPrimary,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SoftSageBg)
                .border(0.5.dp, SoftSageBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.5.dp)
            ) {
              Text(
                text = "${(sentimentScore * 100).toInt()}% Positive",
                color = SoftSage,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "142 employee surveys via Slack & Workday",
              color = TextMuted,
              fontSize = 9.sp,
              lineHeight = 12.sp
            )
          }
        }

        // Vertical Divider
        Box(
          modifier = Modifier
            .width(1.dp)
            .height(64.dp)
            .background(WhiteBorder)
        )

        // Right Column: SSO Login Utilization Metric
        Column(
          modifier = Modifier.weight(0.9f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = null,
                tint = SoftSky,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "SSO Utilization",
                color = TextPrimary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = "${(ssoUtilizationScore * 100).toInt()}%",
              color = SoftSky,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Linear Progress Bar for SSO Utilization
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(Color(0xFFE2E8F0))
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(ssoUtilizationScore)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF0284C7))
            )
          }

          Spacer(modifier = Modifier.height(5.dp))

          Text(
            text = "$activeLoginSeats of $totalProvisionedSeats active",
            color = TextSecondary,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Medium
          )

          Text(
            text = "$dormantSeatsCount dormant licenses flagged",
            color = SoftRose,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

/**
 * Custom Canvas Radial Progress Gauge for Team Sentiment.
 */
@Composable
fun TeamSentimentRadialGauge(
  percentage: Float = 0.85f,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.size(68.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(68.dp)) {
      val strokeWidth = 7.dp.toPx()
      val radius = (size.minDimension - strokeWidth) / 2
      val centerOffset = Offset(size.width / 2, size.height / 2)

      // Background track ring
      drawCircle(
        color = Color(0xFFE2E8F0),
        radius = radius,
        center = centerOffset,
        style = Stroke(width = strokeWidth)
      )

      // Foreground percentage sweep arc
      drawArc(
        color = Color(0xFF10B981), // Emerald / Sage
        startAngle = -90f,
        sweepAngle = 360f * percentage,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = "${(percentage * 100).toInt()}%",
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
      )
      Text(
        text = "Positive",
        color = Color(0xFF10B981),
        fontSize = 8.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

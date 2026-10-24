package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface
import com.example.viewmodel.AppTab

/**
 * Premium Neumorphic Bottom Navigation Bar.
 * Features an elevated center "Audit Bill Screenshot" button anchored in the middle.
 */
@Composable
fun NeumorphicBottomBar(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  onOpenScreenshotAudit: () -> Unit,
  onOpenAlerts: () -> Unit,
  alertsCount: Int = 0,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    // Background Navigation Bar with subtle dual shadow
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(
          elevation = 8.dp,
          shape = RoundedCornerShape(22.dp),
          spotColor = Color(0x180F172A),
          ambientColor = Color(0x120F172A)
        )
        .clip(RoundedCornerShape(22.dp))
        .background(
          Brush.verticalGradient(
            listOf(Color.White, Color(0xFFF8FAFC))
          )
        )
        .border(
          width = 1.2.dp,
          brush = Brush.verticalGradient(
            listOf(Color.White, Color(0xFFE2E8F0))
          ),
          shape = RoundedCornerShape(22.dp)
        )
        .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tab 1: Overview
        BottomBarTabItem(
          label = "Overview",
          icon = Icons.Default.GridView,
          isSelected = currentTab == AppTab.OVERVIEW,
          onClick = { onTabSelected(AppTab.OVERVIEW) },
          testTag = "nav_tab_overview",
          modifier = Modifier.weight(1f)
        )

        // Tab 2: Consumer Pro
        BottomBarTabItem(
          label = "Consumer",
          icon = Icons.Default.Shield,
          isSelected = currentTab == AppTab.CONSUMER_PRO,
          onClick = { onTabSelected(AppTab.CONSUMER_PRO) },
          testTag = "nav_tab_consumer",
          modifier = Modifier.weight(1f)
        )

        // Center Space Placeholder for the Elevated Button
        Spacer(modifier = Modifier.size(54.dp))

        // Tab 3: Enterprise
        BottomBarTabItem(
          label = "Enterprise",
          icon = Icons.Default.Business,
          isSelected = currentTab == AppTab.ENTERPRISE,
          onClick = { onTabSelected(AppTab.ENTERPRISE) },
          testTag = "nav_tab_enterprise",
          modifier = Modifier.weight(1f)
        )

        // Tab 4: Renewal Alerts
        BottomBarTabItem(
          label = "Alerts",
          icon = Icons.Default.NotificationsActive,
          isSelected = false,
          onClick = onOpenAlerts,
          badgeCount = alertsCount,
          testTag = "nav_tab_alerts",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Centered Elevated Neumorphic "Upload Screenshot to Audit" Button
    val centerInteraction = remember { MutableInteractionSource() }
    val isCenterPressed by centerInteraction.collectIsPressedAsState()
    val centerScale by animateFloatAsState(
      targetValue = if (isCenterPressed) 0.94f else 1f,
      animationSpec = tween(durationMillis = 100),
      label = "center_scale"
    )

    Box(
      modifier = Modifier
        .align(Alignment.Center)
        .offset(y = (-6).dp)
        .scale(centerScale)
        .shadow(
          elevation = if (isCenterPressed) 3.dp else 9.dp,
          shape = RoundedCornerShape(18.dp),
          spotColor = Color(0x350F172A),
          ambientColor = Color(0x200F172A)
        )
        .clip(RoundedCornerShape(18.dp))
        .background(
          Brush.linearGradient(
            listOf(Color(0xFF22C55E), Color(0xFF15803D)) // Emerald Neumorphic Gradient
          )
        )
        .border(
          width = 1.2.dp,
          brush = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.45f), Color.Black.copy(alpha = 0.2f))
          ),
          shape = RoundedCornerShape(18.dp)
        )
        .clickable(
          interactionSource = centerInteraction,
          indication = null,
          onClick = onOpenScreenshotAudit
        )
        .padding(horizontal = 13.dp, vertical = 9.dp)
        .testTag("btn_center_upload_screenshot"),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.AddPhotoAlternate,
          contentDescription = "Upload Screenshot",
          tint = Color.White,
          modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = "Audit",
          color = Color.White,
          fontSize = 11.5.sp,
          fontFamily = OutfitFontFamily,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.2.sp
        )
      }
    }
  }
}

@Composable
private fun BottomBarTabItem(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  badgeCount: Int = 0,
  testTag: String = ""
) {
  val iconColor by animateColorAsState(
    targetValue = if (isSelected) TextPrimary else TextMuted,
    label = "tab_icon_color"
  )
  val textColor by animateColorAsState(
    targetValue = if (isSelected) TextPrimary else TextMuted,
    label = "tab_text_color"
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(vertical = 4.dp)
      .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = iconColor,
          modifier = Modifier.size(19.dp)
        )

        if (badgeCount > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .offset(x = 6.dp, y = (-4).dp)
              .size(13.dp)
              .clip(CircleShape)
              .background(SoftRose)
              .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$badgeCount",
              color = Color.White,
              fontSize = 7.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = label,
        color = textColor,
        fontSize = 10.sp,
        fontFamily = if (isSelected) OutfitFontFamily else PlusJakartaSansFontFamily,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

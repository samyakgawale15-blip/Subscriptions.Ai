package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SoftSage
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface
import com.example.viewmodel.AppTab

@Composable
fun TabNavigation(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(WhiteSurface)
      .border(1.dp, WhiteBorder, RoundedCornerShape(10.dp))
      .padding(3.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      AppTab.entries.forEach { tab ->
        val isSelected = tab == currentTab
        val bgColor by animateColorAsState(
          targetValue = if (isSelected) Color(0xFFF1F5F9) else Color.Transparent,
          animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          label = "tab_bg"
        )
        val borderColor by animateColorAsState(
          targetValue = if (isSelected) Color(0xFFE2E8F0) else Color.Transparent,
          animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          label = "tab_border"
        )
        val tintColor by animateColorAsState(
          targetValue = if (isSelected) TextPrimary else TextMuted,
          label = "tab_tint"
        )

        val tabIcon = when (tab) {
          AppTab.OVERVIEW -> Icons.Default.GridView
          AppTab.CONSUMER_PRO -> Icons.Default.Shield
          AppTab.ENTERPRISE -> Icons.Default.Business
        }

        val tabLabel = when (tab) {
          AppTab.OVERVIEW -> "Overview"
          AppTab.CONSUMER_PRO -> "Consumer"
          AppTab.ENTERPRISE -> "Enterprise"
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(7.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(7.dp))
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              onTabSelected(tab)
            }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = tabIcon,
              contentDescription = null,
              tint = if (isSelected && tab == AppTab.ENTERPRISE) SoftSage else tintColor,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = tabLabel,
              color = tintColor,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
              letterSpacing = (-0.2).sp
            )
          }
        }
      }
    }
  }
}

package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun StatCard(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  badgeText: String? = null,
  badgeColor: Color = accentColor,
  badgeBgColor: Color = Color(0x153B82F6),
  testTag: String = "stat_card"
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(WhiteSurface)
      .border(1.dp, WhiteBorder, RoundedCornerShape(10.dp))
      .padding(12.dp)
      .testTag(testTag)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (icon != null) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
          }
          Text(
            text = title.uppercase(),
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
          )
        }

        if (badgeText != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(badgeBgColor)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = badgeText,
              color = badgeColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = value,
        color = TextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtitle,
        color = accentColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.1).sp
      )
    }
  }
}

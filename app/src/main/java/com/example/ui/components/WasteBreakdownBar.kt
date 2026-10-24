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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSky
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun WasteBreakdownBar(
  totalSpend: Double,
  wasteAmount: Double,
  wastePercentage: Double,
  zombieAmount: Double,
  idleSeatsAmount: Double,
  modifier: Modifier = Modifier
) {
  val activeUtilityAmount = (totalSpend - wasteAmount).coerceAtLeast(0.0)

  val utilityWeight = if (totalSpend > 0) (activeUtilityAmount / totalSpend).toFloat().coerceIn(0.05f, 0.9f) else 0.65f
  val zombieWeight = if (totalSpend > 0) (zombieAmount / totalSpend).toFloat().coerceIn(0.05f, 0.5f) else 0.15f
  val idleWeight = if (totalSpend > 0) (idleSeatsAmount / totalSpend).toFloat().coerceIn(0.05f, 0.5f) else 0.20f

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(WhiteSurface)
      .border(1.dp, WhiteBorder, RoundedCornerShape(10.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "SPEND BREAKDOWN (${wastePercentage.toInt()}% LEAKAGE)",
          color = TextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        )
        Text(
          text = "₹${wasteAmount.toInt()}/mo waste",
          color = SoftRose,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Compact Multi-segment progress bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(Color(0xFFE2E8F0))
      ) {
        Box(
          modifier = Modifier
            .weight(utilityWeight)
            .height(6.dp)
            .background(SoftSage)
        )
        Box(
          modifier = Modifier
            .weight(zombieWeight)
            .height(6.dp)
            .background(SoftRose)
        )
        Box(
          modifier = Modifier
            .weight(idleWeight)
            .height(6.dp)
            .background(SoftSky)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Minimalist Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        LegendItem(
          color = SoftSage,
          label = "Utility",
          amount = "₹${activeUtilityAmount.toInt()}"
        )
        LegendItem(
          color = SoftRose,
          label = "Zombie",
          amount = "₹${zombieAmount.toInt()}"
        )
        LegendItem(
          color = SoftSky,
          label = "Idle SSO",
          amount = "₹${idleSeatsAmount.toInt()}"
        )
      }
    }
  }
}

@Composable
private fun LegendItem(
  color: Color,
  label: String,
  amount: String
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = "$label $amount",
      color = TextMuted,
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

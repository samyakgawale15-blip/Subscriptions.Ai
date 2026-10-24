package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Subscription
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun HorizontalSubscriptionSection(
  title: String,
  subtitle: String? = null,
  badgeText: String? = null,
  badgeColor: Color = TextMuted,
  badgeBg: Color = Color.Transparent,
  subscriptions: List<Subscription>,
  onCancelClick: (String) -> Unit,
  onNegotiateClick: (Subscription) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    // Section Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = title,
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.2).sp
        )
        if (badgeText != null) {
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(badgeBg)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = badgeText,
              color = badgeColor,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      if (subtitle != null) {
        Text(
          text = subtitle,
          color = TextMuted,
          fontSize = 11.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Horizontal Scrolling Cards with Respective Logos
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(subscriptions, key = { it.id }) { sub ->
        HorizontalSubscriptionCard(
          subscription = sub,
          onCancelClick = onCancelClick,
          onNegotiateClick = onNegotiateClick
        )
      }
    }
  }
}

@Composable
fun HorizontalBrandLogosBar(
  subscriptions: List<Subscription>,
  onSelectSubscription: (Subscription) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "SERVICES • TAP FOR WHATSAPP BOT & CHATBOT REDIRECT",
      color = TextMuted,
      fontSize = 9.5.sp,
      fontFamily = com.example.ui.theme.PlusJakartaSansFontFamily,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(6.dp))

    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(subscriptions, key = { "logo_${it.id}" }) { sub ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .width(62.dp)
            .clickable { onSelectSubscription(sub) }
            .testTag("logo_pill_${sub.id}")
        ) {
          BrandIcon(
            name = sub.merchantName,
            size = 42.dp,
            iconSize = 22.dp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = sub.merchantName.split(" ").firstOrNull() ?: sub.merchantName,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

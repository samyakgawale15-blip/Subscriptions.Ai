package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// Neumorphic Color Constants (Soft UI FinTech Style)
val NeumorphicSurface = Color(0xFFF1F5F9)
val NeumorphicWhiteHighlight = Color(0xFFFFFFFF)
val NeumorphicDarkShadow = Color(0x240F172A)
val NeumorphicBorderColor = Color(0x1F0F172A)
val NeumorphicBorderHighlight = Color(0x80FFFFFF)

/**
 * Standard Neumorphic Button with realistic dual-shadow convex / concave states.
 */
@Composable
fun NeumorphicButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: CornerBasedShape = RoundedCornerShape(16.dp),
  backgroundColor: Color = Color(0xFFF8FAFC),
  elevation: Dp = 5.dp,
  enabled: Boolean = true,
  testTag: String = "",
  content: @Composable RowScope.() -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.97f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "neu_scale"
  )

  val currentElevation = if (isPressed) 1.dp else elevation
  val currentDarkShadow = if (isPressed) Color(0x100F172A) else NeumorphicDarkShadow

  val gradientBrush = if (isPressed) {
    Brush.linearGradient(
      colors = listOf(
        Color(0xFFE2E8F0),
        Color(0xFFF1F5F9)
      )
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        Color(0xFFFFFFFF),
        backgroundColor
      )
    )
  }

  Box(
    modifier = modifier
      .scale(scale)
      .shadow(
        elevation = currentElevation,
        shape = shape,
        spotColor = currentDarkShadow,
        ambientColor = currentDarkShadow
      )
      .clip(shape)
      .background(gradientBrush)
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(NeumorphicBorderHighlight, NeumorphicBorderColor)
        ),
        shape = shape
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick
      )
      .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      content = content
    )
  }
}

/**
 * High-Contrast Primary Neumorphic Button (e.g., Dark Slate or Emerald with dual soft shadows)
 */
@Composable
fun NeumorphicPrimaryButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: CornerBasedShape = RoundedCornerShape(16.dp),
  primaryColor: Color = TextPrimary,
  contentColor: Color = Color.White,
  elevation: Dp = 6.dp,
  enabled: Boolean = true,
  testTag: String = "",
  content: @Composable RowScope.() -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.97f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "neu_primary_scale"
  )

  val currentElevation = if (isPressed) 2.dp else elevation
  val currentShadow = if (isPressed) Color(0x20000000) else Color(0x350F172A)

  val gradientBrush = if (isPressed) {
    Brush.verticalGradient(
      colors = listOf(
        primaryColor.copy(alpha = 0.92f),
        primaryColor
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        primaryColor.copy(alpha = 0.88f),
        primaryColor
      )
    )
  }

  Box(
    modifier = modifier
      .scale(scale)
      .shadow(
        elevation = currentElevation,
        shape = shape,
        spotColor = currentShadow,
        ambientColor = currentShadow
      )
      .clip(shape)
      .background(gradientBrush)
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(Color.White.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.2f))
        ),
        shape = shape
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick
      )
      .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      content = content
    )
  }
}

/**
 * Large Prominent Neumorphic Card Button (Hero action for Screenshot Audit)
 */
@Composable
fun NeumorphicHeroCardButton(
  title: String,
  subtitle: String,
  badgeText: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.98f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "hero_neu_scale"
  )

  val elevation = if (isPressed) 2.dp else 7.dp
  val shadowColor = if (isPressed) Color(0x150F172A) else Color(0x220F172A)

  val gradientBrush = if (isPressed) {
    Brush.linearGradient(
      colors = listOf(Color(0xFFEDF2F7), Color(0xFFF8FAFC))
    )
  } else {
    Brush.linearGradient(
      colors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9))
    )
  }

  Box(
    modifier = modifier
      .scale(scale)
      .shadow(
        elevation = elevation,
        shape = RoundedCornerShape(20.dp),
        spotColor = shadowColor,
        ambientColor = shadowColor
      )
      .clip(RoundedCornerShape(20.dp))
      .background(gradientBrush)
      .border(
        width = 1.2.dp,
        brush = Brush.verticalGradient(
          colors = listOf(Color.White, Color(0xFFE2E8F0))
        ),
        shape = RoundedCornerShape(20.dp)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        // Embossed Icon Squircle
        Box(
          modifier = Modifier
            .size(44.dp)
            .shadow(
              elevation = 3.dp,
              shape = RoundedCornerShape(13.dp),
              spotColor = Color(0x18000000),
              ambientColor = Color(0x18000000)
            )
            .clip(RoundedCornerShape(13.dp))
            .background(
              Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0)))
            )
            .border(1.dp, Color.White, RoundedCornerShape(13.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SoftSage,
            modifier = Modifier.size(23.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = subtitle,
            color = TextMuted,
            fontSize = 10.5.sp,
            fontFamily = PlusJakartaSansFontFamily
          )
        }
      }

      // Neumorphic Pill CTA
      Box(
        modifier = Modifier
          .shadow(
            elevation = 2.dp,
            shape = RoundedCornerShape(10.dp),
            spotColor = Color(0x20000000),
            ambientColor = Color(0x20000000)
          )
          .clip(RoundedCornerShape(10.dp))
          .background(TextPrimary)
          .padding(horizontal = 11.dp, vertical = 6.dp)
      ) {
        Text(
          text = badgeText,
          color = Color.White,
          fontSize = 10.5.sp,
          fontFamily = OutfitFontFamily,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * Compact Action Tile in Neumorphic Style
 */
@Composable
fun NeumorphicActionTile(
  title: String,
  icon: ImageVector,
  tint: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.96f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "tile_scale"
  )

  val elevation = if (isPressed) 1.dp else 4.dp
  val shadowColor = if (isPressed) Color(0x100F172A) else Color(0x1E0F172A)

  val bg = if (isPressed) {
    Brush.verticalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFEDF2F7)))
  } else {
    Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9)))
  }

  Box(
    modifier = modifier
      .scale(scale)
      .shadow(
        elevation = elevation,
        shape = RoundedCornerShape(12.dp),
        spotColor = shadowColor,
        ambientColor = shadowColor
      )
      .clip(RoundedCornerShape(12.dp))
      .background(bg)
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFE2E8F0))),
        shape = RoundedCornerShape(12.dp)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(vertical = 11.dp, horizontal = 8.dp)
      .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(15.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 11.sp,
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

/**
 * Neumorphic Filter Pill with tactile convex (unselected) and concave/inset (selected) states.
 */
@Composable
fun NeumorphicFilterPill(
  selected: Boolean,
  onClick: () -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  count: Int? = null,
  testTag: String = ""
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "pill_scale"
  )

  val elevation = if (selected || isPressed) 1.dp else 4.dp
  val shadowColor = if (selected || isPressed) Color(0x100F172A) else Color(0x280F172A)

  val bg = if (selected) {
    Brush.linearGradient(
      listOf(Color(0xFFE2E8F0), Color(0xFFF1F5F9)) // Concave sunken look
    )
  } else {
    Brush.linearGradient(
      listOf(Color(0xFFFFFFFF), Color(0xFFEDF2F7)) // Convex extruded look
    )
  }

  val borderColor = if (selected) {
    Brush.linearGradient(listOf(Color(0xFF94A3B8), Color(0xFFFFFFFF)))
  } else {
    Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFCBD5E1)))
  }

  Box(
    modifier = modifier
      .scale(scale)
      .shadow(
        elevation = elevation,
        shape = RoundedCornerShape(12.dp),
        spotColor = shadowColor,
        ambientColor = shadowColor
      )
      .clip(RoundedCornerShape(12.dp))
      .background(bg)
      .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (selected) {
        Box(
          modifier = Modifier
            .size(5.dp)
            .clip(CircleShape)
            .background(SoftSage)
        )
        Spacer(modifier = Modifier.width(5.dp))
      }
      Text(
        text = if (count != null) "$label ($count)" else label,
        color = if (selected) TextPrimary else TextSecondary,
        fontSize = 10.5.sp,
        fontFamily = OutfitFontFamily,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

/**
 * Neumorphic Icon Button (Round or Squircle extruded touch target)
 */
@Composable
fun NeumorphicIconButton(
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
  tint: Color = TextSecondary,
  size: Dp = 38.dp,
  iconSize: Dp = 18.dp,
  shape: CornerBasedShape = RoundedCornerShape(12.dp),
  elevation: Dp = 4.dp,
  testTag: String = ""
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1f,
    animationSpec = tween(durationMillis = 100),
    label = "icon_btn_scale"
  )

  val currentElevation = if (isPressed) 1.dp else elevation
  val shadowColor = if (isPressed) Color(0x100F172A) else Color(0x250F172A)

  val bg = if (isPressed) {
    Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFF1F5F9)))
  } else {
    Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9)))
  }

  val borderBrush = if (isPressed) {
    Brush.linearGradient(listOf(Color(0xFF94A3B8), Color(0xFFFFFFFF)))
  } else {
    Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFCBD5E1)))
  }

  Box(
    modifier = modifier
      .size(size)
      .scale(scale)
      .shadow(
        elevation = currentElevation,
        shape = shape,
        spotColor = shadowColor,
        ambientColor = shadowColor
      )
      .clip(shape)
      .background(bg)
      .border(1.2.dp, borderBrush, shape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = tint,
      modifier = Modifier.size(iconSize)
    )
  }
}


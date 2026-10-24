package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.OutfitFontFamily

data class BrandMetadata(
  val domain: String,
  val primaryColor: Color,
  val lettermark: String,
  val containerBg: Color = Color.White
)

fun getBrandMetadata(name: String): BrandMetadata {
  val lower = name.lowercase().trim()
  return when {
    lower.contains("jio") -> BrandMetadata(
      domain = "jio.com",
      primaryColor = Color(0xFF0F52BA),
      lettermark = "Jio",
      containerBg = Color.White
    )
    lower.contains("airtel") -> BrandMetadata(
      domain = "airtel.in",
      primaryColor = Color(0xFFE11D48),
      lettermark = "airtel",
      containerBg = Color.White
    )
    lower.contains("adobe") -> BrandMetadata(
      domain = "adobe.com",
      primaryColor = Color(0xFFFA0F00),
      lettermark = "A",
      containerBg = Color.White
    )
    lower.contains("netflix") -> BrandMetadata(
      domain = "netflix.com",
      primaryColor = Color(0xFFE50914),
      lettermark = "N",
      containerBg = Color(0xFF141414)
    )
    lower.contains("spotify") -> BrandMetadata(
      domain = "spotify.com",
      primaryColor = Color(0xFF1DB954),
      lettermark = "S",
      containerBg = Color(0xFF121212)
    )
    lower.contains("chatgpt") || lower.contains("openai") -> BrandMetadata(
      domain = "openai.com",
      primaryColor = Color(0xFF10A37F),
      lettermark = "AI",
      containerBg = Color.White
    )
    lower.contains("cult") || lower.contains("fit") -> BrandMetadata(
      domain = "cult.fit",
      primaryColor = Color(0xFFFF3278),
      lettermark = "cult",
      containerBg = Color(0xFF141416)
    )
    lower.contains("hotstar") || lower.contains("disney") -> BrandMetadata(
      domain = "hotstar.com",
      primaryColor = Color(0xFF113CCF),
      lettermark = "D+",
      containerBg = Color(0xFF040714)
    )
    lower.contains("ken") -> BrandMetadata(
      domain = "the-ken.com",
      primaryColor = Color(0xFF1E293B),
      lettermark = "Ken",
      containerBg = Color.White
    )
    lower.contains("swiggy") -> BrandMetadata(
      domain = "swiggy.com",
      primaryColor = Color(0xFFFC8019),
      lettermark = "S",
      containerBg = Color.White
    )
    lower.contains("zomato") -> BrandMetadata(
      domain = "zomato.com",
      primaryColor = Color(0xFFE23744),
      lettermark = "Z",
      containerBg = Color.White
    )
    lower.contains("equinox") || lower.contains("fitness") -> BrandMetadata(
      domain = "equinox.com",
      primaryColor = Color(0xFF000000),
      lettermark = "EQX",
      containerBg = Color.White
    )
    lower.contains("comcast") || lower.contains("xfinity") -> BrandMetadata(
      domain = "xfinity.com",
      primaryColor = Color(0xFFE4002B),
      lettermark = "X",
      containerBg = Color.White
    )
    lower.contains("masterclass") -> BrandMetadata(
      domain = "masterclass.com",
      primaryColor = Color(0xFF000000),
      lettermark = "M",
      containerBg = Color.White
    )
    lower.contains("chegg") -> BrandMetadata(
      domain = "chegg.com",
      primaryColor = Color(0xFFF26722),
      lettermark = "C",
      containerBg = Color.White
    )
    lower.contains("nytimes") || lower.contains("times") -> BrandMetadata(
      domain = "nytimes.com",
      primaryColor = Color(0xFF000000),
      lettermark = "T",
      containerBg = Color.White
    )
    lower.contains("audible") -> BrandMetadata(
      domain = "audible.com",
      primaryColor = Color(0xFFF8991D),
      lettermark = "a",
      containerBg = Color.White
    )
    lower.contains("whoop") -> BrandMetadata(
      domain = "whoop.com",
      primaryColor = Color(0xFF000000),
      lettermark = "W",
      containerBg = Color.White
    )
    lower.contains("duolingo") -> BrandMetadata(
      domain = "duolingo.com",
      primaryColor = Color(0xFF58CC02),
      lettermark = "D",
      containerBg = Color.White
    )
    lower.contains("midjourney") -> BrandMetadata(
      domain = "midjourney.com",
      primaryColor = Color(0xFF0F172A),
      lettermark = "MJ",
      containerBg = Color.White
    )
    lower.contains("figma") -> BrandMetadata(
      domain = "figma.com",
      primaryColor = Color(0xFFF24E1E),
      lettermark = "F",
      containerBg = Color.White
    )
    lower.contains("github") -> BrandMetadata(
      domain = "github.com",
      primaryColor = Color(0xFF24292F),
      lettermark = "GH",
      containerBg = Color.White
    )
    lower.contains("notion") -> BrandMetadata(
      domain = "notion.so",
      primaryColor = Color(0xFF000000),
      lettermark = "N",
      containerBg = Color.White
    )
    lower.contains("slack") -> BrandMetadata(
      domain = "slack.com",
      primaryColor = Color(0xFF4A154B),
      lettermark = "#",
      containerBg = Color.White
    )
    lower.contains("datadog") -> BrandMetadata(
      domain = "datadoghq.com",
      primaryColor = Color(0xFF632CA6),
      lettermark = "DD",
      containerBg = Color.White
    )
    lower.contains("salesforce") -> BrandMetadata(
      domain = "salesforce.com",
      primaryColor = Color(0xFF00A1E0),
      lettermark = "SF",
      containerBg = Color.White
    )
    lower.contains("aws") || lower.contains("amazon") -> BrandMetadata(
      domain = "aws.amazon.com",
      primaryColor = Color(0xFFFF9900),
      lettermark = "aws",
      containerBg = Color(0xFF232F3E)
    )
    lower.contains("zoom") -> BrandMetadata(
      domain = "zoom.us",
      primaryColor = Color(0xFF2D8CFF),
      lettermark = "Z",
      containerBg = Color.White
    )
    lower.contains("youtube") -> BrandMetadata(
      domain = "youtube.com",
      primaryColor = Color(0xFFFF0000),
      lettermark = "YT",
      containerBg = Color.White
    )
    lower.contains("google") || lower.contains("workspace") -> BrandMetadata(
      domain = "google.com",
      primaryColor = Color(0xFF4285F4),
      lettermark = "G",
      containerBg = Color.White
    )
    lower.contains("supabase") -> BrandMetadata(
      domain = "supabase.com",
      primaryColor = Color(0xFF3ECF8E),
      lettermark = "⚡",
      containerBg = Color(0xFF1C1C1C)
    )
    lower.contains("apple") || lower.contains("icloud") -> BrandMetadata(
      domain = "apple.com",
      primaryColor = Color(0xFF000000),
      lettermark = "",
      containerBg = Color.White
    )
    lower.contains("disney") -> BrandMetadata(
      domain = "disneyplus.com",
      primaryColor = Color(0xFF113CCF),
      lettermark = "D+",
      containerBg = Color(0xFF040714)
    )
    lower.contains("microsoft") || lower.contains("office") -> BrandMetadata(
      domain = "microsoft.com",
      primaryColor = Color(0xFF0078D4),
      lettermark = "MS",
      containerBg = Color.White
    )
    lower.contains("canva") -> BrandMetadata(
      domain = "canva.com",
      primaryColor = Color(0xFF00C4CC),
      lettermark = "C",
      containerBg = Color.White
    )
    lower.contains("dropbox") -> BrandMetadata(
      domain = "dropbox.com",
      primaryColor = Color(0xFF0061FF),
      lettermark = "DB",
      containerBg = Color.White
    )
    lower.contains("linkedin") -> BrandMetadata(
      domain = "linkedin.com",
      primaryColor = Color(0xFF0A66C2),
      lettermark = "in",
      containerBg = Color.White
    )
    lower.contains("uber") -> BrandMetadata(
      domain = "uber.com",
      primaryColor = Color(0xFF000000),
      lettermark = "Uber",
      containerBg = Color.White
    )
    else -> {
      val cleanName = lower.replace(Regex("[^a-z0-9]"), "")
      val domain = if (cleanName.isNotBlank()) "$cleanName.com" else "google.com"
      val initial = name.trim().take(2).uppercase().ifBlank { "S" }
      BrandMetadata(
        domain = domain,
        primaryColor = Color(0xFF0F172A),
        lettermark = initial,
        containerBg = Color.White
      )
    }
  }
}

/**
 * Authentic Brand Logo Component
 * Loads the real official brand logo via Google high-res 128px favicon CDN
 * with instant authentic monogram fallback. No generic mock clip-art icons.
 */
@Composable
fun BrandIcon(
  name: String,
  size: Dp = 36.dp,
  iconSize: Dp = 22.dp,
  modifier: Modifier = Modifier
) {
  val meta = getBrandMetadata(name)
  val logoUrl = "https://www.google.com/s2/favicons?domain=${meta.domain}&sz=128"

  Box(
    modifier = modifier
      .size(size)
      .shadow(
        elevation = 2.dp,
        shape = RoundedCornerShape(10.dp),
        spotColor = Color(0x180F172A),
        ambientColor = Color(0x100F172A)
      )
      .clip(RoundedCornerShape(10.dp))
      .background(meta.containerBg)
      .border(
        width = 1.dp,
        color = Color(0x1F0F172A),
        shape = RoundedCornerShape(10.dp)
      ),
    contentAlignment = Alignment.Center
  ) {
    SubcomposeAsyncImage(
      model = ImageRequest.Builder(LocalContext.current)
        .data(logoUrl)
        .crossfade(true)
        .build(),
      contentDescription = "$name logo",
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .size(iconSize)
        .padding(2.dp),
      loading = {
        BrandMonogramFallback(meta = meta, iconSize = iconSize)
      },
      error = {
        BrandMonogramFallback(meta = meta, iconSize = iconSize)
      }
    )
  }
}

@Composable
private fun BrandMonogramFallback(
  meta: BrandMetadata,
  iconSize: Dp
) {
  Box(
    modifier = Modifier.size(iconSize),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = meta.lettermark,
      color = meta.primaryColor,
      fontSize = (iconSize.value * 0.44f).sp,
      fontFamily = OutfitFontFamily,
      fontWeight = FontWeight.ExtraBold,
      maxLines = 1
    )
  }
}

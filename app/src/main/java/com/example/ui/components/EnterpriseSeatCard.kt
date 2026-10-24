package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import com.example.ui.components.NeumorphicIconButton
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
import com.example.ui.theme.WhiteBorderSubtle
import com.example.ui.theme.WhiteSurface

@Composable
fun EnterpriseSeatCard(
  seat: EnterpriseSeat,
  onReclaimClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }
  var detailsExpanded by remember { mutableStateOf(false) }

  val isReclaimed = seat.isReclaimed
  val cardBg = if (isReclaimed) WhiteBackground.copy(alpha = 0.5f) else WhiteSurface
  val borderColor = if (isReclaimed) WhiteBorderSubtle else SoftRoseBorder

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(cardBg)
      .border(1.dp, borderColor, RoundedCornerShape(10.dp))
      .animateContentSize()
      .clickable { detailsExpanded = !detailsExpanded }
      .padding(horizontal = 12.dp, vertical = 9.dp)
      .testTag("enterprise_seat_card_${seat.id}")
  ) {
    Column {
      // Main Single-Line Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App Icon + Info
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          BrandIcon(
            name = seat.appName,
            size = 36.dp,
            iconSize = 17.dp
          )

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f, fill = false)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = seat.appName,
                color = if (isReclaimed) TextMuted else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
              )

              Spacer(modifier = Modifier.width(6.dp))
              SoftPill(
                text = seat.ssoProvider.replace(" SSO", "").replace(" (Entra)", ""),
                color = SoftSky,
                bg = SoftSkyBg
              )
            }

            Spacer(modifier = Modifier.height(1.dp))

            Text(
              text = if (isReclaimed) "${seat.totalSeats} seats • Reclaimed" else "${seat.totalSeats} seats • ${seat.idleSeats} idle >30d",
              color = if (isReclaimed) SoftSage else TextMuted,
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Pricing & Dropdown Menu Anchor
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.End
        ) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = if (isReclaimed) "₹0" else "₹${seat.monthlyWaste.toInt()}",
              color = if (isReclaimed) TextMuted else SoftRose,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (isReclaimed) "reclaimed" else "waste/mo",
              color = if (isReclaimed) SoftSage else SoftRose.copy(alpha = 0.75f),
              fontSize = 9.sp
            )
          }

          // Options Menu Button ("•••")
          Box {
            NeumorphicIconButton(
              icon = Icons.Default.MoreVert,
              onClick = { menuExpanded = true },
              size = 30.dp,
              iconSize = 16.dp,
              elevation = 2.dp,
              testTag = "btn_menu_${seat.id}"
            )

            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false },
              modifier = Modifier
                .background(WhiteSurface)
                .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
            ) {
              if (!isReclaimed) {
                DropdownMenuItem(
                  text = { Text("Reclaim ${seat.idleSeats} Idle Seats", fontSize = 12.sp, color = SoftRose) },
                  leadingIcon = {
                    Icon(Icons.Default.GroupRemove, contentDescription = null, tint = SoftRose, modifier = Modifier.size(15.dp))
                  },
                  onClick = {
                    menuExpanded = false
                    onReclaimClick(seat.id)
                  }
                )
              }

              DropdownMenuItem(
                text = { Text("SSO & Compliance Info", fontSize = 12.sp, color = SoftSky) },
                leadingIcon = {
                  Icon(Icons.Default.Security, contentDescription = null, tint = SoftSky, modifier = Modifier.size(15.dp))
                },
                onClick = {
                  menuExpanded = false
                  detailsExpanded = !detailsExpanded
                }
              )
            }
          }
        }
      }

      // Minimalist Tap-to-Expand Submenu
      AnimatedVisibility(visible = detailsExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorderSubtle, RoundedCornerShape(6.dp))
            .padding(8.dp)
        ) {
          Text(
            text = "SSO: ${seat.ssoProvider} • Tier: ${seat.complianceTier}",
            color = TextPrimary,
            fontSize = 10.sp
          )
          Text(
            text = "Active: ${seat.activeSeats}/${seat.totalSeats} seats @ ₹${seat.costPerSeatMonthly.toInt()}/seat/mo",
            color = TextSecondary,
            fontSize = 10.sp
          )
          Text(
            text = "Renewal: ${seat.contractRenewalDate} • Alternative: ${seat.alternativeVendor ?: "Standard"}",
            color = TextMuted,
            fontSize = 9.sp,
            modifier = Modifier.padding(top = 1.dp)
          )
        }
      }
    }
  }
}

@Composable
fun HorizontalEnterpriseCard(
  seat: EnterpriseSeat,
  onReclaimClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }
  val isReclaimed = seat.isReclaimed
  val borderColor = if (isReclaimed) WhiteBorderSubtle else SoftRoseBorder

  Box(
    modifier = modifier
      .width(160.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(WhiteSurface)
      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
      .padding(12.dp)
      .testTag("horizontal_seat_${seat.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        BrandIcon(
          name = seat.appName,
          size = 38.dp,
          iconSize = 19.dp
        )

        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.size(26.dp)
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Options",
              tint = TextMuted,
              modifier = Modifier.size(15.dp)
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
              .background(WhiteSurface)
              .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
          ) {
            if (!isReclaimed && seat.idleSeats > 0) {
              DropdownMenuItem(
                text = { Text("Reclaim ${seat.idleSeats} Seats", fontSize = 12.sp, color = SoftRose) },
                leadingIcon = {
                  Icon(Icons.Default.GroupRemove, contentDescription = null, tint = SoftRose, modifier = Modifier.size(14.dp))
                },
                onClick = {
                  menuExpanded = false
                  onReclaimClick(seat.id)
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = seat.appName,
        color = if (isReclaimed) TextMuted else TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = if (isReclaimed) "All active" else "${seat.idleSeats} idle of ${seat.totalSeats}",
        color = if (isReclaimed) SoftSage else SoftRose,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isReclaimed) "₹0" else "₹${seat.monthlyWaste.toInt()}",
            color = if (isReclaimed) TextMuted else TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "/mo",
            color = TextMuted,
            fontSize = 9.sp
          )
        }

        if (isReclaimed) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SoftSageBg)
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text("RECLAIMED", color = SoftSage, fontSize = 8.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SoftSkyBg)
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text(seat.ssoProvider.split(" ").firstOrNull() ?: "SSO", color = SoftSky, fontSize = 8.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

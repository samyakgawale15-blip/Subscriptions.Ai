package com.example.ui.dialogs

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.NeumorphicPrimaryButton
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSky
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun SettingsDialog(
  currentApiKey: String,
  currentModel: String,
  onSaveApiKey: (String) -> Unit,
  onSelectModel: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var keyInput by remember { mutableStateOf(currentApiKey) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(16.dp))
        .background(WhiteSurface)
        .border(1.dp, WhiteBorder, RoundedCornerShape(16.dp)),
      color = WhiteSurface
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = null,
              tint = SoftSky,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AI Engine Configuration",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "GROQ LLAMA API KEY",
          color = TextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
          value = keyInput,
          onValueChange = { keyInput = it },
          placeholder = { Text("gsk_...", color = TextMuted, fontSize = 12.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WhiteBackground,
            unfocusedContainerColor = WhiteBackground,
            focusedBorderColor = SoftSky,
            unfocusedBorderColor = WhiteBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_groq_api_key")
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Optional: Runs with intelligent deterministic local engine if key is left blank.",
          color = TextSecondary,
          fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "TARGET LLAMA MODEL",
          color = TextMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        ModelSelectionRow(
          title = "llama-3.3-70b-versatile",
          desc = "Optimal reasoning & negotiation scripts",
          isSelected = currentModel == "llama-3.3-70b-versatile"
        ) {
          onSelectModel("llama-3.3-70b-versatile")
        }

        Spacer(modifier = Modifier.height(6.dp))

        ModelSelectionRow(
          title = "llama-3.1-8b-instant",
          desc = "Sub-100ms ultra-low latency inference",
          isSelected = currentModel == "llama-3.1-8b-instant"
        ) {
          onSelectModel("llama-3.1-8b-instant")
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeumorphicPrimaryButton(
          onClick = {
            onSaveApiKey(keyInput)
            onDismiss()
          },
          primaryColor = TextPrimary,
          contentColor = Color.White,
          shape = RoundedCornerShape(10.dp),
          elevation = 4.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          testTag = "btn_save_settings"
        ) {
          Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Save Configuration", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

@Composable
private fun ModelSelectionRow(
  title: String,
  desc: String,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) WhiteBackground else WhiteSurface)
      .border(1.dp, if (isSelected) SoftSky.copy(alpha = 0.6f) else WhiteBorder, RoundedCornerShape(8.dp))
      .clickable(onClick = onSelect)
      .padding(horizontal = 10.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(desc, color = TextSecondary, fontSize = 10.sp)
      }
      RadioButton(
        selected = isSelected,
        onClick = onSelect,
        colors = RadioButtonDefaults.colors(
          selectedColor = SoftSky,
          unselectedColor = TextMuted
        )
      )
    }
  }
}

package com.example.ui.dialogs

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AutoPaySignal
import com.example.model.BankCode
import com.example.model.ParsedBankSms
import com.example.ui.components.BrandIcon
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicPrimaryButton
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.SoftAmber
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSky
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun BankSmsPipelineDialog(
  smsList: List<ParsedBankSms>,
  isScanning: Boolean,
  hasSmsPermission: Boolean = false,
  selectedBankCode: BankCode?,
  selectedSignal: AutoPaySignal?,
  onSelectBank: (BankCode?) -> Unit,
  onSelectSignal: (AutoPaySignal?) -> Unit,
  onScanDeviceSms: () -> Unit,
  onLoadSampleFeed: () -> Unit,
  onParseCustomSms: (String, String) -> Unit,
  onAddSmsToTracked: (ParsedBankSms) -> Unit,
  onImportAllToTracked: () -> Unit,
  onDismiss: () -> Unit
) {
  var pasteInput by remember { mutableStateOf("") }
  var senderInput by remember { mutableStateOf("AD-HDFCBK") }
  var isPasteExpanded by remember { mutableStateOf(false) }

  // Filter list
  val filteredList = smsList.filter { item ->
    (selectedBankCode == null || item.bankCode == selectedBankCode) &&
        (selectedSignal == null || item.autopaySignal == selectedSignal)
  }

  val totalMonthlyAutopay = smsList.filter { it.autopaySignal != AutoPaySignal.REVOKED }.sumOf { it.amount }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.98f)
        .heightIn(max = 700.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(WhiteSurface)
        .border(1.dp, WhiteBorder, RoundedCornerShape(16.dp))
        .testTag("dialog_bank_sms_pipeline"),
      color = WhiteSurface
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Sms,
                contentDescription = null,
                tint = SoftSky,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "UPI AutoPay & SMS Mandates",
                color = TextPrimary,
                fontSize = 14.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Understands UPI Mandates, e-Mandates, SI & Bank Alerts",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = PlusJakartaSansFontFamily
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).testTag("btn_close_sms_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Metrics Banner
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("TOTAL DETECTED", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("${smsList.size} Mandates", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Column {
            Text("ACTIVE AUTOPAY", color = SoftSage, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("₹${"%.0f".format(totalMonthlyAutopay)}/mo", color = SoftSage, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("PIPELINE ENGINE", color = SoftSky, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("AI + NPCI Rules", color = SoftSky, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Permission Status Banner
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (hasSmsPermission) Color(0xFFF0FDF4) else Color(0xFFEFF6FF))
            .border(0.75.dp, if (hasSmsPermission) Color(0xFFBBF7D0) else Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              imageVector = Icons.Default.Sms,
              contentDescription = null,
              tint = if (hasSmsPermission) SoftSage else SoftSky,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (hasSmsPermission) "READ_SMS Permission Granted • Scanning local inbox" else "Grant READ_SMS permission to scan transaction messages",
              color = if (hasSmsPermission) SoftSage else SoftSky,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          if (!hasSmsPermission) {
            Button(
              onClick = onScanDeviceSms,
              shape = RoundedCornerShape(4.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SoftSky, contentColor = Color.White),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 1.dp),
              modifier = Modifier.height(22.dp).testTag("btn_grant_sms_permission")
            ) {
              Text("Grant Access", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Action Bar: Scan SMS Inbox, Load Sample Feed & Toggle Custom Paste
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Button(
            onClick = onScanDeviceSms,
            enabled = !isScanning,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SoftSky, contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.weight(1f).height(28.dp).testTag("btn_scan_device_sms")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(if (isScanning) "Scanning..." else "Scan Device SMS", fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onLoadSampleFeed,
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.weight(1f).height(28.dp).testTag("btn_load_sample_feed")
          ) {
            Text("Sample Feed", fontSize = 9.sp, color = TextSecondary)
          }

          OutlinedButton(
            onClick = { isPasteExpanded = !isPasteExpanded },
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.weight(1f).height(28.dp).testTag("btn_toggle_paste_sms")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(if (isPasteExpanded) "Hide" else "Paste SMS", fontSize = 9.sp, color = TextSecondary)
          }

          NeumorphicButton(
            onClick = onImportAllToTracked,
            shape = RoundedCornerShape(6.dp),
            backgroundColor = Color(0xFFF0FDF4),
            elevation = 2.dp,
            modifier = Modifier.height(28.dp).testTag("btn_import_all_mandates")
          ) {
            Icon(Icons.AutoMirrored.Filled.PlaylistAddCheck, contentDescription = null, tint = SoftSage, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Track All", fontSize = 9.sp, color = SoftSage, fontWeight = FontWeight.Bold)
          }
        }

        // Expandable Custom SMS input
        if (isPasteExpanded) {
          Spacer(modifier = Modifier.height(8.dp))
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(WhiteBackground)
              .border(0.75.dp, SoftSky.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
              .padding(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              OutlinedTextField(
                value = senderInput,
                onValueChange = { senderInput = it },
                label = { Text("Sender ID / Bank", fontSize = 9.sp) },
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(46.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = WhiteSurface,
                  unfocusedContainerColor = WhiteSurface
                )
              )

              Button(
                onClick = {
                  if (pasteInput.isNotBlank()) {
                    onParseCustomSms(pasteInput, senderInput)
                    pasteInput = ""
                    isPasteExpanded = false
                  }
                },
                enabled = pasteInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SoftSky, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(46.dp).testTag("btn_submit_parse_sms")
              ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Parse Mandate", fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
              value = pasteInput,
              onValueChange = { pasteInput = it },
              placeholder = { Text("Paste UPI AutoPay SMS, e-mandate alert, or debit notification...", fontSize = 10.sp, color = TextMuted) },
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth().height(65.dp).testTag("input_custom_mandate_sms"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WhiteSurface,
                unfocusedContainerColor = WhiteSurface
              ),
              textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bank Filter Chips Row
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            FilterBadgePill(
              label = "All Banks",
              isSelected = selectedBankCode == null,
              onClick = { onSelectBank(null) }
            )
          }
          items(BankCode.values()) { bank ->
            FilterBadgePill(
              label = bank.bankName.replace(" Bank", "").replace(" Other Indian", "Other"),
              isSelected = selectedBankCode == bank,
              onClick = { onSelectBank(if (selectedBankCode == bank) null else bank) }
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Signal Filter Chips Row
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            FilterBadgePill(
              label = "All Signals",
              isSelected = selectedSignal == null,
              onClick = { onSelectSignal(null) }
            )
          }
          items(AutoPaySignal.values()) { signal ->
            FilterBadgePill(
              label = signal.label,
              isSelected = selectedSignal == signal,
              onClick = { onSelectSignal(if (selectedSignal == signal) null else signal) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Parsed Mandates List
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(WhiteBackground)
            .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
            .padding(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (filteredList.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("No mandate messages matching current filter.", color = TextMuted, fontSize = 11.sp)
              }
            }
          } else {
            items(filteredList) { sms ->
              ParsedSmsMandateCard(
                sms = sms,
                onAddTracked = { onAddSmsToTracked(sms) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FilterBadgePill(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) SoftSky else WhiteBackground)
      .border(0.75.dp, if (isSelected) SoftSky else WhiteBorder, RoundedCornerShape(6.dp))
      .clickable { onClick() }
      .padding(horizontal = 7.dp, vertical = 3.dp)
  ) {
    Text(
      text = label,
      color = if (isSelected) Color.White else TextSecondary,
      fontSize = 9.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}

@Composable
private fun ParsedSmsMandateCard(
  sms: ParsedBankSms,
  onAddTracked: () -> Unit
) {
  val signalBg = when (sms.autopaySignal) {
    AutoPaySignal.CREATED -> Color(0xFFEFF6FF)
    AutoPaySignal.SCHEDULED -> Color(0xFFFFFBEB)
    AutoPaySignal.DEBITED -> Color(0xFFF0FDF4)
    AutoPaySignal.REVOKED -> Color(0xFFFEF2F2)
  }
  val signalText = when (sms.autopaySignal) {
    AutoPaySignal.CREATED -> Color(0xFF0284C7)
    AutoPaySignal.SCHEDULED -> SoftAmber
    AutoPaySignal.DEBITED -> SoftSage
    AutoPaySignal.REVOKED -> SoftRose
  }
  val signalBorder = when (sms.autopaySignal) {
    AutoPaySignal.CREATED -> Color(0xFFBAE6FD)
    AutoPaySignal.SCHEDULED -> Color(0xFFFDE68A)
    AutoPaySignal.DEBITED -> Color(0xFFBBF7D0)
    AutoPaySignal.REVOKED -> Color(0xFFFECACA)
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(WhiteSurface)
      .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
      .padding(8.dp)
      .testTag("sms_card_${sms.id}")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          BrandIcon(name = sms.merchant, size = 30.dp, iconSize = 15.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = sms.merchant,
                color = TextPrimary,
                fontSize = 12.sp,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.width(4.dp))
              // Mandate Type Tag
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(3.dp))
                  .background(Color(0xFFF1F5F9))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(sms.mandateType, fontSize = 7.5.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
              }
            }

            Text(
              text = "${sms.bankCode.bankName} • ${sms.date}${if (sms.accountSuffix != null) " • A/C ${sms.accountSuffix}" else ""}",
              color = TextMuted,
              fontSize = 9.5.sp
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "₹${"%.2f".format(sms.amount)}",
            color = TextPrimary,
            fontSize = 12.5.sp,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.Bold
          )

          // AutoPay signal pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(signalBg)
              .border(0.5.dp, signalBorder, RoundedCornerShape(4.dp))
              .padding(horizontal = 5.dp, vertical = 1.5.dp)
          ) {
            Text(
              text = sms.autopaySignal.label,
              color = signalText,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Raw message preview snippet
      Text(
        text = sms.rawBody,
        color = TextSecondary,
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 12.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Bottom Row: Confidence & Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(5.dp)
              .clip(CircleShape)
              .background(SoftSage)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${(sms.confidence * 100).toInt()}% Match Confidence",
            color = TextMuted,
            fontSize = 8.5.sp
          )
          if (sms.umnRef != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Ref: ${sms.umnRef}",
              color = TextMuted,
              fontSize = 8.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        if (sms.isAddedToTracked) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = SoftSage, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Tracked", color = SoftSage, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = onAddTracked,
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SoftSky,
              contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(24.dp).testTag("btn_track_sms_${sms.id}")
          ) {
            Text("+ Track as Sub", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

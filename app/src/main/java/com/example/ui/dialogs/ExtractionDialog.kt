package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SampleData
import com.example.model.AuditJsonOutput
import com.example.ui.components.BrandIcon
import com.example.ui.components.NeumorphicButton
import com.example.ui.components.NeumorphicPrimaryButton
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSky
import com.example.ui.theme.SoftSkyBg
import com.example.ui.theme.SoftSkyBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface

@Composable
fun ExtractionDialog(
  inputText: String,
  extractedResult: AuditJsonOutput?,
  isExtracting: Boolean,
  onInputTextChange: (String) -> Unit,
  onRunExtraction: () -> Unit,
  onApplyExtractedAudit: () -> Unit,
  onDismiss: () -> Unit
) {
  val clipboardManager = LocalClipboardManager.current
  var copyToast by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .heightIn(max = 700.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(WhiteSurface)
        .border(1.dp, WhiteBorder, RoundedCornerShape(16.dp)),
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
            Icon(
              imageVector = Icons.Default.DataArray,
              contentDescription = null,
              tint = SoftSky,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "AI Data Extraction (JSON Mode)",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Strict schema parsing via Groq Llama sub-second engine",
                color = TextMuted,
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Statement Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          OutlinedButton(
            onClick = { onInputTextChange(SampleData.sampleUpiMandateMessagesText) },
            shape = RoundedCornerShape(6.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.height(28.dp).weight(1.1f),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
              containerColor = Color(0xFFF0FDF4)
            )
          ) {
            Text("⚡ UPI Mandates", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = SoftSage)
          }

          OutlinedButton(
            onClick = { onInputTextChange(SampleData.sampleAuditStatementText) },
            shape = RoundedCornerShape(6.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.height(28.dp).weight(1f)
          ) {
            Text("Corporate", fontSize = 9.5.sp, color = TextSecondary)
          }

          OutlinedButton(
            onClick = {
              onInputTextChange(
                """
                  AMEX PLATINUM CONSUMER STATEMENT
                  10/01/2026 Adobe Creative Cloud ₹4230.00 (Monthly Autopay)
                  10/02/2026 Cult.fit Elite ₹1499.00 (Monthly Autopay - Idle)
                  10/03/2026 The Ken Annual ₹249.00 (Monthly Autopay)
                  10/05/2026 Netflix Premium ₹649.00 (Monthly Autopay)
                  10/06/2026 Audible Audiobooks ₹199.00 (Monthly Autopay)
                """.trimIndent()
              )
            },
            shape = RoundedCornerShape(6.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.height(28.dp).weight(1f)
          ) {
            Text("Consumer", fontSize = 9.5.sp, color = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Input statement box
        OutlinedTextField(
          value = inputText,
          onValueChange = onInputTextChange,
          label = { Text("Raw Statement / UPI Mandate SMS / Banking Alerts", color = TextMuted, fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WhiteBackground,
            unfocusedContainerColor = WhiteBackground,
            focusedBorderColor = SoftSky,
            unfocusedBorderColor = WhiteBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          textStyle = androidx.compose.ui.text.TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 15.sp
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .testTag("input_raw_statement")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Trigger Button (Neumorphic Style)
        NeumorphicPrimaryButton(
          onClick = onRunExtraction,
          enabled = !isExtracting && inputText.isNotBlank(),
          primaryColor = SoftSky,
          contentColor = Color.White,
          shape = RoundedCornerShape(10.dp),
          elevation = 4.dp,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          testTag = "btn_run_extraction"
        ) {
          if (isExtracting) {
            CircularProgressIndicator(
              color = Color.White,
              strokeWidth = 2.dp,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Extracting Subscriptions...", fontSize = 11.sp, color = Color.White)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Extract & Audit JSON Schema", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Extracted Result View
        if (extractedResult != null) {
          val audit = extractedResult.financial_audit
          Column(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
          ) {
            // Metrics Summary
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(WhiteBackground)
                .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                .padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("TOTAL SPEND", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("₹${"%.2f".format(audit.total_monthly_spend)}/mo", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
              Column {
                Text("IDENTIFIED WASTE", color = SoftRose, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("₹${"%.2f".format(audit.identified_waste)}/mo", color = SoftRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("LEAKAGE %", color = SoftRose, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${audit.waste_percentage}%", color = SoftRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Detected Subscriptions List
            LazyColumn(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(WhiteBackground)
                .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                .padding(6.dp),
              verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              items(extractedResult.detected_subscriptions) { sub ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(WhiteSurface)
                    .border(1.dp, WhiteBorder, RoundedCornerShape(6.dp))
                    .padding(8.dp)
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
                      BrandIcon(
                        name = sub.merchant_name,
                        size = 30.dp,
                        iconSize = 15.dp
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Text(
                          text = sub.merchant_name,
                          color = TextPrimary,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.SemiBold
                        )
                        Text(
                          text = "${sub.classification} • ${sub.recommended_action}",
                          color = if (sub.classification.contains("ZOMBIE") || sub.classification.contains("IDLE")) SoftRose else SoftSage,
                          fontSize = 10.sp
                        )
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = "₹${"%.2f".format(sub.current_cost)}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                      )
                      if (sub.target_negotiated_price > 0 && sub.target_negotiated_price < sub.current_cost) {
                        Text(
                          text = "Target ₹${"%.2f".format(sub.target_negotiated_price)}",
                          color = SoftSage,
                          fontSize = 10.sp
                        )
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom action
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  val rawJson = buildSchemaJsonString(extractedResult)
                  clipboardManager.setText(AnnotatedString(rawJson))
                  copyToast = true
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(36.dp)
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (copyToast) "Copied!" else "Copy JSON", fontSize = 10.sp, color = TextSecondary)
              }

              NeumorphicPrimaryButton(
                onClick = onApplyExtractedAudit,
                shape = RoundedCornerShape(8.dp),
                primaryColor = TextPrimary,
                contentColor = Color.White,
                elevation = 3.dp,
                modifier = Modifier
                  .weight(1.2f)
                  .height(36.dp),
                testTag = "btn_apply_extracted"
              ) {
                Icon(Icons.Default.FileDownloadDone, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Apply to Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      }
    }
  }
}

private fun buildSchemaJsonString(result: AuditJsonOutput): String {
  val sb = StringBuilder()
  sb.append("{\n")
  sb.append("  \"financial_audit\": {\n")
  sb.append("    \"total_monthly_spend\": ${result.financial_audit.total_monthly_spend},\n")
  sb.append("    \"identified_waste\": ${result.financial_audit.identified_waste},\n")
  sb.append("    \"waste_percentage\": ${result.financial_audit.waste_percentage}\n")
  sb.append("  },\n")
  sb.append("  \"detected_subscriptions\": [\n")
  val items = result.detected_subscriptions.map {
    """    {
      "merchant_name": "${it.merchant_name}",
      "current_cost": ${it.current_cost},
      "billing_cadence": "${it.billing_cadence}",
      "classification": "${it.classification}",
      "recommended_action": "${it.recommended_action}",
      "target_negotiated_price": ${it.target_negotiated_price},
      "negotiation_leverage": ${if (it.negotiation_leverage != null) "\"${it.negotiation_leverage}\"" else "null"}
    }"""
  }
  sb.append(items.joinToString(",\n"))
  sb.append("\n  ]\n}")
  return sb.toString()
}

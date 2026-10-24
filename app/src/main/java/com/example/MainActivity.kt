package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ui.components.NeumorphicBottomBar
import com.example.ui.components.NeumorphicIconButton
import com.example.ui.components.TabNavigation
import com.example.ui.dialogs.BankSmsPipelineDialog
import com.example.ui.dialogs.CancellationLetterDialog
import com.example.ui.dialogs.DiscoverySheet
import com.example.ui.dialogs.ExtractionDialog
import com.example.ui.dialogs.NegotiationDialog
import com.example.ui.dialogs.ScreenshotAuditDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.SupabaseDialog
import com.example.ui.dialogs.SupabaseCloudMessagingDialog
import com.example.data.SupabaseService
import com.example.ui.screens.ConsumerProScreen
import com.example.ui.screens.EnterpriseScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageBg
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.SoftSageBorder
import com.example.ui.theme.SoftSky
import com.example.ui.theme.SubscriptionsAiTheme
import com.example.ui.theme.SupabaseBg
import com.example.ui.theme.SupabaseBorder
import com.example.ui.theme.SupabaseGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface
import com.example.viewmodel.AppTab
import com.example.viewmodel.SubscriptionsViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: SubscriptionsViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SubscriptionsAiTheme {
        SubscriptionsApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun SubscriptionsApp(viewModel: SubscriptionsViewModel) {
  val currentTab by viewModel.currentTab.collectAsState()
  val audit by viewModel.financialAudit.collectAsState()
  val subscriptions by viewModel.subscriptions.collectAsState()
  val enterpriseSeats by viewModel.enterpriseSeats.collectAsState()
  val auditLogs by viewModel.auditLogs.collectAsState()

  // Discovery state
  val isDiscovering by viewModel.isDiscovering.collectAsState()
  val discoveryStatusMessage by viewModel.discoveryStatusMessage.collectAsState()
  val discoveryReport by viewModel.discoveryReport.collectAsState()
  var showDiscoveryModal by remember { mutableStateOf(false) }

  // AI Negotiation state
  val activeNegotiatingSub by viewModel.activeNegotiatingSubscription.collectAsState()
  val negotiationMessages by viewModel.negotiationMessages.collectAsState()
  val isNegotiatingAiBusy by viewModel.isNegotiatingAiBusy.collectAsState()

  // AI Extraction state
  val extractionInputText by viewModel.extractionInputText.collectAsState()
  val extractedResult by viewModel.extractedResult.collectAsState()
  val isExtracting by viewModel.isExtracting.collectAsState()
  var showExtractionModal by remember { mutableStateOf(false) }

  // Supabase state
  var showSupabaseModal by remember { mutableStateOf(false) }
  var showCloudMessagingModal by remember { mutableStateOf(false) }
  val renewalAlerts by viewModel.renewalAlerts.collectAsState()
  val alertThresholdDays by viewModel.alertThresholdDays.collectAsState()
  val autoAlertsEnabled by viewModel.autoAlertsEnabled.collectAsState()
  val isDispatchingAlert by viewModel.isDispatchingAlert.collectAsState()
  val lastDispatchedAlert by viewModel.lastDispatchedAlert.collectAsState()

  val approachingSubsCount = remember(subscriptions, alertThresholdDays) {
    SupabaseService.getSubscriptionsApproachingRenewal(subscriptions, alertThresholdDays).size
  }

  // Cancellation Preview state
  val cancellationPreviewSub by viewModel.cancellationPreviewSub.collectAsState()

  // Settings state
  val apiKeyInput by viewModel.apiKeyInput.collectAsState()
  val selectedModel by viewModel.selectedModel.collectAsState()
  var showSettingsModal by remember { mutableStateOf(false) }

  // Screenshot Bill Audit State
  val isScreenshotAuditOpen by viewModel.isScreenshotAuditOpen.collectAsState()

  // Bank SMS & UPI AutoPay Mandate Pipeline State
  val isSmsPipelineOpen by viewModel.isSmsPipelineOpen.collectAsState()
  val bankSmsList by viewModel.bankSmsList.collectAsState()
  val isSmsScanning by viewModel.isSmsScanning.collectAsState()
  val selectedBankCode by viewModel.selectedBankCode.collectAsState()
  val selectedAutoPaySignal by viewModel.selectedAutoPaySignal.collectAsState()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = WhiteBackground,
    bottomBar = {
      NeumorphicBottomBar(
        currentTab = currentTab,
        onTabSelected = { viewModel.selectTab(it) },
        onOpenScreenshotAudit = { viewModel.openScreenshotAudit() },
        onOpenAlerts = { showCloudMessagingModal = true },
        alertsCount = approachingSubsCount
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(WhiteBackground)
        .padding(innerPadding)
    ) {
      // Top Application Header (White Mode)
      TopHeader(
        realizedSavingsYtd = audit.realizedSavingsYtd,
        approachingRenewalsCount = approachingSubsCount,
        onOpenDiscovery = { showDiscoveryModal = true },
        onOpenExtraction = { showExtractionModal = true },
        onOpenScreenshotAudit = { viewModel.openScreenshotAudit() },
        onOpenCloudMessaging = { showCloudMessagingModal = true },
        onOpenSmsPipeline = { viewModel.openSmsPipeline() },
        onOpenSettings = { showSettingsModal = true }
      )

      Spacer(modifier = Modifier.height(2.dp))

      // Tab Content View
      Crossfade(
        targetState = currentTab,
        animationSpec = tween(durationMillis = 150),
        modifier = Modifier.weight(1f),
        label = "tab_crossfade"
      ) { tab ->
        when (tab) {
          AppTab.OVERVIEW -> {
            OverviewScreen(
              audit = audit,
              subscriptions = subscriptions,
              enterpriseSeats = enterpriseSeats,
              auditLogs = auditLogs,
              onOpenDiscovery = { showDiscoveryModal = true },
              onOpenExtraction = { showExtractionModal = true },
              onOpenScreenshotAudit = { viewModel.openScreenshotAudit() },
              onCancelSub = { viewModel.cancelSubscription(it) },
              onNegotiateSub = { viewModel.startNegotiation(it) },
              onReclaimSeat = { viewModel.reclaimEnterpriseSeats(it) },
              onNavigateTab = { viewModel.selectTab(it) },
              onToggleVirtualCard = { viewModel.toggleVirtualCard(it) },
              onToggleGdprWipe = { viewModel.toggleGdprWipe(it) },
              onOpenCloudMessaging = { showCloudMessagingModal = true },
              onOpenSmsPipeline = { viewModel.openSmsPipeline() },
              renewalAlerts = renewalAlerts
            )
          }

          AppTab.CONSUMER_PRO -> {
            ConsumerProScreen(
              subscriptions = subscriptions,
              onCancelSub = { viewModel.cancelSubscription(it) },
              onNegotiateSub = { viewModel.startNegotiation(it) },
              onToggleVirtualCard = { viewModel.toggleVirtualCard(it) },
              onToggleGdprWipe = { viewModel.toggleGdprWipe(it) },
              onOpenSmsPipeline = { viewModel.openSmsPipeline() }
            )
          }

          AppTab.ENTERPRISE -> {
            EnterpriseScreen(
              enterpriseSeats = enterpriseSeats,
              onReclaimSeat = { viewModel.reclaimEnterpriseSeats(it) },
              onReclaimAllSeats = { viewModel.reclaimAllIdleSeats() }
            )
          }
        }
      }
    }
  }

  // Interactive Dialogs
  if (showDiscoveryModal || isDiscovering) {
    DiscoverySheet(
      isDiscovering = isDiscovering,
      statusMessage = discoveryStatusMessage,
      report = discoveryReport,
      onTriggerScan = { viewModel.triggerDiscoveryScan() },
      onDismiss = {
        showDiscoveryModal = false
        viewModel.dismissDiscoveryReport()
      }
    )
  }

  if (activeNegotiatingSub != null) {
    NegotiationDialog(
      subscription = activeNegotiatingSub!!,
      messages = negotiationMessages,
      isAiBusy = isNegotiatingAiBusy,
      onSendMessage = { viewModel.sendNegotiationMessage(it) },
      onApplyPrice = { subId, price -> viewModel.applyNegotiatedPrice(subId, price) },
      onDismiss = { viewModel.closeNegotiation() }
    )
  }

  if (isScreenshotAuditOpen) {
    ScreenshotAuditDialog(
      onSaveSubscription = { viewModel.addAuditedSubscription(it) },
      onDismiss = { viewModel.dismissScreenshotAudit() }
    )
  }

  if (showExtractionModal) {
    ExtractionDialog(
      inputText = extractionInputText,
      extractedResult = extractedResult,
      isExtracting = isExtracting,
      onInputTextChange = { viewModel.updateExtractionInputText(it) },
      onRunExtraction = { viewModel.runExtraction() },
      onApplyExtractedAudit = {
        viewModel.applyExtractedAuditToActive()
        showExtractionModal = false
      },
      onDismiss = { showExtractionModal = false }
    )
  }

  if (showSupabaseModal) {
    SupabaseDialog(
      subscriptionsCount = subscriptions.size,
      onSyncSubscriptions = { viewModel.syncWithSupabase() },
      onPullSubscriptions = { viewModel.pullFromSupabase() },
      onAddMockSubscription = { name, cost, isZombie ->
        viewModel.addMockSubscription(
          name = name,
          cost = cost,
          classification = if (isZombie) com.example.model.SubscriptionClassification.ZOMBIE_SUBSCRIPTION else com.example.model.SubscriptionClassification.ACTIVE_UTILITY
        )
      },
      onOpenCloudMessaging = {
        showSupabaseModal = false
        showCloudMessagingModal = true
      },
      onDismiss = { showSupabaseModal = false }
    )
  }

  if (showCloudMessagingModal) {
    SupabaseCloudMessagingDialog(
      subscriptions = subscriptions,
      alerts = renewalAlerts,
      thresholdDays = alertThresholdDays,
      autoAlertsEnabled = autoAlertsEnabled,
      isDispatching = isDispatchingAlert,
      onSetThresholdDays = { viewModel.setAlertThresholdDays(it) },
      onToggleAutoAlerts = { viewModel.toggleAutoAlerts(it) },
      onTriggerCheckAndAlert = { ctx -> viewModel.triggerRenewalCheckAndAlert(ctx) },
      onSendSingleAlert = { sub, ctx -> viewModel.sendSingleSubscriptionRenewalAlert(sub, ctx) },
      onToggleVirtualCard = { viewModel.toggleVirtualCard(it) },
      onCancelSub = { viewModel.cancelSubscription(it) },
      onClearAlerts = { viewModel.clearRenewalAlerts() },
      onDismiss = { showCloudMessagingModal = false }
    )
  }

  if (isSmsPipelineOpen) {
    BankSmsPipelineDialog(
      smsList = bankSmsList,
      isScanning = isSmsScanning,
      selectedBankCode = selectedBankCode,
      selectedSignal = selectedAutoPaySignal,
      onSelectBank = { viewModel.setBankFilter(it) },
      onSelectSignal = { viewModel.setAutoPaySignalFilter(it) },
      onLoadSampleFeed = { viewModel.loadSampleBankSms() },
      onParseCustomSms = { text, sender -> viewModel.parseAndAddCustomSms(text, sender) },
      onAddSmsToTracked = { viewModel.addParsedSmsToSubscriptions(it) },
      onImportAllToTracked = { viewModel.importAllParsedSmsToSubscriptions() },
      onDismiss = { viewModel.dismissSmsPipeline() }
    )
  }

  if (cancellationPreviewSub != null) {
    CancellationLetterDialog(
      subscription = cancellationPreviewSub!!,
      onDismiss = { viewModel.dismissCancellationPreview() }
    )
  }

  if (showSettingsModal) {
    SettingsDialog(
      currentApiKey = apiKeyInput,
      currentModel = selectedModel,
      onSaveApiKey = { viewModel.updateApiKey(it) },
      onSelectModel = { viewModel.setModel(it) },
      onDismiss = { showSettingsModal = false }
    )
  }
}

@Composable
private fun TopHeader(
  realizedSavingsYtd: Double,
  approachingRenewalsCount: Int = 0,
  onOpenDiscovery: () -> Unit,
  onOpenExtraction: () -> Unit,
  onOpenScreenshotAudit: () -> Unit,
  onOpenCloudMessaging: () -> Unit = {},
  onOpenSmsPipeline: () -> Unit = {},
  onOpenSettings: () -> Unit
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Brand Logo & Title (White Mode)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(30.dp)
          .shadow(
            elevation = 3.dp,
            shape = RoundedCornerShape(8.dp),
            spotColor = Color(0x250F172A),
            ambientColor = Color(0x150F172A)
          )
          .clip(RoundedCornerShape(8.dp))
          .background(
            androidx.compose.ui.graphics.Brush.linearGradient(
              listOf(Color(0xFF0F172A), Color(0xFF1E293B))
            )
          )
          .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "S",
            color = SoftSage,
            fontSize = 15.sp,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "•",
            color = Color(0xFF3ECF8E),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Subscriptions",
          color = TextPrimary,
          fontSize = 15.sp,
          fontFamily = OutfitFontFamily,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.3).sp
        )
        Text(
          text = ".ai",
          color = SoftSage,
          fontSize = 15.sp,
          fontFamily = OutfitFontFamily,
          fontWeight = FontWeight.Bold,
          letterSpacing = (-0.3).sp
        )
      }
    }

    // Right: Savings Chip, Alerts Bell & Submenu Dropdown
    Row(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Saved ARR Chip
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(SoftSageBg)
          .border(1.dp, SoftSageBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 7.dp, vertical = 3.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(5.dp)
              .clip(CircleShape)
              .background(SoftSage)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "+₹${realizedSavingsYtd.toInt()}",
            color = SoftSage,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Renewal Alerts Bell Button with Badge
      Box {
        NeumorphicIconButton(
          icon = if (approachingRenewalsCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
          onClick = onOpenCloudMessaging,
          tint = if (approachingRenewalsCount > 0) SoftSage else TextMuted,
          size = 32.dp,
          iconSize = 16.dp,
          elevation = 2.dp,
          testTag = "btn_header_notifications"
        )

        if (approachingRenewalsCount > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .size(13.dp)
              .clip(CircleShape)
              .background(SoftRose)
              .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$approachingRenewalsCount",
              color = Color.White,
              fontSize = 7.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Minimalist Actions Menu ("•••")
      Box {
        NeumorphicIconButton(
          icon = Icons.Default.MoreVert,
          onClick = { menuExpanded = true },
          tint = TextMuted,
          size = 32.dp,
          iconSize = 16.dp,
          elevation = 2.dp,
          testTag = "btn_header_menu"
        )

        DropdownMenu(
          expanded = menuExpanded,
          onDismissRequest = { menuExpanded = false },
          modifier = Modifier
            .background(WhiteSurface)
            .border(1.dp, WhiteBorder, RoundedCornerShape(12.dp))
        ) {
          DropdownMenuItem(
            text = { Text("Audit Bill Screenshot", fontSize = 12.sp, color = SoftSage, fontWeight = FontWeight.SemiBold) },
            leadingIcon = {
              Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = SoftSage, modifier = Modifier.size(15.dp))
            },
            onClick = {
              menuExpanded = false
              onOpenScreenshotAudit()
            }
          )

          DropdownMenuItem(
            text = { Text("Renewal Date Alerts", fontSize = 12.sp, color = TextPrimary) },
            leadingIcon = {
              Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SoftSage, modifier = Modifier.size(15.dp))
            },
            onClick = {
              menuExpanded = false
              onOpenCloudMessaging()
            }
          )

          DropdownMenuItem(
            text = { Text("Scan Banking Feeds (Plaid)", fontSize = 12.sp, color = SoftSky) },
            leadingIcon = {
              Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = SoftSky, modifier = Modifier.size(15.dp))
            },
            onClick = {
              menuExpanded = false
              onOpenDiscovery()
            }
          )

          DropdownMenuItem(
            text = { Text("Extract Statement (AI JSON)", fontSize = 12.sp, color = TextPrimary) },
            leadingIcon = {
              Icon(Icons.Default.DataArray, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
            },
            onClick = {
              menuExpanded = false
              onOpenExtraction()
            }
          )

          DropdownMenuItem(
            text = { Text("UPI AutoPay & SMS Mandates", fontSize = 12.sp, color = SoftSky, fontWeight = FontWeight.SemiBold) },
            leadingIcon = {
              Icon(Icons.Default.Sms, contentDescription = null, tint = SoftSky, modifier = Modifier.size(15.dp))
            },
            onClick = {
              menuExpanded = false
              onOpenSmsPipeline()
            }
          )

          DropdownMenuItem(
            text = { Text("Settings & Engine Config", fontSize = 12.sp, color = TextSecondary) },
            leadingIcon = {
              Icon(Icons.Default.Settings, contentDescription = null, tint = TextMuted, modifier = Modifier.size(15.dp))
            },
            onClick = {
              menuExpanded = false
              onOpenSettings()
            }
          )
        }
      }
    }
  }
}

package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DiscoveryEngine
import com.example.data.GroqApiService
import com.example.data.RenewalNotificationHelper
import com.example.data.SampleData
import com.example.data.SupabaseService
import com.example.engine.BankSmsParsingPipeline
import com.example.model.AuditJsonOutput
import com.example.model.AutoPaySignal
import com.example.model.BankCode
import com.example.model.BillingCadence
import com.example.model.EnterpriseSeat
import com.example.model.FinancialAudit
import com.example.model.NegotiationMessage
import com.example.model.ParsedBankSms
import com.example.model.RecommendedAction
import com.example.model.RenewalAlert
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import com.example.model.SupabaseSyncStatus
import com.example.model.SupabaseUser
import com.example.model.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String, val subtitle: String) {
  OVERVIEW("Overview", "Executive Audit"),
  CONSUMER_PRO("Consumer Pro", "₹299/mo"),
  ENTERPRISE("Enterprise", "₹4.5L avg savings")
}

data class AuditLogEntry(
  val id: String,
  val timestamp: String,
  val title: String,
  val detail: String,
  val amountDelta: Double,
  val isPositive: Boolean = true
)

class SubscriptionsViewModel : ViewModel() {

  private val _currentTab = MutableStateFlow(AppTab.OVERVIEW)
  val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

  private val _subscriptions = MutableStateFlow<List<Subscription>>(SampleData.initialSubscriptions)
  val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

  private val _enterpriseSeats = MutableStateFlow<List<EnterpriseSeat>>(SampleData.initialEnterpriseSeats)
  val enterpriseSeats: StateFlow<List<EnterpriseSeat>> = _enterpriseSeats.asStateFlow()

  private val _transactions = MutableStateFlow<List<Transaction>>(SampleData.sampleRawTransactions)
  val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

  private val _realizedSavingsYtd = MutableStateFlow(18450.00)
  val realizedSavingsYtd: StateFlow<Double> = _realizedSavingsYtd.asStateFlow()

  private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(
    listOf(
      AuditLogEntry("log_1", "Today, 10:14 AM", "Initial Plaid Pipeline Audit", "Indexed 38 active subscriptions & enterprise seats", 0.0),
      AuditLogEntry("log_2", "Yesterday", "Automated Adobe Negotiation", "Reduced Creative Cloud from ₹4,230 to ₹2,899", 15972.0, true),
      AuditLogEntry("log_3", "3 days ago", "SSO License Cleanup", "Deprovisioned 6 idle Datadog APM hosts", 4680.0, true)
    )
  )
  val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

  // Discovery engine state
  private val _isDiscovering = MutableStateFlow(false)
  val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

  private val _discoveryStatusMessage = MutableStateFlow("")
  val discoveryStatusMessage: StateFlow<String> = _discoveryStatusMessage.asStateFlow()

  private val _discoveryReport = MutableStateFlow<DiscoveryEngine.DiscoveryReport?>(null)
  val discoveryReport: StateFlow<DiscoveryEngine.DiscoveryReport?> = _discoveryReport.asStateFlow()

  // AI Negotiation State
  private val _activeNegotiatingSubscription = MutableStateFlow<Subscription?>(null)
  val activeNegotiatingSubscription: StateFlow<Subscription?> = _activeNegotiatingSubscription.asStateFlow()

  private val _negotiationMessages = MutableStateFlow<List<NegotiationMessage>>(emptyList())
  val negotiationMessages: StateFlow<List<NegotiationMessage>> = _negotiationMessages.asStateFlow()

  private val _isNegotiatingAiBusy = MutableStateFlow(false)
  val isNegotiatingAiBusy: StateFlow<Boolean> = _isNegotiatingAiBusy.asStateFlow()

  // Screenshot Bill Audit State
  private val _isScreenshotAuditOpen = MutableStateFlow(false)
  val isScreenshotAuditOpen: StateFlow<Boolean> = _isScreenshotAuditOpen.asStateFlow()

  // Supabase Cloud Messaging (SCM) Renewal Alerts State
  private val _renewalAlerts = MutableStateFlow<List<RenewalAlert>>(SampleData.initialRenewalAlerts)
  val renewalAlerts: StateFlow<List<RenewalAlert>> = _renewalAlerts.asStateFlow()

  private val _alertThresholdDays = MutableStateFlow(7)
  val alertThresholdDays: StateFlow<Int> = _alertThresholdDays.asStateFlow()

  private val _autoAlertsEnabled = MutableStateFlow(true)
  val autoAlertsEnabled: StateFlow<Boolean> = _autoAlertsEnabled.asStateFlow()

  private val _isDispatchingAlert = MutableStateFlow(false)
  val isDispatchingAlert: StateFlow<Boolean> = _isDispatchingAlert.asStateFlow()

  private val _lastDispatchedAlert = MutableStateFlow<RenewalAlert?>(null)
  val lastDispatchedAlert: StateFlow<RenewalAlert?> = _lastDispatchedAlert.asStateFlow()

  // Supabase Cloud State
  private val _supabaseUser = MutableStateFlow<SupabaseUser?>(SupabaseService.currentUser)
  val supabaseUser: StateFlow<SupabaseUser?> = _supabaseUser.asStateFlow()

  private val _supabaseSyncStatus = MutableStateFlow(SupabaseSyncStatus.CONNECTED)
  val supabaseSyncStatus: StateFlow<SupabaseSyncStatus> = _supabaseSyncStatus.asStateFlow()

  private val _lastSupabaseSyncTime = MutableStateFlow("Just now")
  val lastSupabaseSyncTime: StateFlow<String> = _lastSupabaseSyncTime.asStateFlow()

  private val _isSupabaseSyncing = MutableStateFlow(false)
  val isSupabaseSyncing: StateFlow<Boolean> = _isSupabaseSyncing.asStateFlow()

  // AI JSON Extraction State
  private val _extractionInputText = MutableStateFlow(SampleData.sampleAuditStatementText)
  val extractionInputText: StateFlow<String> = _extractionInputText.asStateFlow()

  private val _extractedResult = MutableStateFlow<AuditJsonOutput?>(null)
  val extractedResult: StateFlow<AuditJsonOutput?> = _extractedResult.asStateFlow()

  private val _isExtracting = MutableStateFlow(false)
  val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

  // Bank SMS AutoPay Mandate Pipeline State
  private val _bankSmsList = MutableStateFlow<List<ParsedBankSms>>(BankSmsParsingPipeline.sampleBankSmsMessages)
  val bankSmsList: StateFlow<List<ParsedBankSms>> = _bankSmsList.asStateFlow()

  private val _isSmsScanning = MutableStateFlow(false)
  val isSmsScanning: StateFlow<Boolean> = _isSmsScanning.asStateFlow()

  private val _selectedBankCode = MutableStateFlow<BankCode?>(null)
  val selectedBankCode: StateFlow<BankCode?> = _selectedBankCode.asStateFlow()

  private val _selectedAutoPaySignal = MutableStateFlow<AutoPaySignal?>(null)
  val selectedAutoPaySignal: StateFlow<AutoPaySignal?> = _selectedAutoPaySignal.asStateFlow()

  private val _isSmsPipelineOpen = MutableStateFlow(false)
  val isSmsPipelineOpen: StateFlow<Boolean> = _isSmsPipelineOpen.asStateFlow()

  private val _isAiStudioPromptOpen = MutableStateFlow(false)
  val isAiStudioPromptOpen: StateFlow<Boolean> = _isAiStudioPromptOpen.asStateFlow()

  private val _customSmsInput = MutableStateFlow("")
  val customSmsInput: StateFlow<String> = _customSmsInput.asStateFlow()

  private val _customSmsSender = MutableStateFlow("AD-HDFCBK")
  val customSmsSender: StateFlow<String> = _customSmsSender.asStateFlow()

  // Cancellation Dispatch Preview
  private val _cancellationPreviewSub = MutableStateFlow<Subscription?>(null)
  val cancellationPreviewSub: StateFlow<Subscription?> = _cancellationPreviewSub.asStateFlow()

  // Settings
  private val _apiKeyInput = MutableStateFlow(GroqApiService.customApiKey ?: "")
  val apiKeyInput: StateFlow<String> = _apiKeyInput.asStateFlow()

  private val _selectedModel = MutableStateFlow(GroqApiService.selectedModel)
  val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

  // Computed Financial Audit
  val financialAudit: StateFlow<FinancialAudit> = combine(
    _subscriptions,
    _enterpriseSeats,
    _realizedSavingsYtd
  ) { subs, seats, ytdSavings ->
    val activeSubs = subs.filter { it.status != SubscriptionStatus.CANCELLED }
    val monthlySubsSpend = activeSubs.sumOf {
      if (it.billingCadence == BillingCadence.MONTHLY) it.currentCost else it.currentCost / 12.0
    }
    val monthlyEnterpriseSpend = seats.sumOf { it.totalSeats * it.costPerSeatMonthly }
    val totalMonthlySpend = monthlySubsSpend + monthlyEnterpriseSpend

    val zombieWaste = activeSubs.filter { it.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION }.sumOf {
      if (it.billingCadence == BillingCadence.MONTHLY) it.currentCost else it.currentCost / 12.0
    }
    val idleSeatsWaste = seats.filter { !it.isReclaimed }.sumOf { it.idleSeats * it.costPerSeatMonthly }
    val totalWaste = zombieWaste + idleSeatsWaste

    val wastePct = if (totalMonthlySpend > 0) (totalWaste / totalMonthlySpend) * 100.0 else 0.0

    FinancialAudit(
      totalMonthlySpend = (totalMonthlySpend * 100).toLong() / 100.0,
      identifiedWaste = (totalWaste * 100).toLong() / 100.0,
      wastePercentage = (wastePct * 10).toLong() / 10.0,
      realizedSavingsYtd = ytdSavings,
      activeSubscriptionsCount = activeSubs.size + seats.size,
      zombieCount = activeSubs.count { it.classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION },
      idleSeatsCount = seats.filter { !it.isReclaimed }.sumOf { it.idleSeats }
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    FinancialAudit(14892.40, 5212.34, 35.0, 18450.00, 38, 5, 90)
  )

  fun selectTab(tab: AppTab) {
    _currentTab.value = tab
  }

  // 1-Click Cancellation with Optimistic UI
  fun cancelSubscription(subId: String) {
    viewModelScope.launch {
      val sub = _subscriptions.value.find { it.id == subId } ?: return@launch
      // Step 1: Optimistic updating state
      _subscriptions.value = _subscriptions.value.map {
        if (it.id == subId) it.copy(status = SubscriptionStatus.CANCELLING_OPTIMISTIC) else it
      }

      // Show cancellation letter dispatch modal
      _cancellationPreviewSub.value = sub

      delay(600) // Brief optimistic animation delay

      // Step 2: Finalize cancellation & add savings with GDPR Data Wipe
      _subscriptions.value = _subscriptions.value.map {
        if (it.id == subId) it.copy(status = SubscriptionStatus.CANCELLED, gdprWipeRequested = true, isVirtualCardFrozen = true) else it
      }

      val annualSaved = if (sub.billingCadence == BillingCadence.MONTHLY) sub.currentCost * 12.0 else sub.currentCost
      _realizedSavingsYtd.value += annualSaved

      addAuditLog(
        title = "1-Click Cancellation: ${sub.merchantName}",
        detail = "Terminated recurring charge. Dispatched GDPR Article 17 Data Wipe Notice.",
        delta = annualSaved
      )
    }
  }

  // Toggle Virtual Card Active / Frozen
  fun toggleVirtualCard(subId: String) {
    val sub = _subscriptions.value.find { it.id == subId } ?: return
    val newFrozen = !sub.isVirtualCardFrozen
    _subscriptions.value = _subscriptions.value.map {
      if (it.id == subId) it.copy(isVirtualCardFrozen = newFrozen) else it
    }
    addAuditLog(
      title = if (newFrozen) "Virtual Card Frozen: ${sub.merchantName}" else "Virtual Card Active: ${sub.merchantName}",
      detail = if (newFrozen) "Frozen single-use card token. Auto-charge blocked at gateway." else "Activated single-use card token for recurring transactions.",
      delta = 0.0,
      isPositive = newFrozen
    )
  }

  // Toggle GDPR Data Wipe Notice on cancelled subscriptions
  fun toggleGdprWipe(subId: String) {
    val sub = _subscriptions.value.find { it.id == subId } ?: return
    val newStatus = !sub.gdprWipeRequested
    _subscriptions.value = _subscriptions.value.map {
      if (it.id == subId) it.copy(gdprWipeRequested = newStatus) else it
    }
    addAuditLog(
      title = if (newStatus) "GDPR Wipe Dispatched: ${sub.merchantName}" else "GDPR Wipe Cancelled: ${sub.merchantName}",
      detail = if (newStatus) "Sent Article 17 Right to Erasure request for PII & payment telemetry." else "Revoked erasure request.",
      delta = 0.0,
      isPositive = true
    )
  }

  // Screenshot Bill Audit Flow
  fun openScreenshotAudit() {
    _isScreenshotAuditOpen.value = true
  }

  fun dismissScreenshotAudit() {
    _isScreenshotAuditOpen.value = false
  }

  fun addAuditedSubscription(sub: Subscription) {
    _subscriptions.value = listOf(sub) + _subscriptions.value
    addAuditLog(
      title = "Screenshot Audited: ${sub.merchantName}",
      detail = "Verified start date ${sub.startDate} & renewal ${sub.renewalDate} (₹${"%.2f".format(sub.currentCost)}/mo).",
      delta = 0.0,
      isPositive = true
    )
  }

  fun dismissCancellationPreview() {
    _cancellationPreviewSub.value = null
  }

  // 1-Click Reclaim Idle Seats
  fun reclaimEnterpriseSeats(seatId: String) {
    viewModelScope.launch {
      val target = _enterpriseSeats.value.find { it.id == seatId } ?: return@launch
      if (target.isReclaimed) return@launch

      val annualSaved = target.annualWaste

      // Optimistic update
      _enterpriseSeats.value = _enterpriseSeats.value.map {
        if (it.id == seatId) it.copy(isReclaimed = true) else it
      }

      _realizedSavingsYtd.value += annualSaved

      addAuditLog(
        title = "Reclaimed ${target.idleSeats} Idle Seats: ${target.appName}",
        detail = "Deprovisioned dormant licenses with ${target.ssoProvider} sync.",
        delta = annualSaved
      )
    }
  }

  // Reclaim all idle seats across all enterprise vendors
  fun reclaimAllIdleSeats() {
    viewModelScope.launch {
      var totalAnnual = 0.0
      _enterpriseSeats.value = _enterpriseSeats.value.map { seat ->
        if (!seat.isReclaimed) {
          totalAnnual += seat.annualWaste
          seat.copy(isReclaimed = true)
        } else seat
      }

      _realizedSavingsYtd.value += totalAnnual

      addAuditLog(
        title = "Bulk Reclaim: All Dormant SaaS Seats",
        detail = "Optimized Okta, Google Workspace & Azure AD license tiers.",
        delta = totalAnnual
      )
    }
  }

  // Automated Discovery Engine Pipeline
  fun triggerDiscoveryScan() {
    viewModelScope.launch {
      _isDiscovering.value = true
      _discoveryStatusMessage.value = "Connecting to Plaid & Banking API stream..."
      delay(700)
      _discoveryStatusMessage.value = "Auditing Chase, Amex, SVB, Brex & Ramp feeds..."
      delay(800)
      _discoveryStatusMessage.value = "Classifying cadence intervals (30-day autocorrelation)..."
      delay(700)

      val report = DiscoveryEngine.runDiscoveryPipeline(_transactions.value, _subscriptions.value)
      _discoveryReport.value = report

      if (report.detectedSubscriptions.isNotEmpty()) {
        _subscriptions.value = _subscriptions.value + report.detectedSubscriptions
      }

      _discoveryStatusMessage.value = "Completed: Audited ${report.scannedTransactionsCount} transactions."
      delay(500)
      _isDiscovering.value = false

      addAuditLog(
        title = "Discovery Pipeline Run",
        detail = "Scanned ${report.scannedTransactionsCount} bank charges, detected ₹${"%.2f".format(report.monthlyAutopayTotal)}/mo recurring autopay.",
        delta = 0.0
      )
    }
  }

  fun dismissDiscoveryReport() {
    _discoveryReport.value = null
  }

  // AI Negotiation Flow
  fun startNegotiation(sub: Subscription) {
    _activeNegotiatingSubscription.value = sub
    _negotiationMessages.value = listOf(
      NegotiationMessage(
        sender = "AI Architect",
        text = "Initiated AI negotiation playbook for ${sub.merchantName}. Current price is ₹${"%.2f".format(sub.currentCost)}/mo. Our target is ₹${"%.2f".format(sub.targetNegotiatedPrice)}/mo (Save ₹${"%.2f".format((sub.currentCost - sub.targetNegotiatedPrice) * 12.0)}/yr).",
        timestamp = "Just now",
        isLeverageTactic = true
      ),
      NegotiationMessage(
        sender = "AI Architect",
        text = "Tactical strategy loaded: ${sub.negotiationLeverage ?: "Churn threat + parity discount framing"}. Click 'Generate Live Script' or send a custom objection.",
        timestamp = "Just now"
      )
    )
  }

  fun closeNegotiation() {
    _activeNegotiatingSubscription.value = null
    _negotiationMessages.value = emptyList()
  }

  fun sendNegotiationMessage(userText: String) {
    val sub = _activeNegotiatingSubscription.value ?: return
    val currentMsgs = _negotiationMessages.value

    val newMsg = NegotiationMessage(sender = "User", text = userText, timestamp = "Just now")
    _negotiationMessages.value = currentMsgs + newMsg

    viewModelScope.launch {
      _isNegotiatingAiBusy.value = true

      val history = currentMsgs.map { (if (it.sender == "User") "user" else "assistant") to it.text }
      val aiReply = GroqApiService.chatNegotiation(
        merchant = sub.merchantName,
        currentPrice = sub.currentCost,
        targetPrice = sub.targetNegotiatedPrice,
        userNote = userText,
        conversationHistory = history
      )

      _isNegotiatingAiBusy.value = false
      _negotiationMessages.value = _negotiationMessages.value + NegotiationMessage(
        sender = "AI Architect (Groq Llama)",
        text = aiReply,
        timestamp = "Just now"
      )
    }
  }

  fun applyNegotiatedPrice(subId: String, newPrice: Double) {
    viewModelScope.launch {
      val sub = _subscriptions.value.find { it.id == subId } ?: return@launch
      val oldPrice = sub.currentCost
      val annualSavings = (oldPrice - newPrice) * 12.0

      _subscriptions.value = _subscriptions.value.map {
        if (it.id == subId) {
          it.copy(
            currentCost = newPrice,
            status = SubscriptionStatus.NEGOTIATED_SAVED,
            classification = SubscriptionClassification.ACTIVE_UTILITY,
            recommendedAction = RecommendedAction.KEEP,
            notes = "Negotiated down from ₹${"%.2f".format(oldPrice)} to ₹${"%.2f".format(newPrice)}/mo."
          )
        } else it
      }

      _realizedSavingsYtd.value += annualSavings

      addAuditLog(
        title = "Negotiation Locked: ${sub.merchantName}",
        detail = "Reduced rate from ₹${"%.2f".format(oldPrice)} to ₹${"%.2f".format(newPrice)}/mo.",
        delta = annualSavings
      )

      closeNegotiation()
    }
  }

  // AI Data Extraction Flow (JSON Mode)
  fun updateExtractionInputText(text: String) {
    _extractionInputText.value = text
  }

  fun runExtraction() {
    viewModelScope.launch {
      _isExtracting.value = true
      delay(400) // Sub-second feel
      val result = GroqApiService.extractSubscriptionsJson(_extractionInputText.value)
      _extractedResult.value = result
      _isExtracting.value = false
    }
  }

  fun applyExtractedAuditToActive() {
    val result = _extractedResult.value ?: return
    viewModelScope.launch {
      val newSubs = result.detected_subscriptions.mapIndexed { idx, item ->
        val classification = when (item.classification.uppercase()) {
          "ZOMBIE_SUBSCRIPTION" -> SubscriptionClassification.ZOMBIE_SUBSCRIPTION
          "IDLE_SEAT" -> SubscriptionClassification.IDLE_SEAT
          else -> SubscriptionClassification.ACTIVE_UTILITY
        }
        val action = when (item.recommended_action.uppercase()) {
          "1_CLICK_CANCEL" -> RecommendedAction.`1_CLICK_CANCEL`
          "AI_NEGOTIATE" -> RecommendedAction.AI_NEGOTIATE
          else -> RecommendedAction.KEEP
        }
        Subscription(
          id = "extracted_${System.currentTimeMillis()}_$idx",
          merchantName = item.merchant_name,
          currentCost = item.current_cost,
          billingCadence = if (item.billing_cadence.contains("ANNUAL", true)) BillingCadence.ANNUAL else BillingCadence.MONTHLY,
          classification = classification,
          recommendedAction = action,
          targetNegotiatedPrice = item.target_negotiated_price,
          negotiationLeverage = item.negotiation_leverage,
          category = "Extracted Statement",
          renewalDate = "In 30 days",
          status = SubscriptionStatus.ACTIVE,
          isEnterprise = item.current_cost > 300.0,
          notes = "Imported via AI JSON Mode statement extraction."
        )
      }

      _subscriptions.value = _subscriptions.value + newSubs
      _extractedResult.value = null

      addAuditLog(
        title = "Imported AI Extracted Audit",
        detail = "Added ${newSubs.size} audited vendor subscriptions.",
        delta = 0.0
      )
    }
  }

  fun updateApiKey(key: String) {
    _apiKeyInput.value = key
    GroqApiService.customApiKey = key.trim().ifEmpty { null }
  }

  fun setModel(model: String) {
    _selectedModel.value = model
    GroqApiService.selectedModel = model
  }

  // Supabase Cloud Persistence
  fun loginToSupabase(email: String, pass: String) {
    viewModelScope.launch {
      _isSupabaseSyncing.value = true
      val (ok, msg) = SupabaseService.signInWithEmail(email, pass)
      _supabaseUser.value = SupabaseService.currentUser
      _isSupabaseSyncing.value = false
      addAuditLog(
        title = "Supabase Auth Sign In",
        detail = msg,
        delta = 0.0
      )
    }
  }

  fun signUpToSupabase(email: String, pass: String) {
    viewModelScope.launch {
      _isSupabaseSyncing.value = true
      val (ok, msg) = SupabaseService.signUpWithEmail(email, pass)
      _supabaseUser.value = SupabaseService.currentUser
      _isSupabaseSyncing.value = false
      addAuditLog(
        title = "Supabase Auth Sign Up",
        detail = msg,
        delta = 0.0
      )
    }
  }

  fun sendSupabasePasswordReset(email: String) {
    viewModelScope.launch {
      _isSupabaseSyncing.value = true
      val (ok, msg) = SupabaseService.sendPasswordReset(email)
      _isSupabaseSyncing.value = false
      addAuditLog(
        title = "Supabase Auth Password Reset",
        detail = msg,
        delta = 0.0
      )
    }
  }

  fun sendSupabaseMagicLink(email: String) {
    viewModelScope.launch {
      _isSupabaseSyncing.value = true
      val (ok, msg) = SupabaseService.sendMagicLink(email)
      _isSupabaseSyncing.value = false
      addAuditLog(
        title = "Supabase Auth Magic Link",
        detail = msg,
        delta = 0.0
      )
    }
  }

  fun loginToSupabaseAnon() {
    val anon = SupabaseService.signInAnonymously()
    _supabaseUser.value = anon
    addAuditLog(
      title = "Supabase Anonymous Session",
      detail = "Signed in as guest developer with RLS demo policy.",
      delta = 0.0
    )
  }

  fun logoutFromSupabase() {
    viewModelScope.launch {
      val (ok, msg) = SupabaseService.signOut()
      _supabaseUser.value = null
      addAuditLog(
        title = "Supabase Sign Out",
        detail = msg,
        delta = 0.0
      )
    }
  }

  fun syncAllDataToSupabase() {
    viewModelScope.launch {
      _isSupabaseSyncing.value = true
      _supabaseSyncStatus.value = SupabaseSyncStatus.SYNCING
      val subsOk = SupabaseService.syncSubscriptions(_subscriptions.value)
      val seatsOk = SupabaseService.syncEnterpriseSeats(_enterpriseSeats.value)
      _isSupabaseSyncing.value = false
      val now = SimpleDateFormat("h:mm a", Locale.US).format(Date())
      _lastSupabaseSyncTime.value = "Today, $now"
      _supabaseSyncStatus.value = if (subsOk || seatsOk) SupabaseSyncStatus.SYNCED else SupabaseSyncStatus.DEMO_MODE

      addAuditLog(
        title = "Supabase Full Cloud Sync",
        detail = "Synchronized ${_subscriptions.value.size} subscriptions & ${_enterpriseSeats.value.size} SaaS seats with PostgreSQL PostgREST.",
        delta = 0.0
      )
    }
  }

  fun syncWithSupabase() {
    syncAllDataToSupabase()
  }

  fun pullAllDataFromSupabase() {
    viewModelScope.launch {
      _isSupabaseSyncing.value = true
      _supabaseSyncStatus.value = SupabaseSyncStatus.SYNCING
      val remoteSubs = SupabaseService.fetchSubscriptions()
      if (!remoteSubs.isNullOrEmpty()) {
        _subscriptions.value = remoteSubs
      }
      _isSupabaseSyncing.value = false
      val now = SimpleDateFormat("h:mm a", Locale.US).format(Date())
      _lastSupabaseSyncTime.value = "Today, $now"
      _supabaseSyncStatus.value = SupabaseSyncStatus.SYNCED

      addAuditLog(
        title = "Supabase Cloud Pull",
        detail = "Retrieved latest subscription state from Supabase tables.",
        delta = 0.0
      )
    }
  }

  fun pullFromSupabase() {
    pullAllDataFromSupabase()
  }

  fun addMockSubscription(
    name: String,
    cost: Double,
    cadence: BillingCadence = BillingCadence.MONTHLY,
    classification: SubscriptionClassification = SubscriptionClassification.ACTIVE_UTILITY
  ) {
    val newSub = Subscription(
      id = "sub_${System.currentTimeMillis()}",
      merchantName = name,
      currentCost = cost,
      billingCadence = cadence,
      classification = classification,
      recommendedAction = if (classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION) RecommendedAction.`1_CLICK_CANCEL` else RecommendedAction.KEEP,
      targetNegotiatedPrice = (cost * 0.7 * 100).toLong() / 100.0,
      negotiationLeverage = null,
      category = "Cloud & Dev",
      daysSinceLastUse = if (classification == SubscriptionClassification.ZOMBIE_SUBSCRIPTION) 72 else 1,
      renewalDate = "Nov 28, 2026",
      status = SubscriptionStatus.ACTIVE
    )
    _subscriptions.value = listOf(newSub) + _subscriptions.value
    syncWithSupabase()
  }

  // --- Supabase Cloud Messaging (SCM) Renewal Alerts Actions ---

  fun setAlertThresholdDays(days: Int) {
    _alertThresholdDays.value = days
  }

  fun toggleAutoAlerts(enabled: Boolean) {
    _autoAlertsEnabled.value = enabled
  }

  fun dismissLastAlert() {
    _lastDispatchedAlert.value = null
  }

  fun clearRenewalAlerts() {
    _renewalAlerts.value = emptyList()
  }

  fun triggerRenewalCheckAndAlert(context: Context? = null) {
    viewModelScope.launch {
      _isDispatchingAlert.value = true
      delay(300) // sleek responsiveness
      val approaching = SupabaseService.getSubscriptionsApproachingRenewal(
        _subscriptions.value,
        _alertThresholdDays.value
      )

      if (approaching.isEmpty()) {
        _isDispatchingAlert.value = false
        addAuditLog(
          title = "Supabase SCM Renewal Check",
          detail = "Scanned ${_subscriptions.value.size} subscriptions. No renewals due in next ${_alertThresholdDays.value} days.",
          delta = 0.0
        )
        return@launch
      }

      val newAlerts = mutableListOf<RenewalAlert>()
      for ((sub, days) in approaching) {
        val alert = SupabaseService.dispatchRenewalAlert(sub, days)
        newAlerts.add(alert)
        if (context != null) {
          RenewalNotificationHelper.postRenewalNotification(context, alert)
        }
        _subscriptions.value = _subscriptions.value.map {
          if (it.id == sub.id) it.copy(hasRenewalAlertSent = true) else it
        }
      }

      _renewalAlerts.value = newAlerts + _renewalAlerts.value
      _lastDispatchedAlert.value = newAlerts.firstOrNull()
      _isDispatchingAlert.value = false

      addAuditLog(
        title = "Supabase Cloud Messaging: Dispatched ${newAlerts.size} Alerts",
        detail = "Sent renewal alerts to device for: ${newAlerts.joinToString(", ") { "${it.merchantName} (${it.daysRemaining}d)" }}",
        delta = 0.0
      )
    }
  }

  fun sendSingleSubscriptionRenewalAlert(sub: Subscription, context: Context? = null) {
    viewModelScope.launch {
      _isDispatchingAlert.value = true
      delay(200)
      val days = SupabaseService.calculateDaysRemaining(sub.renewalDate) ?: 3
      val alert = SupabaseService.dispatchRenewalAlert(sub, days)
      if (context != null) {
        RenewalNotificationHelper.postRenewalNotification(context, alert)
      }
      _subscriptions.value = _subscriptions.value.map {
        if (it.id == sub.id) it.copy(hasRenewalAlertSent = true) else it
      }
      _renewalAlerts.value = listOf(alert) + _renewalAlerts.value
      _lastDispatchedAlert.value = alert
      _isDispatchingAlert.value = false

      addAuditLog(
        title = "SCM Renewal Alert: ${sub.merchantName}",
        detail = "Broadcast renewal notice for ₹${"%.2f".format(sub.currentCost)} charge in $days days (${sub.renewalDate}).",
        delta = 0.0
      )
    }
  }

  // --- Bank SMS AutoPay Pipeline Actions ---

  fun openSmsPipeline() {
    _isSmsPipelineOpen.value = true
  }

  fun dismissSmsPipeline() {
    _isSmsPipelineOpen.value = false
  }

  fun openAiStudioPromptDialog() {
    _isAiStudioPromptOpen.value = true
  }

  fun dismissAiStudioPromptDialog() {
    _isAiStudioPromptOpen.value = false
  }

  fun updateCustomSmsInput(text: String) {
    _customSmsInput.value = text
  }

  fun updateCustomSmsSender(sender: String) {
    _customSmsSender.value = sender
  }

  fun setBankFilter(bank: BankCode?) {
    _selectedBankCode.value = bank
  }

  fun setAutoPaySignalFilter(signal: AutoPaySignal?) {
    _selectedAutoPaySignal.value = signal
  }

  fun scanDeviceSms(context: Context) {
    viewModelScope.launch {
      _isSmsScanning.value = true
      delay(400) // sleek UI feedback
      val deviceMessages = BankSmsParsingPipeline.readDeviceSmsInbox(context)
      if (deviceMessages.isNotEmpty()) {
        val existingTexts = _bankSmsList.value.map { it.rawBody }.toSet()
        val newItems = deviceMessages.filter { it.rawBody !in existingTexts }
        _bankSmsList.value = newItems + _bankSmsList.value
        addAuditLog(
          title = "Device SMS Inbox Audited",
          detail = "Scanned ${deviceMessages.size} bank messages. Found ${newItems.size} new AutoPay mandate signals.",
          delta = 0.0
        )
      } else {
        addAuditLog(
          title = "Device SMS Inbox Audited",
          detail = "Scanned inbox. Bank AutoPay pipeline active.",
          delta = 0.0
        )
      }
      _isSmsScanning.value = false
    }
  }

  fun loadSampleBankSms() {
    _bankSmsList.value = BankSmsParsingPipeline.sampleBankSmsMessages
    addAuditLog(
      title = "Loaded Indian Bank SMS Feed",
      detail = "Initialized 10 verified AutoPay mandate signals across HDFC, SBI, ICICI, Axis & Kotak.",
      delta = 0.0
    )
  }

  fun parseAndAddCustomSms(text: String, sender: String) {
    if (text.isBlank()) return
    val parsed = BankSmsParsingPipeline.parseSms(text, sender.ifBlank { "AD-HDFCBK" }, idPrefix = "user_sms")
    _bankSmsList.value = listOf(parsed) + _bankSmsList.value
    _customSmsInput.value = ""
    addAuditLog(
      title = "Parsed Bank SMS (${parsed.bankCode.code})",
      detail = "Identified ${parsed.merchant} (₹${"%.2f".format(parsed.amount)}) with signal ${parsed.autopaySignal.name} (${(parsed.confidence * 100).toInt()}% confidence).",
      delta = 0.0
    )
  }

  fun addParsedSmsToSubscriptions(sms: ParsedBankSms) {
    val existing = _subscriptions.value.find { it.merchantName.equals(sms.merchant, ignoreCase = true) }
    if (existing == null) {
      val isWaste = sms.autopaySignal == AutoPaySignal.REVOKED
      val classification = if (isWaste) SubscriptionClassification.ZOMBIE_SUBSCRIPTION else SubscriptionClassification.ACTIVE_UTILITY
      val action = if (isWaste) RecommendedAction.`1_CLICK_CANCEL` else RecommendedAction.KEEP
      val newSub = Subscription(
        id = "sms_sub_${System.currentTimeMillis()}",
        merchantName = sms.merchant,
        currentCost = if (sms.amount > 0.0) sms.amount else 499.0,
        billingCadence = BillingCadence.MONTHLY,
        classification = classification,
        recommendedAction = action,
        targetNegotiatedPrice = if (sms.amount > 0) (sms.amount * 0.7 * 100).toLong() / 100.0 else 349.0,
        negotiationLeverage = "Extracted via ${sms.bankCode.bankName} AutoPay signal (${sms.autopaySignal.name})",
        category = "Bank AutoPay",
        daysSinceLastUse = if (isWaste) 65 else 2,
        renewalDate = if (sms.autopaySignal == AutoPaySignal.SCHEDULED) "In 3 days" else "In 28 days",
        status = SubscriptionStatus.ACTIVE,
        notes = "Auto-detected from ${sms.bankCode.bankName} SMS (${sms.rawSender})."
      )
      _subscriptions.value = listOf(newSub) + _subscriptions.value
    }

    _bankSmsList.value = _bankSmsList.value.map {
      if (it.id == sms.id) it.copy(isAddedToTracked = true) else it
    }

    addAuditLog(
      title = "AutoPay Mandate Added: ${sms.merchant}",
      detail = "Tracked from ${sms.bankCode.bankName} ${sms.autopaySignal.name} signal at ₹${"%.2f".format(sms.amount)}/mo.",
      delta = 0.0
    )
  }

  fun importAllParsedSmsToSubscriptions() {
    val items = _bankSmsList.value.filter { !it.isAddedToTracked && it.amount > 0 }
    for (sms in items) {
      addParsedSmsToSubscriptions(sms)
    }
  }

  private fun addAuditLog(title: String, detail: String, delta: Double, isPositive: Boolean = true) {
    val formatter = SimpleDateFormat("h:mm a", Locale.US)
    val time = formatter.format(Date())
    val entry = AuditLogEntry(
      id = "log_${System.currentTimeMillis()}",
      timestamp = "Today, $time",
      title = title,
      detail = detail,
      amountDelta = delta,
      isPositive = isPositive
    )
    _auditLogs.value = listOf(entry) + _auditLogs.value.take(15)
  }
}

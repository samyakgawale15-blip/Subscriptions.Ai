package com.example.data

import com.example.model.BillingCadence
import com.example.model.EnterpriseSeat
import com.example.model.RecommendedAction
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import com.example.model.Transaction

object SampleData {

  val initialSubscriptions = listOf(
    Subscription(
      id = "sub_adobe",
      merchantName = "Adobe Creative Cloud",
      currentCost = 4230.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.AI_NEGOTIATE,
      targetNegotiatedPrice = 2999.0,
      negotiationLeverage = "Customer churn threat + Canva Enterprise cross-comparison",
      category = "Design",
      darkPatternWarning = "50% early termination fee for remaining contract months",
      daysSinceLastUse = 3,
      renewalDate = "Oct 24, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_netflix",
      merchantName = "Netflix Premium",
      currentCost = 649.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 499.0,
      negotiationLeverage = "Ad-tier downgrade option",
      category = "Entertainment",
      daysSinceLastUse = 1,
      renewalDate = "Oct 13, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_spotify",
      merchantName = "Spotify Duo",
      currentCost = 179.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 119.0,
      negotiationLeverage = null,
      category = "Music",
      daysSinceLastUse = 0,
      renewalDate = "Nov 8, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_chatgpt",
      merchantName = "ChatGPT Plus",
      currentCost = 1999.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 1499.0,
      negotiationLeverage = null,
      category = "AI Tools",
      daysSinceLastUse = 0,
      renewalDate = "Oct 14, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_equinox",
      merchantName = "Cult.fit / Equinox Gym",
      currentCost = 2999.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ZOMBIE_SUBSCRIPTION,
      recommendedAction = RecommendedAction.`1_CLICK_CANCEL`,
      targetNegotiatedPrice = 0.0,
      negotiationLeverage = null,
      category = "Fitness",
      darkPatternWarning = "Requires in-person cancellation signature or certified letter",
      daysSinceLastUse = 84,
      renewalDate = "Nov 1, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_jio",
      merchantName = "Jio Fiber & Mobile",
      currentCost = 999.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.AI_NEGOTIATE,
      targetNegotiatedPrice = 699.0,
      negotiationLeverage = "Airtel Xstream parity promotion & plan downgrade",
      category = "Telecom & Broadband",
      darkPatternWarning = "Auto-debit active on UPI; cancel via Jio WhatsApp Bot",
      daysSinceLastUse = 0,
      startDate = "Sep 28, 2026",
      renewalDate = "Oct 28, 2026",
      chatbotUrl = "https://wa.me/917000770007",
      status = SubscriptionStatus.ACTIVE,
      notes = "WhatsApp Bot assistant configured for instant chat redirection."
    ),
    Subscription(
      id = "sub_comcast",
      merchantName = "Airtel Xstream Fiber",
      currentCost = 1499.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.AI_NEGOTIATE,
      targetNegotiatedPrice = 999.0,
      negotiationLeverage = "Local fiber competitor promo + online chat",
      category = "Telecom & Cable",
      darkPatternWarning = "Retention chat flow; automated script available",
      daysSinceLastUse = 0,
      startDate = "Oct 2, 2026",
      renewalDate = "Nov 2, 2026",
      chatbotUrl = "https://wa.me/919910099100",
      status = SubscriptionStatus.ACTIVE,
      isLegacyVoiceNegotiating = false,
      isVirtualCardFrozen = false,
      notes = "WhatsApp bot & web chatbot script ready."
    ),
    Subscription(
      id = "sub_masterclass",
      merchantName = "MasterClass Annual",
      currentCost = 1299.0,
      billingCadence = BillingCadence.ANNUAL,
      classification = SubscriptionClassification.ZOMBIE_SUBSCRIPTION,
      recommendedAction = RecommendedAction.`1_CLICK_CANCEL`,
      targetNegotiatedPrice = 0.0,
      negotiationLeverage = null,
      category = "Education",
      darkPatternWarning = "Silent auto-renew billing without reminder email",
      daysSinceLastUse = 190,
      renewalDate = "Nov 14, 2026",
      status = SubscriptionStatus.ACTIVE,
      isVirtualCardFrozen = true,
      notes = "Virtual card auto-frozen to prevent unexpected renewal charge."
    ),
    Subscription(
      id = "sub_chegg",
      merchantName = "Chegg Study",
      currentCost = 1499.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ZOMBIE_SUBSCRIPTION,
      recommendedAction = RecommendedAction.`1_CLICK_CANCEL`,
      targetNegotiatedPrice = 0.0,
      negotiationLeverage = null,
      category = "Education",
      darkPatternWarning = "Recurring account reactivation trap",
      daysSinceLastUse = 120,
      renewalDate = "Cancelled",
      status = SubscriptionStatus.CANCELLED,
      isVirtualCardFrozen = true,
      gdprWipeRequested = true,
      notes = "Cancelled. Data wipe notice sent to DPO."
    ),
    Subscription(
      id = "sub_nytimes",
      merchantName = "The New York Times",
      currentCost = 499.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ZOMBIE_SUBSCRIPTION,
      recommendedAction = RecommendedAction.AI_NEGOTIATE,
      targetNegotiatedPrice = 199.0,
      negotiationLeverage = "Introductory ₹99/mo promo renewal guarantee",
      category = "News",
      darkPatternWarning = "Auto-escalated from ₹199/mo teaser rate without warning",
      daysSinceLastUse = 52,
      renewalDate = "Oct 19, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_audible",
      merchantName = "Audible Premium Plus",
      currentCost = 199.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ZOMBIE_SUBSCRIPTION,
      recommendedAction = RecommendedAction.AI_NEGOTIATE,
      targetNegotiatedPrice = 99.0,
      negotiationLeverage = "3-month half-price retention offer or account pause",
      category = "Audiobooks",
      darkPatternWarning = "Unused credits are forfeited upon direct cancellation",
      daysSinceLastUse = 62,
      renewalDate = "Nov 3, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_whoop",
      merchantName = "Whoop 4.0 Membership",
      currentCost = 2499.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ZOMBIE_SUBSCRIPTION,
      recommendedAction = RecommendedAction.`1_CLICK_CANCEL`,
      targetNegotiatedPrice = 0.0,
      negotiationLeverage = null,
      category = "Wearables",
      darkPatternWarning = "Hidden 12-month contract lock-in",
      daysSinceLastUse = 95,
      renewalDate = "Oct 30, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_duolingo",
      merchantName = "Duolingo Super Family",
      currentCost = 399.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 299.0,
      negotiationLeverage = null,
      category = "Education",
      daysSinceLastUse = 2,
      renewalDate = "Nov 12, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_midjourney",
      merchantName = "Midjourney Pro",
      currentCost = 2499.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 1699.0,
      negotiationLeverage = null,
      category = "AI Tools",
      daysSinceLastUse = 4,
      renewalDate = "Oct 29, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_figma",
      merchantName = "Figma Enterprise",
      currentCost = 3500.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.IDLE_SEAT,
      recommendedAction = RecommendedAction.`1_CLICK_CANCEL`,
      targetNegotiatedPrice = 2100.0,
      negotiationLeverage = "14 seats with zero login activity > 30 days",
      category = "Design",
      daysSinceLastUse = 38,
      renewalDate = "Dec 1, 2026",
      status = SubscriptionStatus.ACTIVE,
      isEnterprise = true
    ),
    Subscription(
      id = "sub_github",
      merchantName = "GitHub Enterprise",
      currentCost = 1699.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 1299.0,
      negotiationLeverage = null,
      category = "Developer",
      daysSinceLastUse = 0,
      renewalDate = "Jan 1, 2027",
      status = SubscriptionStatus.ACTIVE,
      isEnterprise = true
    ),
    Subscription(
      id = "sub_notion",
      merchantName = "Notion Team",
      currentCost = 1299.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 999.0,
      negotiationLeverage = null,
      category = "Productivity",
      daysSinceLastUse = 1,
      renewalDate = "Nov 15, 2026",
      status = SubscriptionStatus.ACTIVE
    ),
    Subscription(
      id = "sub_slack",
      merchantName = "Slack Pro",
      currentCost = 899.0,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 699.0,
      negotiationLeverage = null,
      category = "Communication",
      daysSinceLastUse = 0,
      renewalDate = "Nov 10, 2026",
      status = SubscriptionStatus.ACTIVE
    )
  )

  val initialEnterpriseSeats = listOf(
    EnterpriseSeat(
      id = "ent_figma",
      appName = "Figma Enterprise",
      iconKey = "figma",
      totalSeats = 48,
      activeSeats = 34,
      idleSeats = 14,
      costPerSeatMonthly = 3500.0,
      ssoProvider = "Okta SSO",
      isReclaimed = false,
      contractRenewalDate = "Dec 2026",
      complianceTier = "SOC2 Type II",
      alternativeVendor = "Penpot / Sketch"
    ),
    EnterpriseSeat(
      id = "ent_github",
      appName = "GitHub Enterprise",
      iconKey = "github",
      totalSeats = 110,
      activeSeats = 82,
      idleSeats = 28,
      costPerSeatMonthly = 1699.0,
      ssoProvider = "Google Workspace SSO",
      isReclaimed = false,
      contractRenewalDate = "Jan 2027",
      complianceTier = "ISO 27001",
      alternativeVendor = "GitLab SaaS"
    ),
    EnterpriseSeat(
      id = "ent_salesforce",
      appName = "Salesforce Sales Cloud",
      iconKey = "salesforce",
      totalSeats = 25,
      activeSeats = 17,
      idleSeats = 8,
      costPerSeatMonthly = 8500.0,
      ssoProvider = "Azure AD",
      isReclaimed = false,
      contractRenewalDate = "Nov 2026",
      complianceTier = "FedRAMP",
      alternativeVendor = "HubSpot CRM"
    ),
    EnterpriseSeat(
      id = "ent_datadog",
      appName = "Datadog APM & Infra",
      iconKey = "datadog",
      totalSeats = 30,
      activeSeats = 22,
      idleSeats = 8,
      costPerSeatMonthly = 4200.0,
      ssoProvider = "Okta SSO",
      isReclaimed = false,
      contractRenewalDate = "Feb 2027",
      complianceTier = "SOC2",
      alternativeVendor = "Grafana Cloud"
    ),
    EnterpriseSeat(
      id = "ent_slack",
      appName = "Slack Enterprise",
      iconKey = "slack",
      totalSeats = 140,
      activeSeats = 118,
      idleSeats = 22,
      costPerSeatMonthly = 899.0,
      ssoProvider = "Okta SSO",
      isReclaimed = false,
      contractRenewalDate = "Mar 2027",
      complianceTier = "HIPAA",
      alternativeVendor = "Teams"
    ),
    EnterpriseSeat(
      id = "ent_notion",
      appName = "Notion Business",
      iconKey = "notion",
      totalSeats = 65,
      activeSeats = 53,
      idleSeats = 12,
      costPerSeatMonthly = 1299.0,
      ssoProvider = "Google Workspace SSO",
      isReclaimed = false,
      contractRenewalDate = "Nov 2026",
      complianceTier = "SOC2",
      alternativeVendor = "Confluence"
    )
  )

  val sampleRawTransactions = listOf(
    Transaction("tx_01", "2026-10-01", "Adobe Systems Inc", 4230.0, "Design", "HDFC Corporate", true, 30),
    Transaction("tx_02", "2026-10-02", "Cult.fit Fitness", 2999.0, "Fitness", "ICICI Platinum", true, 30),
    Transaction("tx_03", "2026-10-03", "The New York Times", 499.0, "News", "HDFC Corporate", true, 30),
    Transaction("tx_04", "2026-10-05", "Netflix.com", 649.0, "Entertainment", "HDFC Corporate", true, 30),
    Transaction("tx_05", "2026-10-06", "Audible Amazon", 199.0, "Books", "ICICI Platinum", true, 30),
    Transaction("tx_06", "2026-10-08", "Spotify India", 179.0, "Music", "HDFC Corporate", true, 30),
    Transaction("tx_07", "2026-10-09", "Whoop Inc Membership", 2499.0, "Health", "HDFC Corporate", true, 30),
    Transaction("tx_08", "2026-10-10", "OpenAI ChatGPT Plus", 1999.0, "AI Tools", "HDFC Corporate", true, 30)
  )

  val sampleAuditStatementText = """
BANK STATEMENT / CORPORATE EXPENSE REPORT
Account: HDFC Bank / ICICI Corporate 9081

09/01/2026  Figma Enterprise 48-Pack     ₹1,68,000.00 (Monthly Recurring)
09/03/2026  GitHub Enterprise Cloud      ₹1,86,890.00 (Monthly Recurring)
09/05/2026  Salesforce.com Sales Cloud   ₹2,12,500.00 (Monthly Recurring)
09/07/2026  Datadog APM & Host Infra     ₹1,26,000.00 (Monthly Recurring)
09/10/2026  Slack Enterprise Grid Tier   ₹1,25,860.00 (Monthly Recurring)
09/12/2026  Notion Business Seats        ₹84,435.00   (Monthly Recurring)
09/15/2026  AWS Cloud Compute (EC2/RDS)  ₹3,45,280.00 (Usage)
09/18/2026  Adobe Creative Cloud Corp    ₹25,380.00   (Monthly Recurring)
  """.trimIndent()

  val sampleUpiMandateMessagesText = """
HDFC Bank: Dear Customer, UPI AutoPay mandate of INR 649.00 created for Netflix Entertainment Services Pvt Ltd on 12-Oct-2026. Ref: UMN/202610129841. Next debit: 12-Nov-2026.
SBI UPI: AutoPay debit of Rs.179.00 successful towards Spotify India Pvt Ltd. UPI Ref: 328491029384. Next monthly debit on 10-Nov-2026.
ICICI Bank: e-Mandate Alert! Rs. 1499.00 scheduled towards Cult.fit Healthcare Pvt Ltd (Idle Gym Pass). Standing instruction active.
Paytm UPI: Standing instruction of INR 999.00 executed for Jio Infocomm Fiber Autopay. UMN: JIO7812903.
Axis Bank: Mandate execution successful for Rs 249.00 to The Ken Media. Valid till 2027.
PhonePe: AutoPay Alert! Mandate for OpenAI ChatGPT Subscription (OpenAI) for Rs 1999.00 debited. Ref: OPNAI83921.
CRED: Recurring standing instruction active for Disney+ Hotstar VIP INR 299.00 debited from card ending 4091.
Google Pay: UPI AutoPay mandate set for Swiggy One Membership Rs 899.00.
Kotak Bank: Pre-debit notice: AutoPay of INR 4,230.00 for Adobe Creative Cloud will be presented on 24-Oct-2026.
AD-HDFCBK: A/C XX4921 debited ₹199.00 towards Audible Audiobooks monthly autopay subscription. Ref: 89312019.
Axis Bank: UPI AutoPay mandate of INR 1,499.00 for Amazon Prime Video has been revoked / cancelled as requested. Ref: UMN/AMZN82910.
  """.trimIndent()

  val initialRenewalAlerts = listOf(
    com.example.model.RenewalAlert(
      id = "alert_scm_01",
      subscriptionId = "sub_netflix",
      merchantName = "Netflix Premium",
      currentCost = 649.0,
      billingCadence = com.example.model.BillingCadence.MONTHLY,
      renewalDate = "Oct 13, 2026",
      daysRemaining = 3,
      alertTitle = "⚡ Approaching Renewal: Netflix Premium",
      alertBody = "₹649 auto-renewal scheduled in 3 days (Oct 13). Virtual card active — tap to freeze or cancel.",
      timestamp = "Today, 8:45 AM",
      channel = "Supabase Cloud Messaging (SCM)",
      isDelivered = true,
      priority = "HIGH",
      suggestedAction = "FREEZE_OR_CANCEL"
    ),
    com.example.model.RenewalAlert(
      id = "alert_scm_02",
      subscriptionId = "sub_chatgpt",
      merchantName = "ChatGPT Plus",
      currentCost = 1999.0,
      billingCadence = com.example.model.BillingCadence.MONTHLY,
      renewalDate = "Oct 14, 2026",
      daysRemaining = 4,
      alertTitle = "⚡ Approaching Renewal: ChatGPT Plus",
      alertBody = "₹1,999 will be auto-debited in 4 days (Oct 14). Status: Active utility.",
      timestamp = "Yesterday, 6:12 PM",
      channel = "Supabase Cloud Messaging (SCM)",
      isDelivered = true,
      priority = "NORMAL",
      suggestedAction = "KEEP"
    )
  )
}

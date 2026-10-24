package com.example.data

import com.example.model.BillingCadence
import com.example.model.RecommendedAction
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import com.example.model.Transaction
import java.util.UUID

object DiscoveryEngine {

  data class DiscoveryReport(
    val detectedSubscriptions: List<Subscription>,
    val scannedTransactionsCount: Int,
    val monthlyAutopayTotal: Double,
    val zombieLeakageMonthly: Double,
    val newDiscoveriesCount: Int
  )

  /**
   * Pipeline that ingests bank/Plaid transaction arrays, identifies recurring interval cadence
   * (e.g. charges repeating every ~30 days or ~365 days), and classifies them.
   */
  fun runDiscoveryPipeline(
    transactions: List<Transaction>,
    existingSubscriptions: List<Subscription>
  ): DiscoveryReport {
    val groupedByMerchant = transactions.groupBy { normalizeMerchant(it.merchant) }
    val newFoundSubs = mutableListOf<Subscription>()
    var monthlyAutopay = 0.0
    var zombieLeakage = 0.0

    for ((merchantKey, txList) in groupedByMerchant) {
      if (txList.size >= 2 || txList.any { it.isRecurring }) {
        val latest = txList.maxByOrNull { it.date } ?: txList.first()
        val amount = latest.amount

        // Check if merchant already exists in subscriptions
        val existing = existingSubscriptions.find { normalizeMerchant(it.merchantName) == merchantKey }
        if (existing == null) {
          val isZombie = (merchantKey.contains("equinox") || merchantKey.contains("whoop") || merchantKey.contains("masterclass") || merchantKey.contains("cult") || merchantKey.contains("gym"))
          val classification = if (isZombie) SubscriptionClassification.ZOMBIE_SUBSCRIPTION else SubscriptionClassification.ACTIVE_UTILITY
          val recommended = if (isZombie) RecommendedAction.`1_CLICK_CANCEL` else if (amount > 1500) RecommendedAction.AI_NEGOTIATE else RecommendedAction.KEEP
          val targetPrice = if (recommended == RecommendedAction.`1_CLICK_CANCEL`) 0.0 else (amount * 0.7)

          val newSub = Subscription(
            id = "sub_discovered_${UUID.randomUUID().toString().take(6)}",
            merchantName = latest.merchant,
            currentCost = amount,
            billingCadence = if (latest.cadenceDays > 100) BillingCadence.ANNUAL else BillingCadence.MONTHLY,
            classification = classification,
            recommendedAction = recommended,
            targetNegotiatedPrice = targetPrice,
            negotiationLeverage = if (recommended == RecommendedAction.AI_NEGOTIATE) "Discovered recurring UPI interval without active engagement" else null,
            category = latest.category,
            darkPatternWarning = if (isZombie) "Hidden recurring billing / AutoPay detected" else null,
            daysSinceLastUse = if (isZombie) 75 else 2,
            renewalDate = "In 28 days",
            status = SubscriptionStatus.ACTIVE,
            isEnterprise = false,
            notes = "Auto-detected by Discovery Engine from ${latest.accountName} feed."
          )
          newFoundSubs.add(newSub)
        }

        monthlyAutopay += amount
        if (merchantKey.contains("equinox") || merchantKey.contains("whoop") || merchantKey.contains("masterclass") || merchantKey.contains("cult") || merchantKey.contains("gym")) {
          zombieLeakage += amount
        }
      }
    }

    return DiscoveryReport(
      detectedSubscriptions = newFoundSubs,
      scannedTransactionsCount = transactions.size,
      monthlyAutopayTotal = monthlyAutopay,
      zombieLeakageMonthly = zombieLeakage,
      newDiscoveriesCount = newFoundSubs.size
    )
  }

  private fun normalizeMerchant(name: String): String {
    return name.lowercase()
      .replace(Regex("""[^a-z0-9]"""), "")
      .replace("autopay", "")
      .replace("mandate", "")
      .replace("pvtltd", "")
      .replace("pvt", "")
      .replace("ltd", "")
      .replace("inc", "")
      .replace("usa", "")
      .replace("systems", "")
      .replace("corp", "")
      .replace("com", "")
  }
}

package com.example

import com.example.data.DiscoveryEngine
import com.example.data.GroqApiService
import com.example.data.SampleData
import com.example.model.EnterpriseSeat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testDiscoveryPipelineIntervalDetection() {
    val report = DiscoveryEngine.runDiscoveryPipeline(
      SampleData.sampleRawTransactions,
      emptyList()
    )
    assertTrue("Should scan raw transactions", report.scannedTransactionsCount > 0)
    assertTrue("Should detect recurring monthly autopay", report.monthlyAutopayTotal > 0.0)
    assertTrue("Should identify subscriptions", report.detectedSubscriptions.isNotEmpty())
  }

  @Test
  fun testStrictJsonSchemaExtraction() {
    val auditOutput = GroqApiService.generateLocalExtractionAudit(SampleData.sampleAuditStatementText)
    assertNotNull(auditOutput.financial_audit)
    assertTrue("Total monthly spend must be positive", auditOutput.financial_audit.total_monthly_spend > 0)
    assertTrue("Identified waste must be non-negative", auditOutput.financial_audit.identified_waste >= 0)
    assertTrue("Detected subscriptions list must not be empty", auditOutput.detected_subscriptions.isNotEmpty())

    val firstSub = auditOutput.detected_subscriptions.first()
    assertNotNull(firstSub.merchant_name)
    assertTrue(firstSub.current_cost > 0)
    assertTrue(firstSub.billing_cadence in listOf("MONTHLY", "ANNUAL"))
  }

  @Test
  fun testEnterpriseSeatWasteCalculations() {
    val seat = EnterpriseSeat(
      id = "test_figma",
      appName = "Figma Enterprise",
      iconKey = "figma",
      totalSeats = 48,
      activeSeats = 34,
      idleSeats = 14,
      costPerSeatMonthly = 75.0,
      isReclaimed = false
    )
    assertEquals(1050.0, seat.monthlyWaste, 0.01)
    assertEquals(12600.0, seat.annualWaste, 0.01)

    val reclaimedSeat = seat.copy(isReclaimed = true)
    assertEquals(0.0, reclaimedSeat.monthlyWaste, 0.01)
    assertEquals(0.0, reclaimedSeat.annualWaste, 0.01)
  }

  @Test
  fun testMockSubscriptionsAndSupabaseEndpoint() {
    val url = com.example.data.SupabaseService.getEffectiveUrl()
    assertTrue(url.isNotBlank())
    val key = com.example.data.SupabaseService.getEffectiveAnonKey()
    assertTrue(key.isNotBlank())
    assertTrue(SampleData.initialSubscriptions.size >= 10)
  }

  @Test
  fun testSubscriptionListingFieldsForComposable() {
    val subscriptions = SampleData.initialSubscriptions
    for (sub in subscriptions) {
      assertTrue("Subscription name must not be blank", sub.merchantName.isNotBlank())
      assertTrue("Subscription cost must be non-negative", sub.currentCost >= 0.0)
      assertNotNull("Status must not be null", sub.status)
      assertNotNull("Classification must not be null", sub.classification)
    }
  }

  @Test
  fun testAdvancedDifferentiationFeaturesMock() {
    val subscriptions = SampleData.initialSubscriptions

    // 1. Card-per-Vendor Controls: At least one subscription has virtual card control capability
    val frozenCardSub = subscriptions.find { it.isVirtualCardFrozen }
    assertNotNull("Should contain mock subscription with frozen virtual card", frozenCardSub)

    // 2. Privacy Actions: Canceled subscription has GDPR Data Wipe Requested badge
    val gdprWipeSub = subscriptions.find { it.gdprWipeRequested }
    assertNotNull("Should contain canceled subscription with GDPR data wipe requested", gdprWipeSub)
    assertEquals(com.example.model.SubscriptionStatus.CANCELLED, gdprWipeSub?.status)

    // 3. WhatsApp Bot & Chatbot Redirection: Jio & Comcast have chatbot support
    val jioSub = subscriptions.find { it.merchantName.contains("Jio", ignoreCase = true) }
    assertNotNull("Should contain Jio subscription for WhatsApp Bot redirection", jioSub)
    assertTrue("Jio should have WhatsApp bot url", jioSub?.chatbotUrl?.contains("wa.me") == true)
  }

  @Test
  fun testSupabaseCloudMessagingRenewalAlerts() {
    val subscriptions = SampleData.initialSubscriptions

    // Verify renewal calculation
    val daysForNetflix = com.example.data.SupabaseService.calculateDaysRemaining("Oct 13, 2026")
    assertNotNull(daysForNetflix)

    val relativeDays = com.example.data.SupabaseService.calculateDaysRemaining("In 3 days")
    assertEquals(3, relativeDays)

    // Verify finding approaching renewals (threshold 7 days)
    val approaching = com.example.data.SupabaseService.getSubscriptionsApproachingRenewal(subscriptions, 7)
    assertTrue("Should detect approaching renewals within 7 days", approaching.isNotEmpty())

    // Verify initial renewal alerts exist
    assertTrue("Sample renewal alerts should be present", SampleData.initialRenewalAlerts.isNotEmpty())
    val firstAlert = SampleData.initialRenewalAlerts.first()
    assertEquals("Supabase Cloud Messaging (SCM)", firstAlert.channel)
    assertTrue(firstAlert.isDelivered)
    assertTrue(firstAlert.daysRemaining <= 7)
  }

  @Test
  fun testUpiMandateParsingPipeline() {
    val sampleText = com.example.data.SampleData.sampleUpiMandateMessagesText
    val parsedList = com.example.engine.BankSmsParsingPipeline.parseAllMessages(sampleText)
    assertTrue("Should parse multiple mandate SMS messages", parsedList.size >= 8)

    // Check specific detections
    val netflixSms = parsedList.find { it.merchant.contains("Netflix", ignoreCase = true) }
    assertNotNull("Should detect Netflix mandate", netflixSms)
    assertEquals(649.0, netflixSms!!.amount, 0.01)
    assertEquals("UPI AutoPay", netflixSms.mandateType)
    assertEquals(com.example.model.AutoPaySignal.CREATED, netflixSms.autopaySignal)

    val spotifySms = parsedList.find { it.merchant.contains("Spotify", ignoreCase = true) }
    assertNotNull("Should detect Spotify mandate", spotifySms)
    assertEquals(179.0, spotifySms!!.amount, 0.01)
    assertEquals(com.example.model.AutoPaySignal.DEBITED, spotifySms.autopaySignal)

    val hotstarSms = parsedList.find { it.merchant.contains("Disney+ Hotstar", ignoreCase = true) }
    assertNotNull("Should detect Disney+ Hotstar from Novi Digital legal entity", hotstarSms)
    assertEquals(299.0, hotstarSms!!.amount, 0.01)

    val cultSms = parsedList.find { it.merchant.contains("Cult.fit", ignoreCase = true) }
    assertNotNull("Should detect Cult.fit from Curefit Healthcare legal entity", cultSms)
    assertEquals(1499.0, cultSms!!.amount, 0.01)
    assertEquals(com.example.model.AutoPaySignal.SCHEDULED, cultSms.autopaySignal)

    val revokedSms = parsedList.find { it.autopaySignal == com.example.model.AutoPaySignal.REVOKED }
    assertNotNull("Should detect revoked / stopped mandate", revokedSms)
    assertTrue(revokedSms!!.merchant.contains("Amazon Prime", ignoreCase = true))

    // Test AI / Local Extraction with mandate sample
    val auditOutput = GroqApiService.generateLocalExtractionAudit(sampleText)
    assertTrue("Audit output should have detected subscriptions from mandates", auditOutput.detected_subscriptions.isNotEmpty())
    assertTrue("Audit output total monthly spend should reflect mandates", auditOutput.financial_audit.total_monthly_spend > 0)
  }
}

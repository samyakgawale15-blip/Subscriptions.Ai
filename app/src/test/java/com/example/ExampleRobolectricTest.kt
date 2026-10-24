package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SupabaseService
import com.example.model.BillingCadence
import com.example.model.RecommendedAction
import com.example.model.Subscription
import com.example.model.SubscriptionClassification
import com.example.model.SubscriptionStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Subscriptions.AI", appName)
  }

  @Test
  fun `test calculateDaysRemaining parsing`() {
    val daysInThree = SupabaseService.calculateDaysRemaining("In 3 days")
    assertEquals(3, daysInThree)

    val daysInSeven = SupabaseService.calculateDaysRemaining("In 7 days")
    assertEquals(7, daysInSeven)

    val cancelled = SupabaseService.calculateDaysRemaining("Cancelled")
    assertEquals(null, cancelled)
  }

  @Test
  fun `test getSubscriptionsApproachingRenewal threshold filtering`() {
    val sub1 = Subscription(
      id = "s1",
      merchantName = "Netflix",
      currentCost = 15.99,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 12.99,
      category = "Entertainment",
      renewalDate = "In 3 days",
      status = SubscriptionStatus.ACTIVE
    )
    val sub2 = Subscription(
      id = "s2",
      merchantName = "Gym",
      currentCost = 50.00,
      billingCadence = BillingCadence.MONTHLY,
      classification = SubscriptionClassification.ACTIVE_UTILITY,
      recommendedAction = RecommendedAction.KEEP,
      targetNegotiatedPrice = 40.00,
      category = "Fitness",
      renewalDate = "In 14 days",
      status = SubscriptionStatus.ACTIVE
    )

    val approaching = SupabaseService.getSubscriptionsApproachingRenewal(listOf(sub1, sub2), thresholdDays = 7)
    assertEquals(1, approaching.size)
    assertEquals("Netflix", approaching.first().first.merchantName)
    assertEquals(3, approaching.first().second)
  }

  @Test
  fun `test Supabase GoTrue auth flows`() = runBlocking {
    // 1. Sign In
    val (signInOk, signInMsg) = SupabaseService.signInWithEmail("user@example.com", "password123")
    assertTrue(signInOk)
    val activeUser = SupabaseService.currentUser
    assertNotNull(activeUser)
    assertEquals("user@example.com", activeUser?.email)
    assertNotNull(activeUser?.accessToken)

    // 2. Sign Up
    val (signUpOk, signUpMsg) = SupabaseService.signUpWithEmail("newuser@example.com", "securepass123")
    assertTrue(signUpOk)
    assertEquals("newuser@example.com", SupabaseService.currentUser?.email)

    // 3. Passwordless Magic Link
    val (magicOk, magicMsg) = SupabaseService.sendMagicLink("magic@example.com")
    assertTrue(magicOk)

    // 4. Password Recovery
    val (resetOk, resetMsg) = SupabaseService.sendPasswordReset("user@example.com")
    assertTrue(resetOk)

    // 5. Sign Out
    val (signOutOk, signOutMsg) = SupabaseService.signOut()
    assertTrue(signOutOk)
    assertNull(SupabaseService.currentUser)

    // 6. Anonymous Session
    val anon = SupabaseService.signInAnonymously()
    assertTrue(anon.isAnonymous)
    assertEquals("anon", anon.role)
  }

  @Test
  fun `test screenshot bill audit model and date tracking`() {
    val auditResult = com.example.model.ScreenshotAuditResult(
      id = "audit_test_1",
      merchantName = "Jio Fiber & Mobile",
      startDate = "Oct 14, 2026",
      expiryDate = "Nov 14, 2026",
      currentCost = 12.99,
      planName = "Jio 100 Mbps Unlimited Fiber",
      chatbotUrl = "https://wa.me/917000770007",
      suggestedChatbotScript = "Hi Jio Support, I would like to review my current fiber plan (Rs 999/mo)."
    )

    assertEquals("Jio Fiber & Mobile", auditResult.merchantName)
    assertEquals("Oct 14, 2026", auditResult.startDate)
    assertEquals("Nov 14, 2026", auditResult.expiryDate)
    assertTrue(auditResult.chatbotUrl.contains("wa.me"))
  }

  @Test
  fun `test Jio and Airtel chatbot redirection URLs and sample data`() {
    val jioSub = com.example.data.SampleData.initialSubscriptions.find { it.merchantName.contains("Jio") }
    assertNotNull(jioSub)
    assertEquals("https://wa.me/917000770007", jioSub?.chatbotUrl)
    assertEquals("Sep 28, 2026", jioSub?.startDate)
    assertEquals("Oct 28, 2026", jioSub?.renewalDate)
  }
}



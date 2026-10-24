package com.example.ui.dialogs

import androidx.compose.runtime.Composable
import com.example.model.NegotiationMessage
import com.example.model.Subscription

/**
 * WhatsApp Copilot & Web Chatbot Redirection Dialog
 * Replaces legacy voice agent negotiation with WhatsApp Bot guidance and direct web chatbot redirection.
 */
@Composable
fun NegotiationDialog(
  subscription: Subscription,
  messages: List<NegotiationMessage> = emptyList(),
  isAiBusy: Boolean = false,
  onSendMessage: (String) -> Unit = {},
  onApplyPrice: (String, Double) -> Unit,
  onDismiss: () -> Unit
) {
  WhatsAppBotCopilotDialog(
    subscription = subscription,
    onApplyPrice = onApplyPrice,
    onDismiss = onDismiss
  )
}

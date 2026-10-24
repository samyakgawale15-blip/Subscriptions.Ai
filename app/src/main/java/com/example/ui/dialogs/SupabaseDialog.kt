package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SupabaseService
import com.example.ui.theme.SoftRose
import com.example.ui.theme.SupabaseBg
import com.example.ui.theme.SupabaseBorder
import com.example.ui.theme.SupabaseGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteBackground
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteSurface
import kotlinx.coroutines.launch

@Composable
fun SupabaseDialog(
  subscriptionsCount: Int = 14,
  onSyncSubscriptions: () -> Unit,
  onPullSubscriptions: () -> Unit = {},
  onAddMockSubscription: (String, Double, Boolean) -> Unit = { _, _, _ -> },
  onOpenCloudMessaging: (() -> Unit)? = null,
  onDismiss: () -> Unit
) {
  val scope = rememberCoroutineScope()
  var selectedTab by remember { mutableStateOf(0) } // 0: Cloud Sync, 1: User Account
  var urlInput by remember { mutableStateOf(SupabaseService.getEffectiveUrl()) }
  var keyInput by remember { mutableStateOf(SupabaseService.getEffectiveAnonKey()) }
  var emailInput by remember { mutableStateOf("samyakgawale15@gmail.com") }
  var passwordInput by remember { mutableStateOf("••••••••••••") }
  var statusMessage by remember { mutableStateOf("Ready: $subscriptionsCount mock subscriptions ready to sync with Supabase PostgREST") }
  var isTesting by remember { mutableStateOf(false) }

  val activeUser = SupabaseService.currentUser

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .heightIn(max = 640.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(WhiteSurface)
        .border(1.dp, WhiteBorder, RoundedCornerShape(16.dp)),
      color = WhiteSurface
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
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
                .clip(RoundedCornerShape(8.dp))
                .background(SupabaseBg)
                .border(1.dp, SupabaseBorder, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = "Supabase Studio",
                tint = SupabaseGreen,
                modifier = Modifier.size(19.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Supabase Cloud Studio",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "PostgreSQL Persistence, Auth & Cloud Messaging",
                color = TextMuted,
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs (No raw SQL on front end)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            label = { Text("Cloud Sync", fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(13.dp)) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SupabaseBg, selectedLabelColor = SupabaseGreen),
            modifier = Modifier.weight(1f).height(32.dp)
          )

          FilterChip(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            label = { Text("User Account", fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(13.dp)) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SupabaseBg, selectedLabelColor = SupabaseGreen),
            modifier = Modifier.weight(1f).height(32.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
          0 -> {
            // Config & Sync Tab
            Column {
              // Connection Status Banner
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SupabaseBg)
                  .border(1.dp, SupabaseBorder, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(SupabaseGreen)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = statusMessage,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Supabase Project URL
              Text(
                text = "SUPABASE PROJECT URL",
                color = TextMuted,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(3.dp))
              OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                placeholder = { Text("https://your-project.supabase.co", color = TextMuted, fontSize = 11.5.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = WhiteBackground,
                  unfocusedContainerColor = WhiteBackground,
                  focusedBorderColor = SupabaseGreen,
                  unfocusedBorderColor = WhiteBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("input_supabase_url")
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Supabase Anon Key
              Text(
                text = "SUPABASE ANON PUBLIC KEY",
                color = TextMuted,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(3.dp))
              OutlinedTextField(
                value = keyInput,
                onValueChange = { keyInput = it },
                placeholder = { Text("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", color = TextMuted, fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = WhiteBackground,
                  unfocusedContainerColor = WhiteBackground,
                  focusedBorderColor = SupabaseGreen,
                  unfocusedBorderColor = WhiteBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("input_supabase_key")
              )

              Spacer(modifier = Modifier.height(10.dp))

              // SCM Quick Card
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(WhiteBackground)
                  .border(1.dp, WhiteBorder, RoundedCornerShape(8.dp))
                  .padding(8.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SupabaseGreen, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text("Supabase Cloud Messaging (SCM)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                      Text("Renewal date alerts & push webhooks", fontSize = 9.5.sp, color = TextMuted)
                    }
                  }

                  if (onOpenCloudMessaging != null) {
                    OutlinedButton(
                      onClick = onOpenCloudMessaging,
                      shape = RoundedCornerShape(6.dp),
                      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 1.dp),
                      modifier = Modifier.height(26.dp).testTag("btn_manage_scm")
                    ) {
                      Text("Open SCM", fontSize = 9.5.sp, color = SupabaseGreen, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Generate Mock Subscriptions Section
              Text(
                text = "GENERATE MOCK SUBSCRIPTION & SYNC",
                color = TextMuted,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    onAddMockSubscription("Linear Pro", 999.00, false)
                    statusMessage = "✓ Added Linear Pro (₹999/mo) and synced to Supabase"
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                  modifier = Modifier.weight(1f).height(30.dp)
                ) {
                  Text("+ Linear ₹999", fontSize = 10.sp, color = TextPrimary)
                }

                OutlinedButton(
                  onClick = {
                    onAddMockSubscription("Supabase Pro", 2099.00, false)
                    statusMessage = "✓ Added Supabase Pro (₹2,099/mo) and synced to Supabase"
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                  modifier = Modifier.weight(1.1f).height(30.dp)
                ) {
                  Text("+ Supabase ₹2099", fontSize = 10.sp, color = SupabaseGreen, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                  onClick = {
                    onAddMockSubscription("Gym Luxury Membership", 1499.00, true)
                    statusMessage = "✓ Added Zombie Gym (₹1,499/mo) and synced to Supabase"
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                  modifier = Modifier.weight(1.2f).height(30.dp)
                ) {
                  Text("+ Zombie Gym ₹1499", fontSize = 10.sp, color = SoftRose)
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Action Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    scope.launch {
                      isTesting = true
                      statusMessage = "Testing Supabase REST endpoint..."
                      val (ok, msg) = SupabaseService.testConnection(urlInput, keyInput)
                      isTesting = false
                      statusMessage = if (ok) "✓ Supabase connected successfully: $msg" else "Error: $msg"
                    }
                  },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  if (isTesting) {
                    CircularProgressIndicator(color = SupabaseGreen, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                  } else {
                    Text("Test API", fontSize = 11.sp)
                  }
                }

                OutlinedButton(
                  onClick = {
                    onPullSubscriptions()
                    statusMessage = "✓ Pulled subscriptions from Supabase table!"
                  },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Text("Pull Table", fontSize = 11.sp)
                }

                Button(
                  onClick = {
                    SupabaseService.customSupabaseUrl = urlInput.trim()
                    SupabaseService.customSupabaseAnonKey = keyInput.trim()
                    onSyncSubscriptions()
                    statusMessage = "✓ Synchronized subscriptions to Supabase table!"
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = SupabaseGreen, contentColor = TextPrimary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1.2f).height(36.dp).testTag("btn_save_supabase")
                ) {
                  Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Sync (${subscriptionsCount})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          1 -> {
            // User Account & Cloud Auth Tab
            var authMode by remember { mutableStateOf(0) } // 0: Sign In, 1: Sign Up, 2: Magic Link, 3: Reset Password
            Column {
              Text(
                text = "SUPABASE GOTRUE AUTHENTICATION",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(6.dp))

              // Current User & JWT Card
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SupabaseBg)
                  .border(1.dp, SupabaseBorder, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier.size(28.dp).clip(CircleShape).background(SupabaseGreen),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                      }
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Text(
                          text = activeUser?.email ?: "No Active User Session",
                          color = TextPrimary,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold
                        )
                        Text(
                          text = "Role: ${activeUser?.role ?: "guest"} • Signed in: ${activeUser?.lastSignIn ?: "N/A"}",
                          color = TextMuted,
                          fontSize = 10.sp
                        )
                      }
                    }

                    if (activeUser != null) {
                      OutlinedButton(
                        onClick = {
                          scope.launch {
                            val (ok, msg) = SupabaseService.signOut()
                            statusMessage = "✓ $msg"
                          }
                        },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 1.dp),
                        modifier = Modifier.height(26.dp)
                      ) {
                        Text("Sign Out", fontSize = 9.5.sp, color = SoftRose)
                      }
                    }
                  }

                  if (activeUser?.accessToken != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(WhiteBackground)
                        .border(1.dp, WhiteBorder, RoundedCornerShape(6.dp))
                        .padding(6.dp)
                    ) {
                      Column {
                        Text("ACTIVE JWT BEARER TOKEN", color = SupabaseGreen, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        Text(
                          text = activeUser.accessToken ?: "",
                          color = TextMuted,
                          fontSize = 8.5.sp,
                          maxLines = 2,
                          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Auth Mode Selector Tabs
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                val modes = listOf("Sign In", "Sign Up", "Magic Link", "Reset Pass")
                modes.forEachIndexed { index, title ->
                  val selected = authMode == index
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (selected) SupabaseGreen.copy(alpha = 0.2f) else WhiteBackground)
                      .border(1.dp, if (selected) SupabaseGreen else WhiteBorder, RoundedCornerShape(6.dp))
                      .clickable { authMode = index }
                      .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = title,
                      color = if (selected) SupabaseGreen else TextMuted,
                      fontSize = 9.5.sp,
                      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text("USER EMAIL", color = TextMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(3.dp))
              OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = WhiteBackground,
                  unfocusedContainerColor = WhiteBackground,
                  focusedBorderColor = SupabaseGreen,
                  unfocusedBorderColor = WhiteBorder
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("input_auth_email")
              )

              if (authMode == 0 || authMode == 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("PASSWORD", color = TextMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                OutlinedTextField(
                  value = passwordInput,
                  onValueChange = { passwordInput = it },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = WhiteBackground,
                    unfocusedContainerColor = WhiteBackground,
                    focusedBorderColor = SupabaseGreen,
                    unfocusedBorderColor = WhiteBorder
                  ),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth().testTag("input_auth_password")
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              when (authMode) {
                0 -> {
                  // Sign In
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedButton(
                      onClick = {
                        SupabaseService.signInAnonymously()
                        statusMessage = "✓ Signed in with Supabase Anonymous Session"
                        selectedTab = 0
                      },
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f).height(36.dp).testTag("btn_anon_session")
                    ) {
                      Text("Guest Session", fontSize = 11.sp)
                    }

                    Button(
                      onClick = {
                        scope.launch {
                          val (ok, msg) = SupabaseService.signInWithEmail(emailInput, passwordInput)
                          statusMessage = "✓ $msg"
                          selectedTab = 0
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = SupabaseGreen, contentColor = TextPrimary),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1.2f).height(36.dp).testTag("btn_sign_in")
                    ) {
                      Text("Authenticate Session", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
                1 -> {
                  // Sign Up
                  Button(
                    onClick = {
                      scope.launch {
                        val (ok, msg) = SupabaseService.signUpWithEmail(emailInput, passwordInput)
                        statusMessage = "✓ $msg"
                        selectedTab = 0
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SupabaseGreen, contentColor = TextPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_sign_up")
                  ) {
                    Text("Create New Account", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
                2 -> {
                  // Magic Link
                  Button(
                    onClick = {
                      scope.launch {
                        val (ok, msg) = SupabaseService.sendMagicLink(emailInput)
                        statusMessage = "✓ $msg"
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SupabaseGreen, contentColor = TextPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_magic_link")
                  ) {
                    Text("Send Passwordless Magic Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
                3 -> {
                  // Password Reset
                  Button(
                    onClick = {
                      scope.launch {
                        val (ok, msg) = SupabaseService.sendPasswordReset(emailInput)
                        statusMessage = "✓ $msg"
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SupabaseGreen, contentColor = TextPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_reset_password")
                  ) {
                    Text("Send Recovery Email", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.popups

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.AccountSettingsAccountTabUiState
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.AccountSettingsAccountTabViewModel
import com.stefdp.pterodactylpanel.utils.NumberRegex
import kotlinx.coroutines.launch

@Composable
fun Enable2FaPopup(
    activity: FragmentActivity,
    context: Context,
    state: AccountSettingsAccountTabUiState,
    viewModel: AccountSettingsAccountTabViewModel,
) {
    Popup(
        showPopup = state.showEnable2FAPopup && state.recoveryCodes.isEmpty(),
        onDismissRequest = {
            viewModel.hideEnable2FAPopup()
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Enable Two-Step Verification",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Help protect your account from unauthorized access. You'll be prompted for a verification code each time you sign in"
            )

            val clipboardManager = LocalClipboard.current

            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, state.twoFactorAuthenticationUri?.toUri())

                        activity.startActivity(intent)
                    } catch(e: Exception) {
                        Notification.show(
                            activity = activity,
                            duration = 6000L
                        ) {
                            Text(
                                text = "No authenticator app found. Secret copied to clipboard"
                            )
                        }

                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText(
                                "2FA Secret",
                                state.twoFactorAuthenticationSecret ?: ""
                            ).toClipEntry()

                            clipboardManager.setClipEntry(clipData)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.twoFactorAuthenticationUri != null
            ) {
                Text(
                    text = "Open Authenticator App"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Secret: " + (state.twoFactorAuthenticationSecret ?: "Loading..."),
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Icon(
                    painter = painterResource(R.drawable.content_copy),
                    contentDescription = "Copy 2FA secret",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(
                            enabled = !state.isLoading && state.twoFactorAuthenticationSecret != null,
                            onClick = {
                                coroutineScope.launch {
                                    val clipData = ClipData.newPlainText(
                                        "2FA Secret",
                                        state.twoFactorAuthenticationSecret ?: ""
                                    ).toClipEntry()

                                    clipboardManager.setClipEntry(clipData)
                                }
                            }
                        )
                )
            }

            Text(
                text = "Press the button above and open the two-step authentication app of your choice. Then, enter the 6-digit code generated into the field below"
            )

            TextInput(
                placeholder = "000000",
                value = state.twoFactorAuthenticationCode,
                onValueChange = {
                    if (it.text.length > 6 || !NumberRegex.matches(it.text)) return@TextInput

                    viewModel.set2FACode(it)
                },
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                label = "Account Password",
                value = state.twoFactorAuthenticationPassword,
                onValueChange = {
                    viewModel.set2FAPassword(it)
                },
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.hideEnable2FAPopup()
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        viewModel.enable2FA(
                            context = context,
                            onError = { error ->
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                        )
                    },
                    buttonType = ButtonType.PRIMARY,
                    enabled =
                        state.twoFactorAuthenticationCode.text.trim().length == 6 &&
                        state.twoFactorAuthenticationPassword.text.trim().isNotBlank() &&
                        !state.isLoading
                ) {
                    Text("Enable")
                }
            }
        }
    }
}
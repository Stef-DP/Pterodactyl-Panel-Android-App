package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.popups

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.AccountSettingsAccountTabUiState
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.account.AccountSettingsAccountTabViewModel
import kotlinx.coroutines.launch

@Composable
fun RecoveryCodes2FAPopup(
    activity: FragmentActivity,
    context: Context,
    state: AccountSettingsAccountTabUiState,
    viewModel: AccountSettingsAccountTabViewModel,
) {
    Popup(
        showPopup = state.recoveryCodes.isNotEmpty(),
        onDismissRequest = {
            viewModel.hideRecoveryCodes()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Two-Step Authentication Enabled",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Store the codes below somewhere safe. If you lose access to your phone you can use these backup codes to sign in"
            )

            val formattedRecoveryCodes by remember(state.recoveryCodes) {
                mutableStateOf(
                    state.recoveryCodes
                        .chunked(2)
                        .joinToString("\n") {
                            it.joinToString("   ")
                        }
                )
            }

            Text(
                text = formattedRecoveryCodes,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                style = TextStyle(
                    lineBreak = LineBreak.Simple,
                    fontFamily = FontFamily.Monospace
                )
            )

            WarningAlert()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val clipboardManager = LocalClipboard.current

                val coroutineScope = rememberCoroutineScope()

                Button(
                    onClick = {
                        viewModel.hideRecoveryCodes()
                    },
                    buttonType = ButtonType.ERROR,
                ) {
                    Text("Close without copying")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val copyFormattedRecoveryCodes = state.recoveryCodes.joinToString("\n")

                            val clipData = ClipData.newPlainText(
                                "2FA Recovery Codes",
                                copyFormattedRecoveryCodes
                            ).toClipEntry()

                            clipboardManager.setClipEntry(clipData)

                            viewModel.hideRecoveryCodes()
                        }
                    },
                    buttonType = ButtonType.PRIMARY,
                ) {
                    Text("Copy and close")
                }
            }
        }
    }
}

@Composable
private fun WarningAlert() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(MaterialTheme.colorScheme.error)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.gpp_maybe),
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error,
            )

            Text(
                text = "These codes will not be shown again",
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}
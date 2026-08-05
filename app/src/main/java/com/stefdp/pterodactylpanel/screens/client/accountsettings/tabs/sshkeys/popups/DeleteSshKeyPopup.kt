package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.sshkeys.popups

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.sshkeys.ClientAccountSettingsSshKeysTabUiState
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.sshkeys.ClientAccountSettingsSshKeysTabViewModel

@Composable
fun DeleteSshKeyPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientAccountSettingsSshKeysTabUiState,
    viewModel: ClientAccountSettingsSshKeysTabViewModel,
) {
    val sshKey = state.sshKeys.find { it.attributes.fingerprint == state.sshKeyToDelete }

    if (sshKey == null) return

    Popup(
        showPopup = state.sshKeyToDelete != null,
        onDismissRequest = {
            viewModel.setSshKeyToDelete(null)
        },
    ) {
        Text(
            text = "Delete SSH Key",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        FlowRow {
            Text(
                text = "Removing the "
            )

            Text(
                text = sshKey.attributes.name,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                style = TextStyle(
                    lineBreak = LineBreak.Simple,
                    fontFamily = FontFamily.Monospace
                )
            )

            Text(
                text = " SSH key will invalidate its usage across the Panel"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.setSshKeyToDelete(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.deleteSshKey(
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
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "SSH key deleted successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.ERROR,
                enabled = !state.isLoading
            ) {
                Text("Delete Key")
            }
        }
    }
}
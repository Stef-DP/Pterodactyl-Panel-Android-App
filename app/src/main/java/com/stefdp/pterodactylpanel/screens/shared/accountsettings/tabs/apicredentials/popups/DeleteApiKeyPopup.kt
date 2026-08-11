package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.apicredentials.popups

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
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.apicredentials.AccountSettingsApiCredentialsTabUiState
import com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.apicredentials.AccountSettingsApiCredentialsTabViewModel

@Composable
fun DeleteApiKeyPopup(
    activity: FragmentActivity,
    context: Context,
    state: AccountSettingsApiCredentialsTabUiState,
    viewModel: AccountSettingsApiCredentialsTabViewModel,
) {
    val apiKey = state.apiKeys.find { it.attributes.identifier == state.apiKeyToDelete }

    if (apiKey == null) return

    Popup(
        showPopup = state.apiKeyToDelete != null,
        onDismissRequest = {
            viewModel.setApiKeyToDelete(null)
        },
    ) {
        Text(
            text = "Delete Api Key",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        FlowRow {
            Text(
                text = "All requests using the "
            )

            CodeText(
                text = apiKey.attributes.identifier
            )

            Text(
                text = " key will be invalidated"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.setApiKeyToDelete(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.deleteApiKey(
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
                                    text = "Api key deleted successfully",
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
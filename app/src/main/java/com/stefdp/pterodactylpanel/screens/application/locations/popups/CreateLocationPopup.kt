package com.stefdp.pterodactylpanel.screens.application.locations.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.application.locations.ApplicationLocationsUiState
import com.stefdp.pterodactylpanel.screens.application.locations.ApplicationLocationsViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun CreateLocationPopup(
    activity: FragmentActivity,
    context: Context,
    state: ApplicationLocationsUiState,
    viewModel: ApplicationLocationsViewModel,
) {
    Popup(
        showPopup = state.showCreateLocationPopup,
        onDismissRequest = {
            viewModel.hideCreateLocationPopup()
        },
        scrollable = false
    ) {
        Text(
            text = "Create Location",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScrollWithScrollbar(scrollState)
        ) {
            TextInput(
                value = state.newLocationShortCode,
                onValueChange = {
                    viewModel.setNewLocationShortCode(it)
                },
                label = "Short Code",
                description = "A short identifier used to distinguish this location from others. Must be between 1 and 60 characters, for example, \"us.nyc.lvl3\"",
                modifier = Modifier.fillMaxWidth(),
                enabled = state.locations != null
            )

            TextInput(
                value = state.newLocationDescription,
                onValueChange = {
                    viewModel.setNewLocationDescription(it)
                },
                label = "Description",
                description = "A longer description of this location. Must be less than 191 characters",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                enabled = state.locations != null,
                singleLine = false
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideCreateLocationPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = state.locations != null
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createLocation(
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
                                    text = "Location created successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled =
                    state.locations != null &&
                    state.newLocationShortCode.text.trim().isNotBlank()
            ) {
                Text("Create")
            }
        }
    }
}
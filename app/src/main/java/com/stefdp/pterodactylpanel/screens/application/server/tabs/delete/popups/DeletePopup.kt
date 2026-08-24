package com.stefdp.pterodactylpanel.screens.application.server.tabs.delete.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.application.server.tabs.delete.ApplicationServerDeleteTabUiState
import com.stefdp.pterodactylpanel.screens.application.server.tabs.delete.ApplicationServerDeleteTabViewModel

@Composable
fun DeletePopup(
    navController: NavHostController,
    activity: FragmentActivity,
    context: Context,
    state: ApplicationServerDeleteTabUiState,
    viewModel: ApplicationServerDeleteTabViewModel,
) {
    Popup(
        showPopup = state.showSafeDeletePopup || state.showForceDeletePopup,
        onDismissRequest = {
            viewModel.hideSafeDeletePopup()
            viewModel.hideForceDeletePopup()
        },
    ) {
        val isSafeDelete = state.showSafeDeletePopup

        Text(
            text = if (isSafeDelete) {
                "Safely Delete Server"
            } else {
                "Forcibly Delete Server"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Are you sure that you want to delete this server? There is no going back, all data will immediately be removed"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideSafeDeletePopup()
                    viewModel.hideForceDeletePopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (isSafeDelete) {
                        viewModel.safelyDeleteServer(
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
                                navController.popBackStack()
                            }
                        )
                    } else {
                        viewModel.forciblyDeleteServer(
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
                                navController.popBackStack()
                            }
                        )
                    }


                },
                buttonType = ButtonType.ERROR,
                enabled = !state.isLoading
            ) {
                Text("Delete")
            }
        }
    }
}
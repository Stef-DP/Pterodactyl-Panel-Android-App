package com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.ClientServerFilesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.ClientServerFilesTabViewModel

@Composable
fun UnsavedChangesPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerFilesTabUiState,
    viewModel: ClientServerFilesTabViewModel,
    closeFileEditing: () -> Unit
) {
    Popup(
        showPopup = state.showUnsavedFileWarningPopup,
        onDismissRequest = {
            viewModel.hideUnsavedFileWarningPopup()
        },
    ) {
        Text(
            text = "Unsaved Changes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )

        Text(
            text = "You have unsaved changes. Are you sure you want to leave without saving?",
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideUnsavedFileWarningPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Keep Editing")
            }

            Button(
                onClick = {
                    closeFileEditing()
                },
                buttonType = ButtonType.PRIMARY,
                enabled = !state.isLoading
            ) {
                Text("Leave Without Saving")
            }
        }
    }
}
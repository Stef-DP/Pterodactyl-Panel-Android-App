package com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.ClientServerFilesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.ClientServerFilesTabViewModel
import java.nio.file.Paths

@Composable
fun CreateNewDirectoryPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerFilesTabUiState,
    viewModel: ClientServerFilesTabViewModel,
) {
    Popup(
        showPopup = state.showNewDirectoryPopup,
        onDismissRequest = {
            viewModel.hideCreateNewDirectoryPopup()
        },
        scrollable = true,
    ) {
        Text(
            text = "Create New Directory",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )

        TextInput(
            value = state.newDirectoryName,
            label = "Name",
            onValueChange = { newValue ->
                viewModel.setNewDirectoryName(newValue)
            },
            placeholder = "Directory Name",
            modifier = Modifier
                .fillMaxWidth(),
            enabled = !state.isLoading
        )

        FlowRow {
            Text(
                text = "This directory will be created as"
            )

            val folderPathText = buildAnnotatedString {
                append("/${state.filesPath.joinToString("/")}/")

                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    val basePath = Paths.get("/")

                    val absoluteNormalizedPath = basePath.resolve(state.newDirectoryName.text).normalize()

                    append(basePath.relativize(absoluteNormalizedPath).toString())
                }
            }

            CodeText(
                text = folderPathText
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideCreateNewDirectoryPopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createNewDirectory(
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
                                    text = "Directory created successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled = state.newDirectoryName.text.isNotBlank() && !state.isLoading
            ) {
                Text("Create")
            }
        }
    }
}
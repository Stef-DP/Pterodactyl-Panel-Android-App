package com.stefdp.pterodactylpanel.screens.client.server.tabs.files

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.neoutils.highlight.compose.remember.rememberTextFieldValue
import com.neoutils.highlight.core.Highlight
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups.CreateNewFilePopup
import com.stefdp.pterodactylpanel.screens.client.server.tabs.files.popups.UnsavedChangesPopup
import com.stefdp.pterodactylpanel.ui.theme.HighlightLanguage
import com.stefdp.pterodactylpanel.ui.theme.languageToHighlightColors
import com.stefdp.pterodactylpanel.ui.theme.supportedLanguages

@Composable
fun FileEditTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerFilesTabViewModel,
    state: ClientServerFilesTabUiState
) {
    fun closeFileEditing() {
        viewModel.hideUnsavedFileWarningPopup()
        viewModel.setFileContent(TextFieldValue(""))
        viewModel.setCreateNewFile(false)
        viewModel.clearFileToEdit()
        viewModel.setOriginalFileContent("")
    }

    BackHandler {
        if (state.fileContent.text != state.originalFileContent) {
            viewModel.showUnsavedFileWarningPopup()
        } else {
            closeFileEditing()
        }
    }

    UnsavedChangesPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel,
        closeFileEditing = ::closeFileEditing
    )

    CreateNewFilePopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        val highlight = Highlight(
            *languageToHighlightColors[state.selectedLanguage].orEmpty().toTypedArray()
        )

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            if (state.isFetchingFileContent) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                TextInput(
                    value = highlight.rememberTextFieldValue(state.fileContent),
                    onValueChange = {
                        viewModel.setFileContent(it)
                    },
                    singleLine = false,
                    modifier = Modifier.fillMaxSize(),
                    fontFamily = FontFamily.Monospace,
                    enabled = !state.isFileSaving
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            Select(
                multiple = false,
                selectedIds = setOf(state.selectedLanguage.toString()),
                options = supportedLanguages.map { language ->
                    SelectOption(
                        id = language.first.toString(),
                        label = { enabled ->
                            Text(
                                text = language.second,
                                color = if (enabled) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                }
                            )
                        }
                    )
                }.sortedBy { it.id },
                onSelectionChange = {
                    if (it.isEmpty()) return@Select

                    viewModel.setSelectedLanguage(HighlightLanguage.valueOf(it.first()))
                },
                containerModifier = Modifier.weight(1f),
                enabled = !state.isFetchingFileContent && !state.isFileSaving
            )

            Button(
                onClick = {
                    if (state.createNewFile || state.fileToEdit == null) {
                        viewModel.showNewFileNamePopup()
                    } else {
                        viewModel.saveFile(
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
                                        text = "File saved successfully",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        )
                    }
                },
                buttonType = ButtonType.PRIMARY,
                modifier = Modifier.weight(1f),
                enabled = !state.isFetchingFileContent && !state.isFileSaving
            ) {
                Text(
                    text = "Save",
                )
            }
        }
    }
}
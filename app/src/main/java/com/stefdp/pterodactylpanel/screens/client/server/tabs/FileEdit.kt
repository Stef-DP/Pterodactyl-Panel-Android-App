package com.stefdp.pterodactylpanel.screens.client.server.tabs

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.neoutils.highlight.compose.remember.rememberTextFieldValue
import com.neoutils.highlight.core.Highlight
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerUiState
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerViewModel
import com.stefdp.pterodactylpanel.ui.theme.HighlightLanguage
import com.stefdp.pterodactylpanel.ui.theme.languageToHighlightColors
import com.stefdp.pterodactylpanel.ui.theme.supportedLanguages

@Composable
fun FileEditTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerViewModel,
    state: ClientServerUiState
) {
    fun closeFileEditing() {
        viewModel.hideUnsavedFileWarningPopup()
        viewModel.setFileContent(TextFieldValue(""))
        viewModel.setCreateNewFile(false)
        viewModel.setFileToEdit(null)

        Logger.debug("closeFileEditing", "Navigating back to FilesTab")
    }

    BackHandler {
        if (state.fileContent.text != state.originalFileContent) {
            viewModel.showUnsavedFileWarningPopup()
        } else {
            closeFileEditing()
        }
    }

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
            modifier = Modifier.padding(12.dp)
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
            TextInput(
                value = highlight.rememberTextFieldValue(state.fileContent),
                onValueChange = {
                    viewModel.setFileContent(it)
                },
                singleLine = false,
                modifier = Modifier.fillMaxSize(),
                fontFamily = FontFamily.Monospace
            )
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
                },
                onSelectionChange = {
                    if (it.isEmpty()) return@Select

                    viewModel.setSelectedLanguage(HighlightLanguage.valueOf(it.first()))
                },
                containerModifier = Modifier.weight(1f)
            )

            Button(
                onClick = {},
                buttonType = ButtonType.PRIMARY,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Save",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
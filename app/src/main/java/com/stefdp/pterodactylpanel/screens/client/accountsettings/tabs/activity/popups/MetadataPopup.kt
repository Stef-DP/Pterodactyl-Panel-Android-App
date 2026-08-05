package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.activity.popups

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.google.gson.GsonBuilder
import com.neoutils.highlight.compose.extension.toAnnotatedString
import com.neoutils.highlight.core.Highlight
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.activity.ClientAccountSettingsActivityTabUiState
import com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.activity.ClientAccountSettingsActivityTabViewModel
import com.stefdp.pterodactylpanel.ui.theme.jsonHighlightColors

@Composable
fun MetadataPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientAccountSettingsActivityTabUiState,
    viewModel: ClientAccountSettingsActivityTabViewModel
) {
    val activity = state.activity.find { it.attributes.id == state.logToShowMetadata }

    if (activity == null) return

    Popup(
        showPopup = state.logToShowMetadata != null,
        onDismissRequest = {
            viewModel.setLogToShowMetadata(null)
        }
    ) {
        Text(
            text = "Metadata",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        val prettyGson = GsonBuilder().setPrettyPrinting().create()
        val prettyJsonString = prettyGson.toJson(activity.attributes.properties)

        val highlight = remember(jsonHighlightColors) {
            Highlight(jsonHighlightColors)
        }

        val annotatedString = remember(prettyJsonString, highlight) {
            highlight.toAnnotatedString(prettyJsonString)
        }

        Text(
            text = annotatedString,
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 4.dp
                ),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    viewModel.setLogToShowMetadata(null)
                },
                buttonType = ButtonType.SECONDARY,
                enabled = !state.isLoading
            ) {
                Text("Close")
            }
        }
    }
}
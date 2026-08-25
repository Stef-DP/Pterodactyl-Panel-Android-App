package com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.configuration

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.GsonBuilder
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun ConfigurationTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationNestEggConfigurationTabViewModel = viewModel(),
    egg: ApplicationEgg?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(egg?.attributes?.id, refreshIndex) {
        if (egg == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(egg)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val prettyGson = GsonBuilder().setPrettyPrinting().create()

        Container(
            title = {
                Text(
                    text = "Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = TextFieldValue(state.egg?.attributes?.name ?: ""),
                onValueChange = {},
                label = "Name",
                description = "A simple, human-readable name to use as an identifier for this Egg",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = TextFieldValue(state.egg?.attributes?.uuid ?: ""),
                onValueChange = {},
                label = "UUID",
                description = "This is the globally unique identifier for this Egg which the Daemon uses as an identifier",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = TextFieldValue(state.egg?.attributes?.author ?: ""),
                onValueChange = {},
                label = "Author",
                description = "The author of this version of the Egg. Uploading a new Egg configuration from a different author will change this",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.egg?.attributes?.dockerImages
                    ?.map { (name, image) -> "$name|$image" }
                    ?.joinToString("\n")
                    ?.let { TextFieldValue(it) }
                    ?: TextFieldValue(""),
                onValueChange = {},
                label = "Docker Images",
                description = "The docker images available to servers using this egg. Enter one per line. Users will be able to select from this list of images if more than one value is provided. Optionally, a display name may be provided by prefixing the image with the name followed by a pipe character, and then the image URL. Example: `Display Name|ghcr.io/my/egg`",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            TextInput(
                value = TextFieldValue(state.egg?.attributes?.description ?: ""),
                onValueChange = {},
                label = "Description",
                description = "A description of this Egg that will be displayed throughout the Panel as needed",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            TextInput(
                value = TextFieldValue(state.egg?.attributes?.startup ?: ""),
                onValueChange = {},
                label = "Startup Command",
                description = "The default startup command that should be used for new servers using this Egg",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }

        Container(
            title = {
                Text(
                    text = "Process Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = TextFieldValue(state.egg?.attributes?.config?.stop ?: ""),
                onValueChange = {},
                label = "Stop Command",
                description = "The command that should be sent to server processes to stop them gracefully. If you need to send a `SIGINT` you should enter `^C` here",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            val prettyJsonLogsString = prettyGson.toJson(state.egg?.attributes?.config?.logs)

            TextInput(
                value = TextFieldValue(prettyJsonLogsString.takeIf { it != "[]" } ?: "{}"),
                onValueChange = {},
                label = "Log Configuration",
                description = "This should be a JSON representation of where log files are stored, and whether or not the daemon should be creating custom logs",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            val prettyJsonFilesString = prettyGson.toJson(state.egg?.attributes?.config?.files)

            TextInput(
                value = TextFieldValue(prettyJsonFilesString ?: "{}"),
                onValueChange = {},
                label = "Configuration Files",
                description = "This should be a JSON representation of configuration files to modify and what parts should be changed",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            val prettyJsonStartupString = prettyGson.toJson(state.egg?.attributes?.config?.startup)

            TextInput(
                value = TextFieldValue(prettyJsonStartupString ?: "{}"),
                onValueChange = {},
                label = "Start Configuration",
                description = "This should be a JSON representation of what values the daemon should be looking for when booting a server to determine completion",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }
    }
}
package com.stefdp.pterodactylpanel.screens.application.server.tabs.startup

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.screens.application.server.tabs.about.ApplicationServerAboutTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun StartupTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationServerStartupTabViewModel = viewModel(),
    server: ApplicationServer?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(server?.attributes?.id, refreshIndex) {
        if (server == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(
            context = context,
            server = server
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .verticalScrollWithScrollbar(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Container(
                title = {
                    Text(
                        text = "Startup Command Modification",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                TextInput(
                    value = state.startupCommand,
                    onValueChange = { newValue ->
                        viewModel.setStartupCommand(newValue)
                    },
                    label = "Startup Command",
                    description = "Edit your server's startup command here. The following variables are available by default: `{{SERVER_MEMORY}}`, `{{SERVER_IP}}`, and `{{SERVER_PORT}}`" ,
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && !state.nestsLoading
                )

                val clipboardManager = LocalClipboard.current

                val coroutineScope = rememberCoroutineScope()

                TextInput(
                    value = state.defaultStartupCommand,
                    onValueChange = {},
                    label = "Default Service Start Command",
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = painterResource(R.drawable.content_copy),
                    trailingIconContentDescription = "Copy Default Service Start Command",
                    onTrailingIconPress = {
                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText(
                                "Default Service Start Command",
                                state.defaultStartupCommand.text
                            ).toClipEntry()

                            clipboardManager.setClipEntry(clipData)
                        }
                    },
                    readOnly = true,
                    enabled = !state.isLoading && !state.nestsLoading
                )
            }

            val eggs = state.nests
                .filter { it.attributes.id == state.nest.firstOrNull()?.toLongOrNull() }
                .flatMap { it.attributes.relationships?.eggs?.data ?: emptyList() }

            Container(
                title = {
                    Text(
                        text = "Service Configuration",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            append("Changing any of the below values will result in the server processing a re-install command. The server will be stopped and will then proceed. If you would like the service scripts to not run, ensure the box is checked at the bottom.\n")

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("This is a destructive operation in many cases. This server will be stopped immediately in order for this action to proceed.")
                            }
                        }
                    }
                )

                Select(
                    options = state.nests.map { (_, nest) ->
                        SelectOption(
                            id = nest.id.toString(),
                            searchLabel = nest.name,
                            label = {
                                Text(
                                    text = nest.name
                                )
                            }
                        )
                    },
                    searchable = true,
                    label = "Nest",
                    description = "Select the Nest that this server will be grouped into",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    selectedIds = state.nest,
                    onSelectionChange = {
                        viewModel.setNest(it)
                    },
                    enabled = !state.isLoading && !state.nestsLoading
                )

                Select(
                    options = eggs.map { (_, egg) ->
                        SelectOption(
                            id = egg.id.toString(),
                            searchLabel = egg.name,
                            label = {
                                Text(
                                    text = egg.name
                                )
                            }
                        )
                    },
                    searchable = true,
                    label = "Egg",
                    description = "Select the Egg that will provide processing data for this server",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    selectedIds = state.egg,
                    onSelectionChange = {
                        viewModel.setEgg(it)
                    },
                    enabled = !state.isLoading && !state.nestsLoading
                )

                Switch(
                    checked = state.skipEggInstallScript,
                    onCheckedChange = {
                        viewModel.setSkipEggInstallScript(it)
                    },
                    label = "Skip Egg Install Script",
                    description = "If the selected Egg has an install script attached to it, the script will run during install. If you would like to skip this step, check this box",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    enabled = !state.isLoading
                )
            }

            Container(
                title = {
                    Text(
                        text = "Docker Configuration",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                val egg = eggs.find { it.attributes.id == state.egg.firstOrNull()?.toLongOrNull() }

                val dockerImages = egg?.attributes?.dockerImages?.map { (name, image) ->
                    SelectOption(
                        id = image,
                        label = {
                            Text(
                                text = "$name ($image)"
                            )
                        }
                    )
                }

                Select(
                    options = dockerImages ?: emptyList(),
                    label = "Docker Image",
                    selectedIds = state.dockerImage,
                    onSelectionChange = {
                        viewModel.setDockerImage(it)
                    },
                    enabled = !state.isLoading && !state.nestsLoading && (dockerImages?.size ?: 1) > 1
                )

                TextInput(
                    value = state.customDockerImage,
                    onValueChange = {
                        viewModel.setCustomDockerImage(it)
                    },
                    placeholder = "Or enter a custom image...",
                    description = "This is the Docker image that will be used to run this server. Select an image from the dropdown or enter a custom image in the text field above",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && !state.nestsLoading
                )
            }

            state.variables.forEach { (_, variable) ->
                Container(
                    title = {
                        Text(
                            text = variable.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                ) {
                    TextInput(
                        value = state.variableContent[variable.envVariable] ?: TextFieldValue(variable.defaultValue),
                        onValueChange = {
                            viewModel.setVariableContent(variable.envVariable, it)
                        },
                        description = variable.description,
                        placeholder = variable.defaultValue,
                        descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.background)
                    )

                    CodeText(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Access in Startup:")
                            }

                            append(" `${variable.envVariable}`")

                            append("\n")

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Validation Rules:")
                            }

                            append(" `${variable.rules}`")
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                viewModel.updateServer(
                    context = context,
                    onSuccess = {
                        Notification.show(
                            activity = activity,
                            duration = 3000L
                        ) {
                            Text(
                                text = "Successfully updated server startup"
                            )
                        }
                    },
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
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled =
                !state.isLoading &&
                !state.nestsLoading &&
                state.nest.isNotEmpty() &&
                state.egg.isNotEmpty() &&
                (state.dockerImage.isNotEmpty() || state.customDockerImage.text.trim().isNotBlank()) &&
                state.startupCommand.text.trim().isNotBlank()
        ) {
            Text(
                text = "Save Modifications"
            )
        }
    }
}
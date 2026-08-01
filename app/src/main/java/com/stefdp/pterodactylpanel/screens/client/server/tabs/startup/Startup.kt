package com.stefdp.pterodactylpanel.screens.client.server.tabs.startup

import HumanReadableRes.image
import android.R.attr.label
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.StartupContainer
import com.stefdp.pterodactylpanel.screens.client.server.components.StartupVariableDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules.ClientServerSchedulesTabViewModel
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun StartupTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerStartupTabViewModel = viewModel(),
    server: GetServerResponse?
) {
    LaunchedEffect(server) {
        viewModel.init(server)

        viewModel.updateStartup(
            context = context,
            onSuccess = {},
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
    }

    val state by viewModel.state.collectAsState()

    val lazyColumnListState = rememberLazyListState()

    LazyColumn(
        state = lazyColumnListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalLazyScrollbar(
                listState = lazyColumnListState
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            StartupContainer(
                title = "Startup Command"
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    Text(
                        text = state.startupCommand,
                        style = TextStyle(
                            lineBreak = LineBreak.Simple,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        item {
            StartupContainer(
                title = "Docker Image"
            ) {
                if (state.dockerImages.size <= 1) {
                    TextInput(
                        value = TextFieldValue(state.selectedDockerImage.firstOrNull() ?: ""),
                        onValueChange = {},
                        readOnly = true,
                        placeholder = "Unknown Docker Image",
                        modifier = Modifier.fillMaxWidth(),
                    )

                    return@StartupContainer
                }

                Select(
                    options = state.dockerImages.map { image ->
                        SelectOption(
                            id = image.value,
                            label = {
                                Text(
                                    text = image.key
                                )
                            }
                        )
                    },
                    selectedIds = state.selectedDockerImage,
                    onSelectionChange = { image ->
                        if (image == state.selectedDockerImage) return@Select

                        viewModel.updateDockerImage(
                            context = context,
                            image = image,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = "Docker image updated successfully",
                                        color = MaterialTheme.colorScheme.onSurface
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
                    enabled = !state.isLoading
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
        }

        item {
            Text(
                text = "Variables",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.variables.size) { index ->
            val variable = state.variables[index]

            StartupVariableDisplay(
                variable = variable,
                enabled = !state.isLoading,
                onVariableUpdate = { newValue ->
                    viewModel.updateVariable(
                        context = context,
                        variable = variable.attributes.envVariable,
                        value = newValue,
                        onSuccess = {
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Variable updated successfully"
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
                }
            )
        }
    }
}
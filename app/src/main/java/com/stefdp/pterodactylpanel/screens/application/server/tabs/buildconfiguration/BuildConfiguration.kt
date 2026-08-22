package com.stefdp.pterodactylpanel.screens.application.server.tabs.buildconfiguration

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.utils.NumberRegex
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun BuildConfigurationTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationServerBuildConfigurationTabViewModel = viewModel(),
    server: ApplicationServer?,
    refreshIndex: Int,
    reload: (
        isRefresh: Boolean,
        onReloadFinish: () -> Unit,
        increaseRefreshIndex: Boolean,
        onError: (String) -> Unit
    ) -> Unit
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
            server = server,
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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState)
    ) {
        Container(
            title = {
                Text(
                    text = "Resource Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.cpuLimit,
                onValueChange = {
                    if (!it.text.trim().matches(NumberRegex)) return@TextInput

                    viewModel.setCpuLimit(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                enabled = !state.isLoading,
                label = "CPU Limit (%)",
                description = "Each virtual core (thread) on the system is considered to be `100%`. Setting this value to `0` will allow a server to use CPU time without restrictions",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = state.cpuPinning,
                onValueChange = {
                    viewModel.setCpuPinning(it)
                },
                label = "CPU Pinning",
                description = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Advanced: ")
                    }

                    append("Enter the specific CPU cores that this process can run on, or leave blank to allow all cores. This can be a single number, or a comma separated list. Example: `0`, `0-1,3`, or `0,1,3,4`")
                },
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.allocatedMemory,
                onValueChange = {
                    if (!it.text.matches(NumberRegex)) return@TextInput

                    viewModel.setAllocatedMemory(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Allocated Memory (MiB)",
                description = "The maximum amount of memory allowed for this container. Setting this to `0` will allow unlimited memory in a container",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.allocatedSwap,
                onValueChange = {
                    if (!it.text.matches(NumberRegex)) return@TextInput

                    viewModel.setAllocatedSwap(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Allocated Swap (MiB)",
                description = "Setting this to `0` will disable swap space on this server. Setting to `-1` will allow unlimited swap",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.diskSpaceLimit,
                onValueChange = {
                    if (!it.text.matches(NumberRegex)) return@TextInput

                    viewModel.setDiskSpaceLimit(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Disk Space Limit (MiB)",
                description = "This server will not be allowed to boot if it is using more than this amount of space. If a server goes over this limit while running it will be safely stopped and locked until enough space is available. Set to `0` to allow unlimited disk usage",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.blockIoProportion,
                onValueChange = {
                    if (!it.text.matches(NumberRegex)) return@TextInput

                    val io = it.text.toIntOrNull() ?: 0

                    when {
                        io < 10 -> viewModel.setBlockIoProportion(it.copy(text = "10"))
                        io > 1000 -> viewModel.setBlockIoProportion(it.copy(text = "1000"))
                        else -> viewModel.setBlockIoProportion(it)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Block IO Proportion",
                description = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Advanced: ")
                    }

                    append("The IO performance of this server relative to other running containers on the system. Value should be between `10` and `1000`")
                },
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Switch(
                checked = state.oomKiller,
                onCheckedChange = {
                    viewModel.setOomKiller(it)
                },
                label = "OOM Killer",
                description = "Enabling OOM killer may cause server processes to exit unexpectedly",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                enabled = !state.isLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Container(
            title = {
                Text(
                    text = "Application Features Limits",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.databaseLimit,
                onValueChange = {
                    if (!it.text.matches(NumberRegex)) return@TextInput

                    viewModel.setDatabaseLimit(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Database Limit",
                description = "The total number of databases a user is allowed to create for this server",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.allocationLimit,
                onValueChange = {
                    if (!it.text.matches(NumberRegex)) return@TextInput

                    viewModel.setAllocationLimit(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Allocation Limit",
                description = "The total number of allocations a user is allowed to create for this server",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.backupLimit,
                onValueChange = {
                    if (!it.text.matches(NumberRegex)) return@TextInput

                    viewModel.setBackupLimit(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Backup Limit",
                description = "The total number of backups that can be created for this server",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Container(
            title = {
                Text(
                    text = "Allocation Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Select(
                options = state.gamePorts.map { (_, allocation) ->
                    val allocationValue = "${allocation.alias ?: allocation.ip}:${allocation.port}"

                    SelectOption(
                        id = allocation.id.toString(),
                        searchLabel = allocationValue,
                        label = {
                            Text(
                                text = allocationValue
                            )
                        }
                    )
                },
                selectedIds = state.gamePort,
                onSelectionChange = {
                    viewModel.setGamePort(it)
                },
                searchable = true,
                label = "Game Port",
                description = "The default connection address that will be used for this game server",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Select(
                options = state.newAvailablePorts
                    .filter { !it.attributes.assigned }
                    .map { (_, allocation) ->
                        val allocationValue = "${allocation.alias ?: allocation.ip}:${allocation.port}"

                        SelectOption(
                            id = allocation.id.toString(),
                            searchLabel = allocationValue,
                            label = {
                                Text(
                                    text = allocationValue
                                )
                            }
                        )
                    },
                selectedIds = state.addNewPorts,
                onSelectionChange = {
                    viewModel.setAddNewPorts(it)
                },
                searchable = true,
                label = "Assign Additional Ports",
                description = "Please note that due to software limitations you cannot assign identical ports on different IPs to the same server",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                multiple = true
            )

            Select(
                options = state.gamePorts
                    .filter { it.attributes.id != server?.attributes?.allocation }
                    .map { (_, allocation) ->
                        val allocationValue = "${allocation.alias ?: allocation.ip}:${allocation.port}"

                        SelectOption(
                            id = allocation.id.toString(),
                            searchLabel = allocationValue,
                            label = {
                                Text(
                                    text = allocationValue
                                )
                            }
                        )
                    },
                selectedIds = state.removePorts,
                onSelectionChange = {
                    viewModel.setRemovePorts(it)
                },
                searchable = true,
                label = "Remove Additional Ports",
                description = "Simply select which ports you would like to remove from the list above. If you want to assign a port on a different IP that is already in use you can select it from the left and delete it here",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                multiple = true
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

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
                                text = "Successfully updated server build configuration",
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
                    },
                    reload = reload
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(
                text = "Update Build Configuration"
            )
        }
    }
}
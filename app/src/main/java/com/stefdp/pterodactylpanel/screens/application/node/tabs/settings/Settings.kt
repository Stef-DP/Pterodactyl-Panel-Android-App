package com.stefdp.pterodactylpanel.screens.application.node.tabs.settings

import android.R.attr.enabled
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun SettingsTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationNodeSettingsTabViewModel = viewModel(),
    node: ApplicationNode?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(node?.attributes?.id, refreshIndex) {
        if (node == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(
            context = context,
            node = node
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val scrollState = rememberScrollState()

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
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                TextInput(
                    value = state.nodeName,
                    onValueChange = {
                        if (it.text.trim().length > 100) return@TextInput

                        viewModel.setNodeName(it)
                    },
                    label = "Node Name",
                    description = "Character limits: \"a-z A-Z 0-9 _ . -\" and \"[Space]\" (min 1, max 100 characters)",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    value = state.nodeDescription,
                    onValueChange = {
                        if (it.text.trim().length > 100) return@TextInput

                        viewModel.setNodeDescription(it)
                    },
                    label = "Description",
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    enabled = !state.isLoading
                )

                Select(
                    options = state.locations.map { (_, location) ->
                        SelectOption(
                            id = location.id.toString(),
                            label = {
                                Text(
                                    text = location.short
                                )
                            }
                        )
                    },
                    label = "Location",
                    selectedIds = state.selectedNodeLocation,
                    onSelectionChange = {
                        viewModel.setSelectedNodeLocation(it)
                    },
                    enabled = !state.isLoading
                )

                Switch(
                    checked = state.nodePublic,
                    onCheckedChange = {
                        viewModel.setNodePublic(it)
                    },
                    label = "Allow Automatic Allocation",
                    description = "Allow automatic allocations to this node?",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    enabled = !state.isLoading
                )

                TextInput(
                    value = state.nodeFQDN,
                    onValueChange = {
                        viewModel.setNodeFQDN(it)
                    },
                    label = "FQDN",
                    description = "Please enter domain name (e.g \"node.example.com\") to be used for connecting to the daemon. An IP address may be used only if you are not using SSL for this node",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                Switch(
                    checked = state.nodeUseSsl,
                    onCheckedChange = {
                        viewModel.setNodeUseSsl(it)
                    },
                    label = "Communicate over SSL",
                    description = "In most cases you should select to use a SSL connection. If using an IP Address or you do not wish to use SSL at all, select a HTTP connection",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    enabled = !state.isLoading
                )

                Switch(
                    checked = state.nodeBehindProxy,
                    onCheckedChange = {
                        viewModel.setNodeBehindProxy(it)
                    },
                    label = "Behind Proxy",
                    description = "If you are running the daemon behind a proxy such as Cloudflare, select this to have the daemon skip looking for certificates on boot",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    enabled = !state.isLoading
                )

                Switch(
                    checked = state.nodeUnderMaintenance,
                    onCheckedChange = {
                        viewModel.setNodeUnderMaintenance(it)
                    },
                    label = "Maintenance Mode",
                    description = "If the node is marked as 'Under Maintenance' users won't be able to access servers that are on this node",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    enabled = !state.isLoading
                )
            }

            Container(
                title = {
                    Text(
                        text = "Allocation Limits",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                TextInput(
                    value = state.nodeTotalMemory,
                    onValueChange = {
                        viewModel.setNodeTotalMemory(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = "Total Memory (MiB)",
                    description = "Enter the total amount of memory available for new servers",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    value = state.nodeMemoryOverallocation,
                    onValueChange = {
                        val value = it.text.trim().toIntOrNull() ?: 0

                        if (value > 100) {
                            viewModel.setNodeMemoryOverallocation(it.copy(text = "100"))

                            return@TextInput
                        }

                        if (value < -1) {
                            viewModel.setNodeMemoryOverallocation(it.copy(text = "-1"))

                            return@TextInput
                        }

                        viewModel.setNodeMemoryOverallocation(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = "Memory Over-Allocation (%)",
                    description = "If you would like to allow overallocation of memory enter the percentage that you want to allow. To disable checking for overallocation enter \"-1\" into the field. Entering \"0\" will prevent creating new servers if it would put the node over the limit",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    value = state.nodeTotalDisk,
                    onValueChange = {
                        viewModel.setNodeTotalDisk(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = "Total Disk Space (MiB)",
                    description = "Enter the total amount of disk space available for new servers",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    value = state.nodeDiskOverallocation,
                    onValueChange = {
                        val value = it.text.trim().toIntOrNull() ?: 0

                        if (value > 100) {
                            viewModel.setNodeDiskOverallocation(it.copy(text = "100"))

                            return@TextInput
                        }

                        if (value < -1) {
                            viewModel.setNodeDiskOverallocation(it.copy(text = "-1"))

                            return@TextInput
                        }

                        viewModel.setNodeDiskOverallocation(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = "Disk Over-Allocation (%)",
                    description = "If you would like to allow overallocation of disk space enter the percentage that you want to allow. To disable checking for overallocation enter \"-1\" into the field. Entering \"0\" will prevent creating new servers if it would put the node over the limit",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )
            }

            Container(
                title = {
                    Text(
                        text = "General Configuration",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                TextInput(
                    value = state.nodeMaxWebUploadFileSize,
                    onValueChange = {
                        viewModel.setNodeMaxWebUploadFileSize(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = "Maximum Web Upload File Size (MiB)",
                    description = "Enter the maximum size of files that can be uploaded through the web-based file manager",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    value = state.nodeDaemonPort,
                    onValueChange = {
                        val value = it.text.trim().toIntOrNull() ?: 0

                        if (value > 65535) {
                            viewModel.setNodeDaemonPort(it.copy(text = "65535"))

                            return@TextInput
                        }

                        viewModel.setNodeDaemonPort(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = "Daemon Port",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    value = state.nodeDaemonSftpPort,
                    onValueChange = {
                        val value = it.text.trim().toIntOrNull() ?: 0

                        if (value > 65535) {
                            viewModel.setNodeDaemonSftpPort(it.copy(text = "65535"))

                            return@TextInput
                        }

                        viewModel.setNodeDaemonSftpPort(it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = "Daemon SFTP Port",
                    description = "The daemon runs its own SFTP management container and does not use the SSHd process on the main physical server. Do not use the same port that you have assigned for your physical server's SSH process",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = {
                viewModel.updateNode(
                    context = context,
                    onSuccess = {
                        Notification.show(
                            activity = activity,
                            duration = 8000L
                        ) {
                            Text(
                                text = "Node information has been updated. If any daemon settings were changed you will need to reboot it for those changes to take effect",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    onError = { error ->
                        Notification.show(
                            activity = activity,
                            duration = 16000L
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Save Changes"
            )
        }
    }
}
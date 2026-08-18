package com.stefdp.pterodactylpanel.screens.application.nodes.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.application.nodes.ApplicationNodesUiState
import com.stefdp.pterodactylpanel.screens.application.nodes.ApplicationNodesViewModel

@Composable
fun CreateNodePopup(
    activity: FragmentActivity,
    context: Context,
    state: ApplicationNodesUiState,
    viewModel: ApplicationNodesViewModel,
) {
    Popup(
        showPopup = state.showCreateNodePopup,
        onDismissRequest = {
            viewModel.hideCreateNodePopup()
        },
    ) {
        Text(
            text = "New Node",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        )

        Text(
            text = "Create a new local or remote node for servers to be installed to",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Container(
            title = {
                Text(
                    text = "Basic Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.newNodeName,
                onValueChange = {
                    if (it.text.trim().length > 100) return@TextInput

                    viewModel.setNewNodeName(it)
                },
                label = "Name",
                description = "Character limits: `a-z A-Z 0-9 _ . -` and `[Space]` (min 1, max 100 characters)",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newNodeDescription,
                onValueChange = {
                    if (it.text.trim().length > 100) return@TextInput

                    viewModel.setNewNodeDescription(it)
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
                selectedIds = state.selectedNewNodeLocation,
                onSelectionChange = {
                    viewModel.setSelectedNewNodeLocation(it)
                },
                enabled = !state.isLoading
            )

            Switch(
                checked = state.newNodePublic,
                onCheckedChange = {
                    viewModel.setNewNodePublic(it)
                },
                label = "Public Node",
                description = "By turning this off you will be denying the ability to auto-deploy to this node",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newNodeFQDN,
                onValueChange = {
                    viewModel.setNewNodeFQDN(it)
                },
                label = "FQDN",
                description = "Please enter domain name (e.g `node.example.com`) to be used for connecting to the daemon. An IP address may be used only if you are not using SSL for this node",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Switch(
                checked = state.newNodeUseSsl,
                onCheckedChange = {
                    viewModel.setNewNodeUseSsl(it)
                },
                label = "Communicate over SSL",
                description = "If your Panel is configured to use a secure connection. In order for browsers to connect to your node it must use a SSL connection",
                descriptionColor = MaterialTheme.colorScheme.error,
                enabled = !state.isLoading
            )

            Switch(
                checked = state.newNodeBehindProxy,
                onCheckedChange = {
                    viewModel.setNewNodeBehindProxy(it)
                },
                label = "Behind Proxy",
                description = "If you are running the daemon behind a proxy such as Cloudflare, select this to have the daemon skip looking for certificates on boot",
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
                   text = "Configuration",
                   style = MaterialTheme.typography.titleLarge,
                   fontWeight = FontWeight.Bold,
               )
           }
        ) {
            TextInput(
                value = state.newNodeDaemonServerFileDirectory,
                onValueChange = {
                    viewModel.setNewNodeDaemonServerFileDirectory(it)
                },
                label = "Daemon Server File Directory",
                description = "Enter the directory where server files should be stored. If you use OVH you should check your partition scheme. You may need to use `/home/daemon-data` to have enough space",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newNodeTotalMemory,
                onValueChange = {
                    viewModel.setNewNodeTotalMemory(it)
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
                value = state.newNodeMemoryOverallocation,
                onValueChange = {
                    val value = it.text.trim().toIntOrNull() ?: 0

                    if (value > 100) {
                        viewModel.setNewNodeMemoryOverallocation(it.copy(text = "100"))

                        return@TextInput
                    }

                    if (value < -1) {
                        viewModel.setNewNodeMemoryOverallocation(it.copy(text = "-1"))

                        return@TextInput
                    }

                    viewModel.setNewNodeMemoryOverallocation(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Memory Over-Allocation (%)",
                description = "If you would like to allow overallocation of memory enter the percentage that you want to allow. To disable checking for overallocation enter `-1` into the field. Entering `0` will prevent creating new servers if it would put the node over the limit",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newNodeTotalDisk,
                onValueChange = {
                    viewModel.setNewNodeTotalDisk(it)
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
                value = state.newNodeDiskOverallocation,
                onValueChange = {
                    val value = it.text.trim().toIntOrNull() ?: 0

                    if (value > 100) {
                        viewModel.setNewNodeDiskOverallocation(it.copy(text = "100"))

                        return@TextInput
                    }

                    if (value < -1) {
                        viewModel.setNewNodeDiskOverallocation(it.copy(text = "-1"))

                        return@TextInput
                    }

                    viewModel.setNewNodeDiskOverallocation(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Disk Over-Allocation (%)",
                description = "If you would like to allow overallocation of disk space enter the percentage that you want to allow. To disable checking for overallocation enter `-1` into the field. Entering `0` will prevent creating new servers if it would put the node over the limit",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newNodeDaemonPort,
                onValueChange = {
                    val value = it.text.trim().toIntOrNull() ?: 0

                    if (value > 65535) {
                        viewModel.setNewNodeDaemonPort(it.copy(text = "65535"))

                        return@TextInput
                    }

                    viewModel.setNewNodeDaemonPort(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Daemon Port",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newNodeDaemonSftpPort,
                onValueChange = {
                    val value = it.text.trim().toIntOrNull() ?: 0

                    if (value > 65535) {
                        viewModel.setNewNodeDaemonSftpPort(it.copy(text = "65535"))

                        return@TextInput
                    }

                    viewModel.setNewNodeDaemonSftpPort(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = "Daemon SFTP Port",
                description = "The daemon runs its own SFTP management container and does not use the SSHd process on the main physical server. Do not use the same port that you have assigned for your physical server's SSH process. If you will be running the daemon behind CloudFlare® you should set the daemon port to `8443` to allow websocket proxying over SSL",
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.hideCreateNodePopup()
                },
                buttonType = ButtonType.SECONDARY,
                enabled = state.nodes != null
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.createNode(
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
                                    text = "Node created successfully",
                                )
                            }
                        }
                    )
                },
                buttonType = ButtonType.PRIMARY,
                enabled = state.nodes != null
            ) {
                Text("Create Node")
            }
        }
    }
}
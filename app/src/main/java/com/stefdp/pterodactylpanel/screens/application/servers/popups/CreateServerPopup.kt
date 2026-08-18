package com.stefdp.pterodactylpanel.screens.application.servers.popups

import android.R.attr.description
import android.R.attr.label
import android.R.attr.text
import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.Select
import com.stefdp.pterodactylpanel.components.SelectOption
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.application.servers.ApplicationServersUiState
import com.stefdp.pterodactylpanel.screens.application.servers.ApplicationServersViewModel

@Composable
fun CreateServerPopup(
    activity: FragmentActivity,
    context: Context,
    state: ApplicationServersUiState,
    viewModel: ApplicationServersViewModel
) {
    Popup(
        showPopup = state.showCreateServerPopup,
        onDismissRequest = {
            viewModel.hideCreateServerPopup()
        }
    ) {
        Text(
            text = "Create Server",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        )

        Text(
            text = "Add a new server to the panel",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Container(
            title = {
                Text(
                    text = "Core Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.newServerName,
                onValueChange = {
                    viewModel.setNewServerName(it)
                },
                label = "Server Name",
                placeholder = "Server Name",
                description = "Character limits: `a-z A-Z 0-9 _ - .` and `[Space]`",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerOwner,
                onValueChange = {
                    viewModel.setNewServerOwner(it)
                },
                label = "Server Owner",
                description = "Email address of the Server Owner",
                suggestions = state.users.map { it.attributes.email },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && !state.usersLoading
            )

            TextInput(
                value = state.newServerDescription,
                onValueChange = {
                    viewModel.setNewServerDescription(it)
                },
                label = "Server Description",
                description = "A brief description of this server",
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                enabled = !state.isLoading
            )

            Switch(
                checked = state.newServerStartWhenInstalled,
                onCheckedChange = {
                    viewModel.setNewServerStartWhenInstalled(it)
                },
                label = "Start Server when Installed",
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
            val nodes = state.nodes
                .groupBy { it.attributes.locationId }
                .flatMap { (locationId, nodes) ->
                    val firstNode = nodes.firstOrNull()
                    val location = firstNode?.attributes?.relationships?.location?.attributes

                    val locationOption = SelectOption(
                        id = "location_$locationId",
                        label = {
                            Text(
                                text = "(${location?.short})",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        enabled = false
                    )

                    val nodeNames = nodes.map { (_, node) ->
                        SelectOption(
                            id = node.id.toString(),
                            label = {
                                Text(
                                    text = node.name
                                )
                            }
                        )
                    }

                    return@flatMap listOf(locationOption) + nodeNames
                }

            Select(
                options = nodes,
                label = "Node",
                description = "The node which this server will be deployed to",
                selectedIds = state.newServerNode,
                onSelectionChange = {
                    viewModel.setNewServerNode(it)
                },
                enabled = !state.isLoading && !state.nodesLoading
            )

            val node = state.nodes.find { it.attributes.id == state.newServerNode.firstOrNull()?.toLongOrNull() }

            val allocations = node?.attributes?.relationships?.allocations?.data
                ?.filter { !it.attributes.assigned }
                ?: emptyList()

            Select(
                options = allocations.map { (_, allocation) ->
                    SelectOption(
                        id = allocation.id.toString(),
                        label = {
                            Text(
                                text = "${allocation.ip}:${allocation.port}"
                            )
                        }
                    )
                },
                label = "Default Allocation",
                description = "The main allocation that will be assigned to this server",
                selectedIds = state.newServerDefaultAllocation,
                onSelectionChange = {
                    viewModel.setNewServerDefaultAllocation(it)
                },
                enabled = !state.isLoading && !state.nodesLoading
            )

            val additionalAllocations = allocations.filter { it.attributes.id.toString() != state.newServerDefaultAllocation.firstOrNull() }

            Select(
                options = additionalAllocations.map { (_, allocation) ->
                    SelectOption(
                        id = allocation.id.toString(),
                        label = {
                            Text(
                                text = "${allocation.ip}:${allocation.port}"
                            )
                        }
                    )
                },
                label = "Additional Allocations",
                description = "Additional allocations to assign to this server on creation",
                selectedIds = state.newServerAdditionalAllocations,
                onSelectionChange = {
                    viewModel.setNewServerAdditionalAllocations(it)
                },
                enabled = !state.isLoading && !state.nodesLoading,
                multiple = true
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Container(
            title = {
                Text(
                    text = "Application Feature Limits",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.newServerDatabaseLimit,
                onValueChange = {
                    viewModel.setNewServerDatabaseLimit(it)
                },
                label = "Database Limit",
                description = "The total number of databases a user is allowed to create for this server",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerAllocationLimit,
                onValueChange = {
                    viewModel.setNewServerAllocationLimit(it)
                },
                label = "Allocation Limit",
                description = "The total number of allocations a user is allowed to create for this server",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerBackupLimit,
                onValueChange = {
                    viewModel.setNewServerBackupLimit(it)
                },
                label = "Backup Limit",
                description = "The total number of backups that can be created for this server",
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
                    text = "Resource Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.newServerCpuLimit,
                onValueChange = {
                    viewModel.setNewServerCpuLimit(it)
                },
                label = "CPU Limit (%)",
                description = "If you do not want to limit CPU usage, set the value to `0`. To determine a value, take the number of threads and multiply it by 100. For example, on a quad core system without hyperthreading `(4 * 100 = 400)` there is `400%` available. To limit a server to using half of a single thread, you would set the value to `50`. To allow a server to use up to two threads, set the value to `200`",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerCpuPinning,
                onValueChange = {
                    viewModel.setNewServerCpuPinning(it)
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

                    append("Enter the specific CPU threads that this process can run on, or leave blank to allow all threads. This can be a single number, or a comma separated list. Example: `0`, `0-1,3`, or `0,1,3,4`")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerMemory,
                onValueChange = {
                    viewModel.setNewServerMemory(it)
                },
                label = "Memory (MiB)",
                description = "The maximum amount of memory allowed for this container. Setting this to `0` will allow unlimited memory in a container",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerSwap,
                onValueChange = {
                    viewModel.setNewServerSwap(it)
                },
                label = "Swap (MiB)",
                description = "Setting this to `0` will disable swap space on this server. Setting to `-1` will allow unlimited swap",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerDisk,
                onValueChange = {
                    viewModel.setNewServerDisk(it)
                },
                label = "Disk Space (MiB)",
                description = "This server will not be allowed to boot if it is using more than this amount of space. If a server goes over this limit while running it will be safely stopped and locked until enough space is available. Set to `0` to allow unlimited disk usage",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            TextInput(
                value = state.newServerIo,
                onValueChange = {
                    val io = it.text.toIntOrNull() ?: 0

                    when {
                        io < 10 -> viewModel.setNewServerIo(it.copy(text = "10"))
                        io > 1000 -> viewModel.setNewServerIo(it.copy(text = "1000"))
                        else -> viewModel.setNewServerIo(it)
                    }
                },
                label = "Block IO Weight",
                description = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Advanced: ")
                    }

                    append("The IO performance of this server relative to other running containers on the system. Value should be between `10` and `1000`. Please see ")

                    withLink(
                        link = LinkAnnotation.Url(
                            url = "https://docs.docker.com/engine/reference/run/#block-io-bandwidth-blkio-constraint"
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("this documentation")
                        }
                    }

                    append(" for more information about it")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Switch(
                checked = state.newServerEnableOOMKiller,
                onCheckedChange = {
                    viewModel.setNewServerEnableOOMKiller(it)
                },
                label = "Enable OOM Killer",
                description = "Terminates the server if it breaches the memory limits. Enabling OOM killer may cause server processes to exit unexpectedly",
                enabled = !state.isLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Container(
            title = {
                Text(
                    text = "Nest Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Select(
                options = state.nests.map { (_, nest) ->
                    SelectOption(
                        id = nest.id.toString(),
                        label = {
                            Text(
                                text = nest.name
                            )
                        }
                    )
                },
                label = "Nest",
                description = "Select the Nest that this server will be grouped under",
                selectedIds = state.newServerNest,
                onSelectionChange = {
                    viewModel.setNewServerNest(it)
                },
                enabled = !state.isLoading && !state.nestsLoading
            )

            val eggs = state.nests
                .filter { it.attributes.id == state.newServerNest.firstOrNull()?.toLongOrNull() }
                .flatMap { it.attributes.relationships?.eggs?.data ?: emptyList() }

            Select(
                options = eggs.map { (_, egg) ->
                    SelectOption(
                        id = egg.id.toString(),
                        label = {
                            Text(
                                text = egg.name
                            )
                        }
                    )
                },
                label = "Egg",
                description = "Select the Egg that will define how this server should operate",
                selectedIds = state.newServerEgg,
                onSelectionChange = {
                    viewModel.setNewServerEgg(it)
                },
                enabled = !state.isLoading && !state.nestsLoading
            )

            Switch(
                checked = state.newServerSkipEggInstallScript,
                onCheckedChange = {
                    viewModel.setNewServerSkipEggInstallScript(it)
                },
                label = "Skip Egg Install Script",
                description = "If the selected Egg has an install script attached to it, the script will run during the install. If you would like to skip this step, check this box",
                enabled = !state.isLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        val nest = state.nests.find { it.attributes.id == state.newServerNest.firstOrNull()?.toLongOrNull() }
        val egg = nest?.attributes?.relationships?.eggs?.data?.find { it.attributes.id == state.newServerEgg.firstOrNull()?.toLongOrNull() }

        Container(
            title = {
                Text(
                    text = "Docker Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
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
                selectedIds = state.newServerDockerImage,
                onSelectionChange = {
                    viewModel.setNewServerDockerImage(it)
                },
                enabled = !state.isLoading && !state.nestsLoading
            )

            TextInput(
                value = state.newServerCustomDockerImage,
                onValueChange = {
                    viewModel.setNewServerCustomDockerImage(it)
                },
                placeholder = "Or enter a custom image...",
                description = "This is the default Docker image that will be used to run this server. Select an image from the dropdown above, or enter a custom image in the text field above",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && !state.nestsLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Container(
            title = {
                Text(
                    text = "Startup Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            TextInput(
                value = state.newServerStartupCommand,
                onValueChange = {
                    viewModel.setNewServerStartupCommand(it)
                },
                description = "The following data substitutes are available for the startup command: `{{SERVER_MEMORY}}`, `{{SERVER_IP}}`, and `{{SERVER_PORT}}`. They will be replaced with the allocated memory, server IP, and server port respectively",
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && !state.nestsLoading
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Container(
            title = {
                Text(
                    text = "Service Variables",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            LaunchedEffect(state.newServerVariables) {
                Logger.debug("CodeServerPopup",
                    state.newServerVariables.joinToString(", ") { it.attributes.envVariable })
            }

            state.newServerVariables.forEach { (_, variable) ->
                TextInput(
                    value = state.newServerVariableContent[variable.envVariable] ?: TextFieldValue(variable.defaultValue),
                    onValueChange = {
                        viewModel.setNewServerVariableContent(variable.envVariable, it)
                    },
                    label = variable.name,
                    description = buildAnnotatedString {
                        if (variable.description.isNotBlank()) {
                            append(variable.description)

                            append("\n")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Access in Startup:")
                        }

                        append(" `{{${variable.envVariable}}}`")

                        append("\n")

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Validation Rules:")
                        }

                        append(" `{{${variable.rules}}}`")
                    },
                    placeholder = variable.defaultValue,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(
                text = "Create Server"
            )
        }
    }
}
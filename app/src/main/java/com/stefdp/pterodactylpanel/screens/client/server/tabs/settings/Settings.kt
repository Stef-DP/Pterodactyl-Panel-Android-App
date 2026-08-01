package com.stefdp.pterodactylpanel.screens.client.server.tabs.settings

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.utils.hasPermission
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun SettingsTab(
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerSettingsTabViewModel = viewModel(),
    server: GetServerResponse?
) {
    LaunchedEffect(server) {
        viewModel.init(server)
    }

    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(12.dp)
            .verticalScrollWithScrollbar(
                scrollState = scrollState,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val clipboardManager = LocalClipboard.current
        val coroutineScope = rememberCoroutineScope()

        if (
            hasPermission(
                isServerOwner = state.isServerOwner,
                userPermissions = state.userPermissions,
                requiredPermission = null
            )
        ) {
            Container(
                title = {
                    Text(
                        text = "SFTP Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                val sftpIp = server?.attributes?.sftpDetails?.ip ?: "0.0.0.0"
                val sftpPort = server?.attributes?.sftpDetails?.port ?: 2022

                val sftpAddress = "sftp://$sftpIp:$sftpPort"

                TextInput(
                    label = "Server Address",
                    value = TextFieldValue(sftpAddress),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = painterResource(R.drawable.content_copy),
                    onTrailingIconPress = {
                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText(
                                "SFTP Address",
                                sftpAddress
                            ).toClipEntry()

                            clipboardManager.setClipEntry(clipData)
                        }
                    }
                )

                val serverId = server?.attributes?.identifier ?: "00000000"
                val userName = LocalLoggedUser.current?.attributes?.username ?: "unknown"

                val sftpUsername = "$userName.$serverId"

                TextInput(
                    label = "Username",
                    value = TextFieldValue(sftpUsername),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = painterResource(R.drawable.content_copy),
                    onTrailingIconPress = {
                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText(
                                "SFTP Username",
                                sftpUsername
                            ).toClipEntry()

                            clipboardManager.setClipEntry(clipData)
                        }
                    }
                )

                Text(
                    text = "Your SFTP password is the same as the password you use to access this panel"
                )
            }
        }

        Container(
            title = {
                Text(
                    text = "Debug Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Node",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = server?.attributes?.node ?: "Unknown",
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp),
                    style = TextStyle(
                        lineBreak = LineBreak.Simple,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Server ID",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                val serverUuid = server?.attributes?.uuid ?: "00000000-0000-0000-0000-000000000000"

                Text(
                    text = serverUuid,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(
                            enabled = true,
                            onClick = {
                                coroutineScope.launch {
                                    val clipData = ClipData.newPlainText(
                                        "Server UUID",
                                        serverUuid
                                    ).toClipEntry()

                                    clipboardManager.setClipEntry(clipData)
                                }
                            }
                        )
                        .padding(4.dp),
                    style = TextStyle(
                        lineBreak = LineBreak.Simple,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        if (
            hasPermission(
                isServerOwner = state.isServerOwner,
                userPermissions = state.userPermissions,
                requiredPermission = ServerSubuser.Permissions.SETTINGS_RENAME
            )
        ) {
            Container(
                title = {
                    Text(
                        text = "Change Server Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                TextInput(
                    label = "Server Name",
                    value = state.newServerName,
                    onValueChange = {
                        viewModel.setNewServerName(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                TextInput(
                    label = "Server Description",
                    value = state.newServerDescription,
                    onValueChange = {
                        viewModel.setNewServerDescription(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )

                Button(
                    onClick = {
                        viewModel.renameServer(
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
                                        text = "Server renamed successfully",
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && state.newServerName.text.isNotBlank()
                ) {
                    Text(
                        text = "Save"
                    )
                }
            }
        }

        if (
            hasPermission(
                isServerOwner = state.isServerOwner,
                userPermissions = state.userPermissions,
                requiredPermission = ServerSubuser.Permissions.SETTINGS_REINSTALL
            )
        ) {
            Container(
                title = {
                    Text(
                        text = "Reinstall Server",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                Text(
                    text = "Reinstalling your server will stop it, and then re-run the installation script that initially set it up. Some files may be deleted or modified during this process, please back up your data before continuing"
                )

                Button(
                    onClick = {
                        viewModel.reinstall(
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
                                        text = "Started reinstalling server",
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    buttonType = ButtonType.ERROR,
                ) {
                    Text(
                        text = "Reinstall Server"
                    )
                }
            }
        }
    }
}
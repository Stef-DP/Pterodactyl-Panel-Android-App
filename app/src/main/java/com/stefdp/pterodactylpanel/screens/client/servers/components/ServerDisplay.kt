package com.stefdp.pterodactylpanel.screens.client.servers.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.network.client.models.Server
import com.stefdp.pterodactylpanel.network.client.models.ServerState
import com.stefdp.pterodactylpanel.network.client.models.ServerStats
import com.stefdp.pterodactylpanel.ui.theme.Green
import com.stefdp.pterodactylpanel.ui.theme.Red
import com.stefdp.pterodactylpanel.ui.theme.Yellow
import com.stefdp.pterodactylpanel.utils.shimmerable
import nl.jacobras.humanreadable.HumanReadable

@Composable
fun ServerDisplay(
    context: Context,
    server: Server,
    serverStats: ServerStats?,
    statsLoading: Boolean,
    onOpen: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                enabled = !server.attributes.isSuspended,
                onClick = onOpen
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = server.attributes.name,
                    modifier = Modifier.weight(0.9f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (server.attributes.isSuspended) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                                .background(MaterialTheme.colorScheme.error)
                                .padding(
                                    top = 4.dp,
                                    bottom = 4.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                )
                        ) {
                            Text(
                                text = "Suspended",
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom  = 8.dp
                ),
                thickness = 2.dp
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(0.65f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.dns),
                            contentDescription = "Server IP"
                        )

                        val mainAllocation = server.attributes.relationships.allocations.data.find { it.attributes.isDefault }?.attributes

                        Text(
                            text = "${mainAllocation?.ipAlias ?: mainAllocation?.ip}:${mainAllocation?.port}",
                        )
                    }

                    Row(
                        modifier = Modifier.weight(0.35f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.memory),
                            contentDescription = "Server CPU Usage"
                        )

                        val cpuUsage = String.format(
                            LocalLocale.current.platformLocale,
                            "%.2f",
                            serverStats?.attributes?.resources?.cpuAbsolute ?: 0.0
                        )

                        val cpuLimit = if (server.attributes.limits.cpu == 0L) "Unlimited" else "${server.attributes.limits.cpu}%"

                        Text(
                            text = "$cpuUsage% of $cpuLimit",
                            modifier = Modifier.shimmerable(
                                enabled = statsLoading,
                                width = 50.dp,
                                height = 20.dp
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(0.65f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.memory_alt),
                            contentDescription = "Server Memory Usage"
                        )

                        val memoryUsage = HumanReadable.fileSize(
                            serverStats?.attributes?.resources?.memoryBytes ?: 0L,
                            decimals = 2
                        )

                        val memoryLimit = if (server.attributes.limits.memory == 0L) "Unlimited" else {
                            HumanReadable.fileSize(
                                bytes = server.attributes.limits.memory * 1024L * 1024L,
                                decimals = 2
                            )
                        }

                        Text(
                            text = "$memoryUsage of $memoryLimit",
                            modifier = Modifier.shimmerable(
                                enabled = statsLoading,
                                width = 50.dp,
                                height = 20.dp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.weight(0.35f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.storage),
                            contentDescription = "Server Storage Usage"
                        )

                        val storageUsage = HumanReadable.fileSize(
                            serverStats?.attributes?.resources?.diskBytes ?: 0L,
                            decimals = 2
                        )

                        val storageLimit = if (server.attributes.limits.disk == 0L) "Unlimited" else {
                            HumanReadable.fileSize(
                                bytes = server.attributes.limits.disk * 1024L * 1024L,
                                decimals = 2
                            )
                        }

                        Text(
                            text = "$storageUsage of $storageLimit",
                            modifier = Modifier.shimmerable(
                                enabled = statsLoading,
                                width = 50.dp,
                                height = 20.dp
                            )
                        )
                    }
                }
            }
        }

        val statusColor = when (serverStats?.attributes?.currentState) {
            ServerState.OFFLINE, ServerState.SUSPENDED -> Red
            ServerState.STARTING, ServerState.STOPPING, ServerState.INSTALLING -> Yellow
            ServerState.RUNNING -> Green
            else -> Red
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(statusColor)
        ) {}
    }
}
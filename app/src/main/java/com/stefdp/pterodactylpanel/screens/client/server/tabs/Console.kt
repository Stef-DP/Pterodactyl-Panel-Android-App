package com.stefdp.pterodactylpanel.screens.client.server.tabs

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Layout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.fox2code.androidansi.AnsiParser
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.client.models.ServerState
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerUiState
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerViewModel
import com.stefdp.pterodactylpanel.screens.client.server.WebSocketConnectionStatus
import com.stefdp.pterodactylpanel.screens.client.server.components.ChartContainer
import com.stefdp.pterodactylpanel.screens.client.server.components.StatsDisplay
import com.stefdp.pterodactylpanel.ui.theme.Yellow
import com.stefdp.pterodactylpanel.utils.scrollbar
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.PopupProperties
import nl.jacobras.humanreadable.HumanReadable

@Composable
fun ConsoleTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerViewModel,
    state: ClientServerUiState
) {
    val locale = LocalLocale.current.platformLocale

    DisposableEffect(Unit) {
        viewModel.connectToWebSocket(
            context = context,
            locale = locale,
            onError = { error ->
                Notification.show(
                    activity = activity
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        onDispose {
            viewModel.disconnectFromWebSocket()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier =  Modifier
            .verticalScroll(scrollState)
            .scrollbar(
                scrollState = scrollState,
                direction = Orientation.Vertical
            )
    ) {
        Text(
            text = state.server?.attributes?.name ?: "Unknown",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    top = 12.dp
                )
                .shimmerable(
                    enabled = state.isLoading
                )
        )

        val loadingWebSocketStates = listOf(
            WebSocketConnectionStatus.CONNECTING,
            WebSocketConnectionStatus.DISCONNECTED
        )

        val isWebSocketLoading by remember(state.connectionState, state.logs) {
            mutableStateOf(state.connectionState in loadingWebSocketStates || state.logs.isEmpty())
        }

        Column(
            modifier = Modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                .height(
                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        500.dp
                    } else {
                        200.dp
                    }
                )
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val lazyColumnListState = rememberLazyListState()

            LaunchedEffect(state.logs) {
                val lastIndex = state.logs.lastIndex

                if (lastIndex >= 0) {
                    lazyColumnListState.animateScrollToItem(lastIndex)
                }
            }

            LazyColumn(
                state = lazyColumnListState,
                modifier = Modifier
                    .weight(1f)
                    .verticalLazyScrollbar(
                        listState = lazyColumnListState
                    )
                    .padding(8.dp)
            ) {
                if (isWebSocketLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(state.logs.size) { index ->
                        val log = state.logs[index]

                        AndroidView(
                            factory = { context ->
                                TextView(context).apply {
                                    setTextColor(android.graphics.Color.WHITE)
                                    setTypeface(Typeface.MONOSPACE)

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
                                    }

                                    hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
                                }
                            },
                            update = { textView ->
                                AnsiParser.setAnsiText(textView, log)
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                BasicTextField(
                    state = state.commandToSend,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                                viewModel.sendCommand()

                                true
                            } else {
                                false
                            }
                        },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        viewModel.sendCommand()
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    ),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    decorator = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (state.commandToSend.text.isEmpty()) {
                                Text(
                                    text = "Type a command...",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            innerTextField()
                        }
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 12.dp
            )
        ) {
            Button(
                onClick = {
                    viewModel.sendPowerSignal(ServerPowerSignal.START)
                },
                enabled = !isWebSocketLoading && state.status == ServerState.OFFLINE,
                buttonType = ButtonType.PRIMARY,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Start",
                    color = if (!isWebSocketLoading && state.status == ServerState.OFFLINE) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    }
                )
            }

            val disallowedRestartStates = listOf(
                ServerState.INSTALLING,
                ServerState.SUSPENDED,
            )

            Button(
                onClick = {
                    viewModel.sendPowerSignal(ServerPowerSignal.RESTART)
                },
                enabled = !isWebSocketLoading && state.status !in disallowedRestartStates,
                buttonType = ButtonType.SECONDARY,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Restart",
                    color = if (!isWebSocketLoading && state.status !in disallowedRestartStates) {
                        MaterialTheme.colorScheme.onSecondary
                    } else {
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
                    }
                )
            }

            Button(
                onClick = {
                    if (state.status == ServerState.STOPPING) {
                        viewModel.sendPowerSignal(ServerPowerSignal.KILL)
                    } else {
                        viewModel.sendPowerSignal(ServerPowerSignal.STOP)
                    }
                },
                enabled = !isWebSocketLoading && state.status != ServerState.OFFLINE,
                buttonType = ButtonType.ERROR,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (state.status == ServerState.STOPPING) {
                        "Kill"
                    } else {
                        "Stop"
                    },
                    color = if (isWebSocketLoading || state.status == ServerState.OFFLINE || state.status == ServerState.STOPPING) {
                        MaterialTheme.colorScheme.onError.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onError
                    }
                )
            }
        }

        val defaultAllocation = state.server?.attributes?.relationships?.allocations?.data?.find { it.attributes.isDefault }?.attributes

        val address = if (defaultAllocation != null) {
            "${defaultAllocation.ipAlias ?: defaultAllocation.ip}:${defaultAllocation.port}"
        } else {
            "Unknown"
        }

        StatsDisplay(
            label = "Address",
            value = address,
            loading = state.isLoading
        )

        val isServerOffline by remember(state.status) {
            mutableStateOf(state.status == ServerState.OFFLINE)
        }

        StatsDisplay(
            label = "Uptime",
            value = state.uptime,
            sideColor = if (isServerOffline) {
                MaterialTheme.colorScheme.error
            } else null,
            loading = isWebSocketLoading
        )

        val cpuLimit = if (state.server != null) {
            if (state.server.attributes.limits.cpu == 0L) "∞" else "${state.server.attributes.limits.cpu}%"
        } else {
            "Unknown"
        }

        StatsDisplay(
            label = "CPU Load",
            value = if (isServerOffline) {
                "Offline"
            } else state.cpuUsage,
            secondaryValue = if (isServerOffline) "" else "/ $cpuLimit",
            enabled = !isServerOffline,
            loading = isWebSocketLoading
        )

        val memoryLimit = if (state.server != null) {
            if (state.server.attributes.limits.memory == 0L) "∞" else {
                HumanReadable.fileSize(
                    bytes = state.server.attributes.limits.memory * 1024L * 1024L,
                    decimals = 2
                )
            }
        } else {
            "Unknown"
        }

        StatsDisplay(
            label = "Memory",
            value = if (isServerOffline) {
                "Offline"
            } else state.memoryUsage,
            secondaryValue = if (isServerOffline) "" else "/ $memoryLimit",
            enabled = !isServerOffline,
            loading = isWebSocketLoading
        )

        val diskLimit = if (state.server != null) {
            if (state.server.attributes.limits.disk == 0L) "∞" else {
                HumanReadable.fileSize(
                    bytes = state.server.attributes.limits.disk * 1024L * 1024L,
                    decimals = 2
                )
            }
        } else {
            "Unknown"
        }

        StatsDisplay(
            label = "Disk",
            value = state.diskUsage,
            secondaryValue = if (isServerOffline) "" else "/ $diskLimit",
            loading = isWebSocketLoading
        )

        StatsDisplay(
            label = "Network (Inbound)",
            value = if (isServerOffline) {
                "Offline"
            } else state.incomingNetwork,
            enabled = !isServerOffline,
            loading = isWebSocketLoading
        )

        StatsDisplay(
            label = "Network (Outbound)",
            value = if (isServerOffline) {
                "Offline"
            } else state.outgoingNetwork,
            enabled = !isServerOffline,
            loading = isWebSocketLoading
        )

        val chartSize = 250.dp

        val chartPrimaryColor = MaterialTheme.colorScheme.primary
        val chartSecondaryColor = Yellow

        LaunchedEffect(
            chartPrimaryColor,
            chartSecondaryColor
        ) {
            viewModel.updateCharts(
                primaryColor = chartPrimaryColor,
                secondaryColor = chartSecondaryColor
            )
        }

        ChartContainer(
            label = "CPU Load",
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 4.dp,
                bottom = 4.dp
            )
        ) {
            LineChart(
                data = state.cpuLoadLineChartLines,
                modifier = Modifier
                    .height(chartSize),
                dotsProperties = DotProperties(
                    enabled = false
                ),
                popupProperties = PopupProperties(
                    enabled = false
//                enabled = true,
//                containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                cornerRadius = BASE_CORNER_RADIUS.dp,
//                contentVerticalPadding = 8.dp,
//                contentHorizontalPadding = 8.dp,
//                textStyle = MaterialTheme.typography.labelLarge.copy(
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    textAlign = TextAlign.Center
//                ),
//                contentBuilder = { value ->
//                    viewModel.setCpuLoadLineChartActivePopupIndex(value.valueIndex)
//
//                    val metric = state.cpuLoadLineData[value.valueIndex]
//                    val formattedValue = String.format(
//                        locale,
//                        "%.2f%%",
//                        metric
//                    )
//
//                    return@PopupProperties formattedValue
//                },
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    count = IndicatorCount.CountBased(5),
                    contentBuilder = { value ->
                        String.format(
                            locale,
                            "%.2f%%",
                            value
                        )
                    }
                )
            )
        }

        ChartContainer(
            label = "Memory",
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 4.dp,
                bottom = 4.dp
            )
        ) {
            LineChart(
                data = state.memoryLineChartLines,
                modifier = Modifier
                    .height(chartSize),
                dotsProperties = DotProperties(
                    enabled = false
                ),
                popupProperties = PopupProperties(
                    enabled = false
//                enabled = true,
//                containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                cornerRadius = BASE_CORNER_RADIUS.dp,
//                contentVerticalPadding = 8.dp,
//                contentHorizontalPadding = 8.dp,
//                textStyle = MaterialTheme.typography.labelLarge.copy(
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    textAlign = TextAlign.Center
//                ),
//                contentBuilder = { value ->
//                    viewModel.setMemoryLineChartActivePopupIndex(value.valueIndex)
//
//                    val metric = state.memoryLineData[value.valueIndex]
//                    val formattedValue = HumanReadable.fileSize(
//                        bytes = metric.toLong(),
//                        decimals = 2
//                    )
//
//                    return@PopupProperties formattedValue
//                },
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    count = IndicatorCount.CountBased(5),
                    contentBuilder = { value ->
                        HumanReadable.fileSize(
                            bytes = value.toLong(),
                            decimals = 2
                        )
                    }
                )
            )
        }

        ChartContainer(
            label = "Network",
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 4.dp,
                bottom = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Inbound"
                    )

                    Icon(
                        painter = painterResource(R.drawable.cloud_download),
                        contentDescription = "Inbound Network Legend",
                        tint = chartSecondaryColor
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Outbound"
                    )

                    Icon(
                        painter = painterResource(R.drawable.cloud_upload),
                        contentDescription = "Outbound Network Legend",
                        tint = chartPrimaryColor
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LineChart(
                data = state.networkLineChartLines,
                modifier = Modifier
                    .height(chartSize),
                dotsProperties = DotProperties(
                    enabled = false
                ),
                popupProperties = PopupProperties(
                    enabled = false
//                enabled = true,
//                containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                cornerRadius = BASE_CORNER_RADIUS.dp,
//                contentVerticalPadding = 8.dp,
//                contentHorizontalPadding = 8.dp,
//                textStyle = MaterialTheme.typography.labelLarge.copy(
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    textAlign = TextAlign.Center
//                ),
//                contentBuilder = { value ->
//                    viewModel.setNetworkLineChartActivePopupIndex(value.valueIndex)
//
//                    val inMetric = state.networkInboundLineData[value.valueIndex]
//                    val formattedInValue = HumanReadable.fileSize(
//                        bytes = inMetric.toLong(),
//                        decimals = 2
//                    )
//
//                    val outMetric = state.networkOutboundLineData[value.valueIndex]
//                    val formattedOutValue = HumanReadable.fileSize(
//                        bytes = outMetric.toLong(),
//                        decimals = 2
//                    )
//
//                    return@PopupProperties "Inbound: $formattedInValue\nOutbound: $formattedOutValue"
//                },
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = true,
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    count = IndicatorCount.CountBased(5),
                    contentBuilder = { value ->
                        HumanReadable.fileSize(
                            bytes = value.toLong(),
                            decimals = 2
                        )
                    }
                )
            )
        }
    }
}
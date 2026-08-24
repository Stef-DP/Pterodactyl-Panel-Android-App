package com.stefdp.pterodactylpanel.screens.application.server.tabs.delete

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.screens.application.server.tabs.delete.popups.DeletePopup
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun DeleteTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationServerDeleteTabViewModel = viewModel(),
    server: ApplicationServer?,
    refreshIndex: Int,
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

        viewModel.init(server)
    }

    DeletePopup(
        navController = navController,
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Container(
            title = {
                Text(
                    text = "Safely Delete Server",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Text(
                text = buildAnnotatedString {
                    append("This action will attempt to delete the server from both the panel and daemon. If either one reports an error the action will be cancelled.\n")

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.errorContainer,
                        )
                    ) {
                        append("Deleting a server is an irreversible action. ")

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("All server data")
                        }

                        append(" (including files and users) will be removed from the system")
                    }
                }
            )

            Button(
                onClick = {
                    viewModel.showSafeDeletePopup()
                },
                enabled = !state.isLoading,
                buttonType = ButtonType.ERROR,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Safely Delete This Server"
                )
            }
        }

        Container(
            title = {
                Text(
                    text = "Force Delete Server",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Text(
                text = buildAnnotatedString {
                    append("This action will attempt to delete the server from both the panel and daemon. If the daemon does not respond, or reports an error the deletion will continue.\n")

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.errorContainer,
                        )
                    ) {
                        append("Deleting a server is an irreversible action. ")

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("All server data")
                        }

                        append(" (including files and users) will be removed from the system. This method may leave dangling files on your daemon if it reports an error")
                    }
                }
            )

            Button(
                onClick = {
                    viewModel.showForceDeletePopup()
                },
                enabled = !state.isLoading,
                buttonType = ButtonType.ERROR,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Forcibly Delete This Server"
                )
            }
        }
    }
}
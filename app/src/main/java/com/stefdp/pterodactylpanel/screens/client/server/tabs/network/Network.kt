package com.stefdp.pterodactylpanel.screens.client.server.tabs.network

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.screens.client.server.components.AllocationDisplay
import com.stefdp.pterodactylpanel.screens.client.server.tabs.network.popups.DeleteAllocationPopup
import com.stefdp.pterodactylpanel.utils.shimmerable
import com.stefdp.pterodactylpanel.utils.verticalLazyScrollbar

@Composable
fun NetworkTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientServerNetworkTabViewModel = viewModel(),
    server: GetServerResponse?
) {
    LaunchedEffect(server) {
        viewModel.init(server)

        viewModel.updateAllocations(
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

    DeleteAllocationPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        val allocatedAllocations = state.allocations.size
        val allocationLimit = server?.attributes?.featureLimits?.allocations ?: 0

        if (allocationLimit > 0) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "You are currently using $allocatedAllocations of $allocationLimit allowed allocations for this server",
                    modifier = Modifier.weight(1f)
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                if (state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.ALLOCATION_CREATE)) {
                    Button(
                        onClick = {
                            viewModel.createAllocation(
                                context = context,
                                onSuccess = {
                                    Notification.show(
                                        activity = activity,
                                        duration = 3000L
                                    ) {
                                        Text(
                                            text = "Allocation created successfully",
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
                        modifier = Modifier.weight(1f),
                        enabled = allocatedAllocations < allocationLimit && !state.isLoading
                    ) {
                        Text(
                            text = "Create Allocation"
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        val lazyColumnListState = rememberLazyListState()

        LazyColumn(
            state = lazyColumnListState,
            modifier = Modifier
                .fillMaxSize()
                .verticalLazyScrollbar(
                    listState = lazyColumnListState
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (state.isLoading) {
                items(5) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmerable(
                                enabled = true,
                                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                                height = 220.dp
                            )
                    )
                }

                return@LazyColumn
            }

            items(state.allocations.size) { index ->
                val allocation = state.allocations[index]

                AllocationDisplay(
                    allocation = allocation,
                    onDelete = {
                        viewModel.setAllocationToDelete(allocation.attributes.id)
                    },
                    onMakePrimary = {
                        viewModel.makeAllocationPrimary(
                            context = context,
                            allocationId = allocation.attributes.id,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = "Successfully made \"${allocation.attributes.ipAlias ?: allocation.attributes.ip}\" the primary allocation",
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
                    onUpdateNotes = { notes ->
                        viewModel.updateAllocationNotes(
                            context = context,
                            allocationId = allocation.attributes.id,
                            notes = notes,
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = "Successfully updated notes for \"${allocation.attributes.ipAlias ?: allocation.attributes.ip}\"",
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
                    hasDeletePermission = state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.ALLOCATION_DELETE),
                    hasUpdatePermission = state.isServerOwner || state.userPermissions.contains(ServerSubuser.Permissions.ALLOCATION_UPDATE)
                )
            }
        }
    }
}
package com.stefdp.pterodactylpanel.screens.application.users

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gravatar.types.Email
import com.gravatar.ui.components.atomic.Avatar
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Pager
import com.stefdp.pterodactylpanel.components.PullToRefreshBox
import com.stefdp.pterodactylpanel.components.table.Table
import com.stefdp.pterodactylpanel.components.table.TableCellData
import com.stefdp.pterodactylpanel.components.table.TableHeaderData
import com.stefdp.pterodactylpanel.components.table.TableRowData
import com.stefdp.pterodactylpanel.network.application.models.requests.GetUsersQuerySort
import com.stefdp.pterodactylpanel.screens.ApplicationServersScreen
import com.stefdp.pterodactylpanel.screens.ApplicationUserScreen
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.screens.application.servers.ApplicationServersScreen
import com.stefdp.pterodactylpanel.screens.application.users.popups.CreateUserPopup
import com.stefdp.pterodactylpanel.ui.theme.Green
import com.stefdp.pterodactylpanel.ui.theme.Yellow

@Composable
fun ApplicationUsersScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    innerPadding: PaddingValues,
    viewModel: ApplicationUsersViewModel = viewModel()
) {
    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null || !localLoggedUser.attributes.admin) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    fun updateData(
        filterEmail: String? = null,
        filterUuid: String? = null,
        filterUsername: String? = null,
        filterExternalId: String? = null,
        sort: GetUsersQuerySort? = null,
        isRefresh: Boolean = false,
    ) {
        viewModel.updateData(
            context = context,
            filterEmail = filterEmail,
            filterUuid = filterUuid,
            filterUsername = filterUsername,
            filterExternalId = filterExternalId,
            sort = sort,
            isRefresh = isRefresh
        )
    }

    LaunchedEffect(state.page) {
        updateData()
    }

    CreateUserPopup(
        activity = activity,
        context = context,
        state = state,
        viewModel = viewModel
    )

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            updateData(
                isRefresh = true
            )
        },
        modifier = Modifier.padding(innerPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Users",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "All registered users on the system",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.showCreateUserPopup()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.users != null
                    ) {
                        Text(
                            text = "Create New"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                val tableIdWidth = 100.dp
                val tableEmailWidth = 300.dp
                val tableClientNameWidth = 230.dp
                val tableUsernameWidth = 160.dp
                val table2FAWidth = 80.dp
                val tableServersOwnedWidth = 90.dp
                val tableCanAccessWidth = 90.dp
                val tableAvatarWidth = 80.dp

                val headers: List<TableHeaderData> = listOf(
                    TableHeaderData(
                        name = "id",
                        width = tableIdWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ID",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "email",
                        width = tableEmailWidth
                    ) {
                        Text(
                            text = "Email",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "client_name",
                        width = tableClientNameWidth
                    ) {
                        Text(
                            text = "Client Name",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "username",
                        width = tableUsernameWidth
                    ) {
                        Text(
                            text = "Username",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "2fa",
                        width = table2FAWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "2FA",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    TableHeaderData(
                        name = "servers_owned",
                        width = tableServersOwnedWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Servers Owned",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },

                    TableHeaderData(
                        name = "can_access",
                        width = tableCanAccessWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Can Access",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },

                    TableHeaderData(
                        name = "avatar",
                        width = tableAvatarWidth,
                        arrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Avatar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                val rows: List<TableRowData>? = state.users?.map { (_, user) ->
                    val userServers = user.relationships?.servers?.data ?: emptyList()

                    val ownedServers = userServers.filter { it.attributes.user == user.id }
                    val canAccessServers = userServers.filter { it.attributes.user != user.id }

                    TableRowData(
                        id = user.id.toString(),
                        cells = listOf(
                            TableCellData(
                                width = tableIdWidth,
                                arrangement = Arrangement.Center
                            ) {
                                CodeText(
                                    text =  "`${user.id}`"
                                )
                            },

                            TableCellData(
                                width = tableEmailWidth,
                                arrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                FlowRow {
                                    Text(
                                        text = user.email,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable(
                                            enabled = true,
                                            onClick = {
                                                navController.navigate(ApplicationUserScreen(user.id))
                                            }
                                        )
                                    )

                                    if (user.rootAdmin) {
                                        Icon(
                                            painter = painterResource(R.drawable.star_fill),
                                            contentDescription = "Administrator",
                                            tint = Yellow
                                        )
                                    }
                                }
                            },

                            TableCellData(
                                width = tableClientNameWidth
                            ) {
                                Text(
                                    text = "${user.lastName}, ${user.firstName}"
                                )
                            },

                            TableCellData(
                                width = tableUsernameWidth
                            ) {
                                Text(
                                    text = user.username
                                )
                            },

                            TableCellData(
                                width = table2FAWidth,
                                arrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (user.twoFactorAuthentication) {
                                            R.drawable.lock
                                        } else {
                                            R.drawable.lock_open
                                        }
                                    ),
                                    tint = if (user.twoFactorAuthentication) {
                                        Green
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    },
                                    contentDescription = if (user.twoFactorAuthentication) {
                                        "2FA Enabled"
                                    } else {
                                        "2FA Disabled"
                                    }
                                )
                            },

                            TableCellData(
                                width = tableServersOwnedWidth,
                                arrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = ownedServers.size.toString()
                                )
                            },

                            TableCellData(
                                width = tableCanAccessWidth,
                                arrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = canAccessServers.size.toString()
                                )
                            },

                            TableCellData(
                                width = tableAvatarWidth,
                                arrangement = Arrangement.Center
                            ) {
                                Avatar(
                                    email = Email(user.email),
                                    size = tableAvatarWidth / 2,
                                    modifier = Modifier.clip(CircleShape)
                                )
                            }
                        )
                    )
                }

                Table(
                    headers = headers,
                    rows = rows ?: emptyList(),
                    loading = state.users == null,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
                        ),
                    lazy = false
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Pager(
                    currentPage = state.page,
                    totalPages = state.pagination?.totalPages ?: 1,
                    enabled = !state.users.isNullOrEmpty(),
                    onFirstPageClick = {
                        viewModel.setPage(1)
                    },
                    onPreviousPageClick = {
                        viewModel.setPage(state.page - 1)
                    },
                    onCustomPageInput = { page ->
                        viewModel.setPage(page)
                    },
                    onNextPageClick = {
                        viewModel.setPage(state.page + 1)
                    },
                    onLastPageClick = {
                        viewModel.setPage(state.pagination?.totalPages ?: 1)
                    },
                )
            }
        }
    }
}

enum class Languages(
    val label: String,
    val code: String
) {
    ENGLISH(
        label = "English",
        code = "en"
    )
}
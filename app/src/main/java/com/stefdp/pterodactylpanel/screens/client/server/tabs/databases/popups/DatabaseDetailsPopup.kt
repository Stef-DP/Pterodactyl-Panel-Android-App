package com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.popups

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Popup
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.ClientServerDatabasesTabUiState
import com.stefdp.pterodactylpanel.screens.client.server.tabs.databases.ClientServerDatabasesTabViewModel
import kotlinx.coroutines.launch

@Composable
fun DatabaseDetailsPopup(
    activity: FragmentActivity,
    context: Context,
    state: ClientServerDatabasesTabUiState,
    viewModel: ClientServerDatabasesTabViewModel,
) {
    val clipboardManager = LocalClipboard.current

    val coroutineScope = rememberCoroutineScope()

    Popup(
        showPopup = state.databaseToShowDetails != null,
        onDismissRequest = {
            viewModel.setDatabaseToShowDetails(null)
        },
        scrollable = true
    ) {
        val database = state.databases.find { it.attributes.id == state.databaseToShowDetails }?.attributes ?: return@Popup

        Text(
            text = "Database Connection Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val endpoint = database.host.address + ":" + database.host.port

            TextInput(
                label = "Endpoint",
                value = TextFieldValue(endpoint),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Endpoint",
                            endpoint
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val allowedIp = database.allowedIp

            TextInput(
                label = "Connections From",
                value = TextFieldValue(if (allowedIp == "%") "$allowedIp (Any IP Address)" else allowedIp),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Connections From",
                            allowedIp
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val username = database.username

            TextInput(
                label = "Username",
                value = TextFieldValue(username),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Username",
                            username
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val password = database.relationships?.password?.attributes?.password ?: "Unknown Password"

            TextInput(
                label = "Password",
                value = TextFieldValue(password),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database Password",
                            password
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            val databaseName = database.name

            val jdbcConnectionString = if (database.relationships?.password?.attributes?.password != null) {
                "jdbc:mysql://$username:$password@$endpoint/$databaseName"
            } else {
                "Unknown JDBC Connection String"
            }

            TextInput(
                label = "JDBC Connection String",
                value = TextFieldValue(jdbcConnectionString),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = painterResource(R.drawable.content_copy),
                onTrailingIconPress = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Database JDBC Connection String",
                            jdbcConnectionString
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.setDatabaseToShowDetails(null)
                    },
                    buttonType = ButtonType.SECONDARY,
                    enabled = !state.isLoading
                ) {
                    Text("Close")
                }

                Button(
                    onClick = {
                        viewModel.rotateDatabasePassword(
                            context = context,
                            databaseId = database.id,
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
                                        text = "Database password rotated successfully",
                                    )
                                }
                            }
                        )
                    },
                    buttonType = ButtonType.ERROR,
                    enabled = !state.isLoading
                ) {
                    Text("Rotate Password")
                }
            }
        }
    }
}
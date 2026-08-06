package com.stefdp.pterodactylpanel.screens.client.accountsettings.tabs.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BuildConfig
import com.stefdp.pterodactylpanel.LocalUpdateLoggedUser
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun AppTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ClientAccountSettingsAppTabViewModel = viewModel(),
    refreshIndex: Int
) {
    val localUpdateLoggedUser = LocalUpdateLoggedUser.current

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(
                scrollState = scrollState
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Container(
            title = {
                Text(
                    text = "App Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            Text(
                text = AnnotatedString.fromHtml("<b>App Version:</b> ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )

            val appBuild = BuildConfig.BUILD_TYPE.replaceFirstChar { it.uppercase() }

            Text(
                text = AnnotatedString.fromHtml("<b>App Build:</b> $appBuild"),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )

            val directoryPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { uri: Uri? ->
                uri?.let {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    viewModel.updateDownloadFolder(
                        context = context,
                        uri = it
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.setUpdateDownloadFolderType(UpdateDownloadFolderType.FILE)

                    directoryPicker.launch(null)
                },
                buttonType = ButtonType.TERTIARY,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Change File Download Folder"
                )
            }

            Button(
                onClick = {
                    viewModel.setUpdateDownloadFolderType(UpdateDownloadFolderType.BACKUP)

                    directoryPicker.launch(null)
                },
                buttonType = ButtonType.TERTIARY,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Change Backup Download Folder"
                )
            }
        }

        Container(
            title = {
                Text(
                    text = "Authentication",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
            // TODO: add biometric auth

            Button(
                onClick = {
                    viewModel.logout(
                        context = context,
                        navController = navController,
                        localUpdateLoggedUser = localUpdateLoggedUser
                    )
                },
                buttonType = ButtonType.ERROR,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Logout"
                )
            }
        }
    }
}
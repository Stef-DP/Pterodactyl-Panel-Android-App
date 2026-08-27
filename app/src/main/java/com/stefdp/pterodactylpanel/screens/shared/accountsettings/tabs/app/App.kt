package com.stefdp.pterodactylpanel.screens.shared.accountsettings.tabs.app

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.BuildConfig
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.LocalUpdateLoggedUser
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.Notification
import com.stefdp.pterodactylpanel.components.Switch
import com.stefdp.pterodactylpanel.screens.LoginScreen
import com.stefdp.pterodactylpanel.updatemanager.UpdateManager
import com.stefdp.pterodactylpanel.utils.createBiometricPrompt
import com.stefdp.pterodactylpanel.utils.createPromptInfo
import com.stefdp.pterodactylpanel.utils.getBiometricStatus
import com.stefdp.pterodactylpanel.utils.hasNotificationsPermission
import com.stefdp.pterodactylpanel.utils.promptBiometricAuthentication
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun AppTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: AccountSettingsAppTabViewModel = viewModel(),
    update: Boolean,
) {
    val localUpdateLoggedUser = LocalUpdateLoggedUser.current

    val localLoggedUser = LocalLoggedUser.current

    if (localLoggedUser == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val state by viewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.init(update)
        viewModel.refreshBiometricAuthenticationEnabled(context)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.setHasNotificationPermission(
                    hasNotificationsPermission(context)
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setHasNotificationPermission(isGranted)

        val notificationText = if (isGranted)
            "Notifications Permission Granted"
        else
            "Notifications Permission Denied, please go to the app settings and allow it from there"

        Notification.show(
            activity = activity,
        ) {
            Text(notificationText)
        }
    }

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

            if (!state.hasNotificationPermission) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            Notification.show(
                                activity = activity,
                            ) {
                                Text(
                                    text = "Notifications Permission is automatically granted on this version of Android"
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Grant Notifications Permission"
                    )
                }
            }

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

            val updateLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode != RESULT_OK) {
                    Notification.show(
                        activity = activity,
                        duration = 3000L
                    ) {
                        Text(
                            text = "Update failed or was cancelled",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            val updateManager = UpdateManager(
                activity = activity,
                context = context,
                updateLauncher = updateLauncher
            )

            Button(
                onClick = {
                    if (state.isUpdateAvailable) {
                        viewModel.downloadUpdate(updateManager)
                    } else {
                        viewModel.checkForUpdates(
                            updateManager = updateManager,
                            onSuccess = { hasUpdate ->
                                if (hasUpdate) {
                                    Notification.show(
                                        activity = activity,
                                    ) {
                                        Text(
                                            text = if (hasUpdate) "An update is available!" else "You are on the latest version."
                                        )
                                    }
                                } else {
                                    Notification.show(
                                        activity = activity,
                                        duration = 3000L
                                    ) {
                                        Text(
                                            text = "You're on the latest version"
                                        )
                                    }
                                }
                            }
                        )
                    }
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedVisibility(
                    visible = state.isLoading && !state.isUpdateAvailable
                ) {
                    Row {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = LocalContentColor.current
                        )

                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )
                    }
                }

                AnimatedContent(
                    targetState = if (state.isUpdateAvailable) "Download Update" else "Check for updates",
                    transitionSpec = {
                        (
                            fadeIn() + slideInVertically { height -> height }
                        ) togetherWith (
                            fadeOut() + slideOutVertically { height -> -height }
                        )
                    },
                    label = "UpdateTextAnimation"
                ) { targetText ->
                    Text(text = targetText)
                }
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
            val lifecycleOwner = LocalLifecycleOwner.current

            var biometricAuthenticationStatus by rememberSaveable {
                mutableIntStateOf(getBiometricStatus(context))
            }

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        biometricAuthenticationStatus = getBiometricStatus(context)
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val enrollBiometricAuthenticationLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) {}

            Switch(
                checked = state.biometricAuthenticationEnabled,
                enabled = (
                    biometricAuthenticationStatus == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS || (
                        biometricAuthenticationStatus == androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                        )
                ),
                onCheckedChange = { checked ->
                    val biometricPrompt = createBiometricPrompt(
                        activity = activity,
                        onSuccess = {
                            viewModel.setBiometricAuthenticationEnabled(
                                context = context,
                                enabled = checked
                            )
                        },
                        onError = { _, _ ->
                            Notification.show(
                                activity = activity,
                                duration = 3000L
                            ) {
                                Text(
                                    text = "Biometric Authentication Failed",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    val biometricPromptInfo = createPromptInfo()

                    promptBiometricAuthentication(
                        activity = activity,
                        prompt = biometricPrompt,
                        promptInfo = biometricPromptInfo,
                        onBiometricNotEnrolledError = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return@promptBiometricAuthentication

                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                )
                            }

                            enrollBiometricAuthenticationLauncher.launch(enrollIntent)
                        }
                    )
                },
                label = "Unlock with Biometrics",
                description = AnnotatedString.fromHtml("Require biometric authentication to unlock the app.${
                    if (biometricAuthenticationStatus == androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        "<br> <b>NOTE:</b> You will be prompted to enroll an authentication method."
                    else
                        ""
                }"),
                descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

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
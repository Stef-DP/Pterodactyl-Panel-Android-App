package com.stefdp.pterodactylpanel.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.screens.*

const val DRAWER_CORNER_RADIUS = BASE_CORNER_RADIUS + 5

@Composable
fun Sidebar(
    onItemClick: (AppScreen) -> Unit,
    navController: NavHostController,
    closeSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentUser = LocalLoggedUser.current

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(
            topEnd = DRAWER_CORNER_RADIUS.dp,
            bottomEnd = DRAWER_CORNER_RADIUS.dp
        ),
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            NavigationDrawerItem(
                label = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.host),
                            contentDescription = "Servers Screen"
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Servers")
                    }
                },
                selected = false,
                onClick = {
                    if (currentDestination?.route != ClientServersScreen::class.qualifiedName) {
                        onItemClick(ClientServersScreen)
                    } else {
                        closeSidebar()
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )
//
//            NavigationDrawerItem(
//                label = {
//                    Row {
//                        Icon(
//                            painter = painterResource(R.drawable.draft),
//                            contentDescription = "Files Screen"
//                        )
//
//                        Spacer(
//                            modifier = Modifier.width(6.dp)
//                        )
//
//                        Text("Files")
//                    }
//                },
//                selected = false,
//                onClick = {
//                    val userId = runCatching { navBackStackEntry?.toRoute<FilesScreen>()?.userId }.getOrNull()
//                    val destination = currentDestination?.route?.split("?")?.first()
//
//                    if (
//                        destination == FilesScreen::class.qualifiedName &&
//                        userId == null
//                    ) {
//                        closeSidebar()
//                    } else {
//                        onItemClick(FilesScreen())
//                    }
//                },
//                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//            )
//
//            NavigationDrawerItem(
//                label = {
//                    Row {
//                        Icon(
//                            painter = painterResource(R.drawable.folder),
//                            contentDescription = "Folders Screen"
//                        )
//
//                        Spacer(
//                            modifier = Modifier.width(6.dp)
//                        )
//
//                        Text("Folders")
//                    }
//                },
//                selected = false,
//                onClick = {
//                    if (currentDestination?.route != FoldersScreen::class.qualifiedName) {
//                        onItemClick(FoldersScreen)
//                    } else {
//                        closeSidebar()
//                    }
//                },
//                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//            )
//
//            ExpandableDrawerSection(
//                label = {
//                    Row {
//                        Icon(
//                            painter = painterResource(R.drawable.upload),
//                            contentDescription = "Upload Menu"
//                        )
//
//                        Spacer(
//                            modifier = Modifier.width(6.dp)
//                        )
//
//                        Text("Upload")
//                    }
//                },
//            ) {
//                NavigationDrawerItem(
//                    label = {
//                        Row {
//                            Icon(
//                                painter = painterResource(R.drawable.upload_file),
//                                contentDescription = "Upload File Screen"
//                            )
//
//                            Spacer(
//                                modifier = Modifier.width(6.dp)
//                            )
//
//                            Text("File")
//                        }
//                    },
//                    selected = false,
//                    onClick = {
//                        if (currentDestination?.route != UploadFileScreen::class.qualifiedName) {
//                            onItemClick(UploadFileScreen())
//                        } else {
//                            closeSidebar()
//                        }
//                    },
//                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//                )
//
//                NavigationDrawerItem(
//                    label = {
//                        Row {
//                            Icon(
//                                painter = painterResource(R.drawable.text_fields),
//                                contentDescription = "Upload Text Screen"
//                            )
//
//                            Spacer(
//                                modifier = Modifier.width(6.dp)
//                            )
//
//                            Text("Text")
//                        }
//                    },
//                    selected = false,
//                    onClick = {
//                        if (currentDestination?.route != UploadTextScreen::class.qualifiedName) {
//                            onItemClick(UploadTextScreen())
//                        } else {
//                            closeSidebar()
//                        }
//                    },
//                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//                )
//            }
//
//            NavigationDrawerItem(
//                label = {
//                    Row {
//                        Icon(
//                            painter = painterResource(R.drawable.link_2),
//                            contentDescription = "URLs Screen"
//                        )
//
//                        Spacer(
//                            modifier = Modifier.width(6.dp)
//                        )
//
//                        Text("URLs")
//                    }
//                },
//                selected = false,
//                onClick = {
//                    if (currentDestination?.route != UrlsScreen::class.qualifiedName) {
//                        onItemClick(UrlsScreen())
//                    } else {
//                        closeSidebar()
//                    }
//                },
//                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//            )
//
//            DebugWrapper {
//                ExpandableDrawerSection(
//                    label = {
//                        Text("Debug")
//                    },
//                ) {
//                    NavigationDrawerItem(
//                        label = {
//                            Text("Login")
//                        },
//                        selected = false,
//                        onClick = { onItemClick(LoginScreen()) },
//                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//                    )
//
//                    NavigationDrawerItem(
//                        label = {
//                            Text("Loading")
//                        },
//                        selected = false,
//                        onClick = { onItemClick(LoadingScreen) },
//                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//                    )
//
//                    NavigationDrawerItem(
//                        label = {
//                            Text("Biometric Authentication")
//                        },
//                        selected = false,
//                        onClick = { onItemClick(BiometricAuthScreen) },
//                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//                    )
//
//                    NavigationDrawerItem(
//                        label = {
//                            Text("Files with User ID")
//                        },
//                        selected = false,
//                        onClick = { onItemClick(FilesScreen(DEBUG_USER_ID)) },
//                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
//                    )
//                }
//            }
        }
    }
}

@Composable
fun ExpandableDrawerSection(
    label: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Column {
        NavigationDrawerItem(
            label = label,
            selected = false,
            onClick = { expanded = !expanded },
            badge = {
                Icon(
                    painter = painterResource(R.drawable.keyboard_arrow_down),
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationState)
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp)
            ) {
                content()
            }
        }
    }
}
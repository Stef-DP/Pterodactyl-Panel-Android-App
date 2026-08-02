package com.stefdp.pterodactylpanel.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.stefdp.pterodactylpanel.R

//import com.stefdp.pterodactylpanel.screens.SettingsScreen

@Composable
fun Header(
    activity: FragmentActivity,
    context: Context,
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val outlineColor = MaterialTheme.colorScheme.outline

    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 5.dp.toPx()
                )
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .statusBarsPadding()
                .waterfallPadding()
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.menu),
                    contentDescription = "Open Sidebar",
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
//                val isInSettings = currentDestination?.route?.startsWith(SettingsScreen::class.qualifiedName ?: "") == true

                UserAvatar(
//                    enabled = !isInSettings,
                    onClick = {
//                        navController.navigate(SettingsScreen(
//                            update = isUpdateAvailable
//                        ))
                    },
                )
            }
        }
    }
}
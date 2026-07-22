package com.stefdp.pterodactylpanel.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.utils.horizontalLazyScrollbar

@Composable
fun ScrollableTabRow(
    tabs: List<Tab>,
    onTabClick: (tab: Tab) -> Unit
) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.outline)
            .horizontalLazyScrollbar(listState)
    ) {
        items(tabs.size) { index ->
            val tab = tabs[index]

            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .clickable(
                        enabled = tab.enabled,
                        onClick = { onTabClick(tab) }
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                    )
                ) {
                    if (tab.icon != null) {
                        Icon(
                            painter = tab.icon,
                            contentDescription = tab.iconContentDescription,
                            modifier = Modifier.padding(16.dp),
                            tint = if (tab.enabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            }
                        )
                    }

                    if (tab.label != null) {
                        Text(
                            text = tab.label,
                            modifier = Modifier.padding(
                                top = 16.dp,
                                bottom = 16.dp
                            ),
                            color = if (tab.enabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            if (tab.active) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

data class Tab(
    val label: String? = null,
    val icon: Painter? = null,
    val iconContentDescription: String? = null,
    val id: String,
    val active: Boolean,
    val enabled: Boolean = true,
)

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL or Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ScrollableTabRowPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    val tabs = (0..2).map { index ->
                        Tab(
                            label = "Tab $index",
                            id = "tab_$index",
                            active = index == 0,
                            enabled = true,
//                            icon = painterResource(R.drawable.storage)
                        )
                    } + Tab(
                        icon = painterResource(R.drawable.open_in_new),
                        iconContentDescription = "Open in admin view",
                        id = "admin",
                        active = false,
                    )

                    ScrollableTabRow(
                        tabs = tabs,
                        onTabClick = {}
                    )
                }
            }
        }
    }
}
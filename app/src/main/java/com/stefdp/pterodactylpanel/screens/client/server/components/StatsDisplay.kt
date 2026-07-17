package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.utils.shimmerable

@Composable
fun StatsDisplay(
    label: String,
    value: String,
    secondaryValue: String = "",
    sideColor: Color? = null,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 4.dp,
                bottom = 4.dp
            )
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(sideColor ?: MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .alignByBaseline()
                        .shimmerable(
                            enabled = loading,
                            height = 30.dp
                        )
                )

                Text(
                    text = secondaryValue,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .alignByBaseline()
                        .shimmerable(
                            enabled = loading,
                            height = 20.dp
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun StatsDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    StatsDisplay(
                        label = "CPU Usage",
                        value = "50%",
                        secondaryValue = "/ 100%",
                        sideColor = Color.Red
                    )

                    StatsDisplay(
                        label = "CPU Usage",
                        value = "50%",
                        secondaryValue = "/ 100%",
                    )

                    StatsDisplay(
                        label = "CPU Usage",
                        value = "50%",
                        secondaryValue = "/ 100%",
                        sideColor = Color.Red,
                        enabled = false
                    )

                    StatsDisplay(
                        label = "CPU Usage",
                        value = "50%",
                        secondaryValue = "/ 100%",
                        enabled = false
                    )
                }
            }
        }
    }
}
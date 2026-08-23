package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme

@Composable
fun Container(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit,
    titleArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    titleAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = titleArrangement,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(
                    topStart = BASE_CORNER_RADIUS.dp,
                    topEnd = BASE_CORNER_RADIUS.dp
                ))
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = titleAlignment,
        ) {
            title()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(
                    bottomStart = BASE_CORNER_RADIUS.dp,
                    bottomEnd = BASE_CORNER_RADIUS.dp
                ))
                .background(MaterialTheme.colorScheme.outline)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun ContainerPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    Container(
                        title = {
                            Text(
                                text = "Title",
                            )
                        }
                    ) {
                        Text("hello")
                    }
                }
            }
        }
    }
}
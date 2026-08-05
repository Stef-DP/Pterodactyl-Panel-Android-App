package com.stefdp.pterodactylpanel.screens.client.accountsettings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.network.client.models.ApiKey
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.utils.formatDate

@Composable
fun ApiKeyDisplay(
    apiKey: ApiKey,
    onDelete: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.key),
            contentDescription = "API Key",
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = apiKey.attributes.description,
            )

            val lastUsed = if (apiKey.attributes.lastUsedAt != null) {
                formatDate(
                    date = apiKey.attributes.lastUsedAt,
                )
            } else {
                "Never"
            }

            Text(
                text = "Last used: $lastUsed"
            )

            Text(
                text = apiKey.attributes.identifier,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outline)
                    .padding(4.dp),
                style = TextStyle(
                    lineBreak = LineBreak.Simple,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        IconButton(
            icon = painterResource(R.drawable.delete),
            iconContentDescription = "Delete API Key",
            iconColor = MaterialTheme.colorScheme.error,
            borderColor = MaterialTheme.colorScheme.error,
            onClick =  onDelete,
            enabled = enabled
        )
    }
}

val previewApiKey = ApiKey(
    attributes = ApiKey.Attributes(
        identifier = "ptlc_tWzLRBLnGSG",
        description = "this is a very very very very very very very very long name that will overflow twice",
        allowedIps = emptyList(),
        lastUsedAt = "2026-07-30T12:27:38+02:00",
        createdAt = "2025-12-02T15:40:19+01:00",
    )
)

@Preview
@Composable
fun ApiKeyDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.outline
            ) {
                Column {
                    ApiKeyDisplay(
                        apiKey = previewApiKey,
                        onDelete = {},
                        enabled = true
                    )
                }
            }
        }
    }
}
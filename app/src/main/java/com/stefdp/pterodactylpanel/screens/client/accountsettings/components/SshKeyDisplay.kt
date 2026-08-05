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
import com.stefdp.pterodactylpanel.network.client.models.SshKey
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.utils.formatDate

@Composable
fun SshKeyDisplay(
    sshKey: SshKey,
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
                text = sshKey.attributes.name,
            )

            val lastUsed = formatDate(
                date = sshKey.attributes.createdAt,
            )

            Text(
                text = "SHA256:" + sshKey.attributes.fingerprint,
                style = TextStyle(
                    lineBreak = LineBreak.Simple,
                    fontFamily = FontFamily.Monospace
                )
            )

            Text(
                text = "Added on: $lastUsed"
            )
        }

        IconButton(
            icon = painterResource(R.drawable.delete),
            iconContentDescription = "Delete SSH Key",
            iconColor = MaterialTheme.colorScheme.error,
            borderColor = MaterialTheme.colorScheme.error,
            onClick =  onDelete,
            enabled = enabled
        )
    }
}

val previewSshKey = SshKey(
    attributes = SshKey.Attributes(
        name = "test",
        fingerprint = "ZhqM50XThWZAb49wnKws2F2/hPseb4LdgU96M47CFvE",
        publicKey = "-----BEGIN PUBLIC KEY-----\\r\\nMCowBQYDK2VwAyEAgDE+00xbOE5uBNa+wNjEBZmtvRIH6T/GYgV3+7z3m8s=\\r\\n-----END PUBLIC KEY-----",
        createdAt = "2026-08-05T18:20:05+02:00"
    )
)

@Preview
@Composable
fun SshKeyDisplayPreview() {
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
                    SshKeyDisplay(
                        sshKey = previewSshKey,
                        onDelete = {},
                        enabled = true
                    )
                }
            }
        }
    }
}
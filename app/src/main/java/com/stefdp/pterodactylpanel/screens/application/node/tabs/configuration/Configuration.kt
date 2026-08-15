package com.stefdp.pterodactylpanel.screens.application.node.tabs.configuration

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.charleskorn.kaml.Yaml
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.CodeBlockText
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.network.application.models.responses.GetNodeConfigurationResponse
import com.stefdp.pterodactylpanel.ui.theme.yamlHighlightColors
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlinx.coroutines.launch

@Composable
fun ConfigurationTab(
    context: Context,
    activity: FragmentActivity,
    nodeConfiguration: GetNodeConfigurationResponse? = null,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Container(
            title = {
                Text(
                    text = "Configuration File",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        ) {
             val yamlString = nodeConfiguration?.let { Yaml.default.encodeToString(
                 GetNodeConfigurationResponse.serializer(), it)
             } ?: "status: \"loading\""

            CodeBlockText(
                text = yamlString,
                highlightColors = yamlHighlightColors,
            )

            CodeText(
                text = "This file should be placed in your daemon's root directory (usually `/etc/pterodactyl`) in a file called `config.yml`"
            )

            val clipboardManager = LocalClipboard.current
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText(
                            "Node Configuration",
                            yamlString
                        ).toClipEntry()

                        clipboardManager.setClipEntry(clipData)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nodeConfiguration != null
            ) {
                Text(
                    text = "Copy to Clipboard"
                )
            }
        }
    }
}
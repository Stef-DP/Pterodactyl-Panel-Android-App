package com.stefdp.pterodactylpanel.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.ui.theme.getSwitchColors
import com.stefdp.pterodactylpanel.utils.toAnnotatedString

@Composable

fun Switch(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    label: CharSequence,
    description: CharSequence? = null,
    descriptionColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = getSwitchColors()
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            CodeText(
                text = label.toAnnotatedString(),
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        if (description != null) {
            CodeText(
                text = description.toAnnotatedString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = descriptionColor
                ),
                modifier = Modifier.padding(
                    horizontal = 8.dp
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL or Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SwitchPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        enabled = true,
                        label = "test label",
                        description = "a very long description `with some` code to test how it goes on a new line hello hello hello hello hello hello hello"
                    )

                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        enabled = true,
                        label = "test label",
                        description = "a very long description `with some` code to test how it goes on a new line hello hello hello hello hello hello hello"
                    )

                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        enabled = false,
                        label = "test label",
                        description = "a very long description `with some` code to test how it goes on a new line hello hello hello hello hello hello hello"
                    )

                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        enabled = false,
                        label = "test label",
                        description = "a very long description `with some` code to test how it goes on a new line hello hello hello hello hello hello hello"
                    )
                }
            }
        }
    }
}
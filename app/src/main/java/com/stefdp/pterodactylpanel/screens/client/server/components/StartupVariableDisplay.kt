package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.ServerEggVariable
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme

@Composable
fun StartupVariableDisplay(
    variable: ServerEggVariable,
    onVariableUpdate: (value: String) -> Unit,
    enabled: Boolean
) {
    var value by rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue(variable.attributes.serverValue ?: variable.attributes.defaultValue ?: ""))
    }

    Container(
        title = {
            Text(
                text = variable.attributes.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    ) {
        TextInput(
            value = value,
            onValueChange = {
                value = it
            },
            readOnly = !variable.attributes.isEditable,
            description = variable.attributes.description.takeIf { it?.isNotBlank() == true },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = if (variable.attributes.isEditable) {
                painterResource(R.drawable.save)
            } else null,
            onTrailingIconPress = {
                onVariableUpdate(value.text)
            },
            enabled = enabled
        )
    }
}

val variablePreview = ServerEggVariable(
    attributes = ServerEggVariable.Attributes(
        name = "Variable Name",
        description = null,
        envVariable = "ENV_VARIABLE",
        defaultValue = "default_value",
        serverValue = "server_value",
        rules = "required|string|max:255",
        isEditable = true
    )
)

@Preview
@Composable
fun StartupVariableDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    StartupVariableDisplay(
                        variable = variablePreview,
                        onVariableUpdate = {},
                        enabled = true
                    )
                }
            }
        }
    }
}
package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.Button
import com.stefdp.pterodactylpanel.components.ButtonType
import com.stefdp.pterodactylpanel.components.CodeText
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.client.models.ServerAllocation
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme

@Composable
fun AllocationDisplay(
    isLoading: Boolean,
    allocation: ServerAllocation,
    onMakePrimary: () -> Unit,
    onDelete: () -> Unit,
    onUpdateNotes: (notes: String) -> Unit,
    hasDeletePermission: Boolean,
    hasUpdatePermission: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.lan),
                contentDescription = "Allocation Icon"
            )

            AllocationDisplayItem(
                title = "HOSTNAME",
                value = allocation.attributes.ipAlias ?: allocation.attributes.ip
            )

            AllocationDisplayItem(
                title = "PORT",
                value = allocation.attributes.port.toString()
            )
        }

        var notes by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) {
            mutableStateOf(TextFieldValue(allocation.attributes.notes ?: ""))
        }

        TextInput(
            value = notes,
            onValueChange = {
                notes = it
            },
            label = "Notes",
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            singleLine = false,
            enabled = hasUpdatePermission && !isLoading,
            trailingIcon = painterResource(R.drawable.save),
            onTrailingIconPress = {
                onUpdateNotes(notes.text)
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (allocation.attributes.isDefault || hasUpdatePermission) {
                Button(
                    onClick = onMakePrimary,
                    buttonType = if (allocation.attributes.isDefault) {
                        ButtonType.PRIMARY
                    } else {
                        ButtonType.TERTIARY
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !allocation.attributes.isDefault && !isLoading,
                    hideDisabledColors = !isLoading
                ) {
                    Text(
                        text = if (allocation.attributes.isDefault) {
                            "Primary"
                        } else {
                            "Make Primary"
                        }
                    )
                }
            }

            if (!allocation.attributes.isDefault && hasDeletePermission) {
                Button(
                    onClick = onDelete,
                    buttonType = ButtonType.ERROR,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun AllocationDisplayItem(
    title: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CodeText(
            text = value
        )

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

val allocationPreview1 = ServerAllocation(
    attributes = ServerAllocation.Attributes(
        id = 1,
        ip = "127.0.0.1",
        ipAlias = "localhost",
        port = 3000,
        notes = "hello world",
        isDefault = false
    )
)

val allocationPreview2 = ServerAllocation(
    attributes = ServerAllocation.Attributes(
        id = 1,
        ip = "127.0.0.2",
        ipAlias = "node.stefdp.com",
        port = 3000,
        notes = "hello world",
        isDefault = true
    )
)

@Preview
@Composable
fun AllocationDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    AllocationDisplay(
                        allocation = allocationPreview1,
                        hasDeletePermission = true,
                        hasUpdatePermission = true,
                        onMakePrimary = {},
                        onDelete = {},
                        onUpdateNotes = {},
                        isLoading = false
                    )

                    AllocationDisplay(
                        allocation = allocationPreview2,
                        hasDeletePermission = true,
                        hasUpdatePermission = true,
                        onMakePrimary = {},
                        onDelete = {},
                        onUpdateNotes = {},
                        isLoading = false
                    )
                }
            }
        }
    }
}
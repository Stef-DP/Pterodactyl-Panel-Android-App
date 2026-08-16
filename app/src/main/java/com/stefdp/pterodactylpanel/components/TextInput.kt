package com.stefdp.pterodactylpanel.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.ui.theme.getOutlinedTextFieldColors
import com.stefdp.pterodactylpanel.utils.toAnnotatedString
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    label: CharSequence? = null,
    description: CharSequence? = null,
    descriptionColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    placeholder: CharSequence? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    suggestions: List<String> = emptyList(),
    onPasswordToggle: suspend (
        currentlyVisible: Boolean
    ) -> Boolean = { true },
    trailingIcon: Painter? = null,
    trailingIconColor: Color = LocalContentColor.current,
    onTrailingIconPress: () -> Unit = {},
    trailingIconContentDescription: String? = null,
    leadingIcon: Painter? = null,
    leadingIconColor: Color = LocalContentColor.current,
    onLeadingIconPress: () -> Unit = {},
    leadingIconContentDescription: String? = null,
    colors: TextFieldColors = getOutlinedTextFieldColors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
    required: Boolean = false,
    fontFamily: FontFamily? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val filteredSuggestions = remember(value.text, suggestions) {
        if (suggestions.isEmpty()) {
            emptyList()
        } else if (value.text.isEmpty()) {
            suggestions
        } else {
            suggestions.filter { it.contains(value.text, ignoreCase = true) }
        }
    }

    fun handlePasswordToggle() {
        coroutineScope.launch {
            val proceed = onPasswordToggle(passwordVisible)

            if (proceed) {
                passwordVisible = !passwordVisible
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = containerModifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded && filteredSuggestions.isNotEmpty() && enabled,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = value,
                keyboardActions = keyboardActions,
                onValueChange = {
                    onValueChange(it)
                    expanded = true
                },
                textStyle = LocalTextStyle.current.copy(
                    color = if (enabled)
                        MaterialTheme.colorScheme.onBackground
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontFamily = fontFamily
                ),
                modifier = modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                        enabled = enabled
                    )
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused

                        if (isFocused && suggestions.isNotEmpty()) {
                            expanded = true
                        }
                    },
                readOnly = readOnly,
                singleLine = singleLine,
                label = if (label != null) {
                    {
                        Row {
                            Text(
                                text = label.toAnnotatedString(),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )

                            if (required) {
                                Text(
                                    text = "*",
                                    color = if (enabled)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else null,
                placeholder = if (placeholder != null) {
                    {
                        Text(
                            text = placeholder.toAnnotatedString(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else null,
                enabled = enabled,
                colors = colors,
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                visualTransformation = if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else VisualTransformation.None,
                keyboardOptions = keyboardOptions,
                trailingIcon = if (isPassword || trailingIcon != null) {
                    {
                        IconButton(
                            modifier = Modifier.padding(end = 4.dp),
                            enabled = enabled,
                            onClick = if (isPassword) {
                                ::handlePasswordToggle
                            } else onTrailingIconPress
                        ) {
                            Icon(
                                painter = if (isPassword) {
                                    if (passwordVisible) {
                                        painterResource(R.drawable.visibility_off)
                                    } else {
                                        painterResource(R.drawable.visibility)
                                    }
                                } else {
                                    trailingIcon as Painter
                                },
                                tint = if (enabled)
                                    trailingIconColor
                                else
                                    trailingIconColor.copy(alpha = 0.5f),
                                contentDescription = if (isPassword) {
                                    if (passwordVisible) "Hide Password" else "Show Password"
                                } else trailingIconContentDescription ?: "Unknown",
                                modifier = Modifier.requiredSize(28.dp)
                            )
                        }
                    }
                } else null,
                leadingIcon = if (leadingIcon != null) {
                    {
                        IconButton(
                            modifier = Modifier.padding(end = 4.dp),
                            onClick = onLeadingIconPress,
                            enabled = enabled
                        ) {
                            Icon(
                                painter = leadingIcon,
                                tint = if (enabled)
                                    leadingIconColor
                                else
                                    leadingIconColor.copy(alpha = 0.5f),
                                contentDescription = leadingIconContentDescription ?: "Unknown",
                                modifier = Modifier.requiredSize(28.dp)
                            )
                        }
                    }
                } else null
            )

            if (filteredSuggestions.isNotEmpty() && enabled) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = false),
                    shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                    ),
                    offset = DpOffset(x = 0.dp, y = 8.dp),
                ) {
                    filteredSuggestions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                onValueChange(
                                    TextFieldValue(
                                        text = option,
                                        selection = TextRange(option.length)
                                    )
                                )
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }

        if (description != null) {
            Text(
                text = description.toAnnotatedString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = descriptionColor
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL or Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TextInputPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    TextInput(
                        value = TextFieldValue("hello\nworld\nhello\nwold"),
                        onValueChange = {},
                        label = "label",
                        description = "description",
                        placeholder = "placeholder",
                        enabled = true,
                        isPassword = false,
                        singleLine = true,
                    )

                    TextInput(
                        value = TextFieldValue("hello\nworld\nhello\nwold"),
                        onValueChange = {},
                        label = "label",
                        description = "description",
                        placeholder = "placeholder",
                        enabled = false,
                        isPassword = false,
                        singleLine = true,
                    )

                    TextInput(
                        value = TextFieldValue("hello\nworld\nhello\nwold"),
                        onValueChange = {},
                        label = "label",
                        description = "description",
                        placeholder = "placeholder",
                        enabled = true,
                        isPassword = true,
                        singleLine = true,
                    )

                    TextInput(
                        value = TextFieldValue("hello\nworld\nhello\nwold"),
                        onValueChange = {},
                        label = "label",
                        description = "description",
                        placeholder = "placeholder",
                        enabled = false,
                        isPassword = true,
                        singleLine = true,
                    )

                    TextInput(
                        value = TextFieldValue("hello\nworld\nhello\nwold"),
                        onValueChange = {},
                        label = "label",
                        description = "description",
                        placeholder = "placeholder",
                        enabled = true,
                        isPassword = false,
                        singleLine = false,
                    )

                    TextInput(
                        value = TextFieldValue("hello\nworld\nhello\nwold"),
                        onValueChange = {},
                        label = "label",
                        description = "description",
                        placeholder = "placeholder",
                        enabled = false,
                        isPassword = false,
                        singleLine = false,
                    )
                }
            }
        }
    }
}
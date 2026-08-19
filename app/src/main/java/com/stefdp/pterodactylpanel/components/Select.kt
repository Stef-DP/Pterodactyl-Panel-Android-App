//package com.stefdp.pterodactylpanel.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ExperimentalLayoutApi
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ExposedDropdownMenuAnchorType
//import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.OutlinedTextFieldDefaults.FocusedBorderThickness
//import androidx.compose.material3.OutlinedTextFieldDefaults.UnfocusedBorderThickness
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextFieldColors
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.layout.positionOnScreen
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
//import com.stefdp.pterodactylpanel.R
//import com.stefdp.pterodactylpanel.ui.theme.getOutlinedTextFieldColors
//import com.stefdp.pterodactylpanel.utils.toAnnotatedString
//
//data class SelectOption(
//    val id: String,
//    val searchLabel: String = "", // Used for internal search matching
//    val label: @Composable (enabled: Boolean) -> Unit,
//    val enabled: Boolean = true
//)
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
//@Composable
//fun Select(
//    modifier: Modifier = Modifier,
//    containerModifier: Modifier = Modifier,
//    options: List<SelectOption>,
//    selectedIds: Set<String>,
//    onSelectionChange: (Set<String>) -> Unit,
//    multiple: Boolean = false,
//    label: CharSequence? = null,
//    enabled: Boolean = true,
//    description: CharSequence? = null,
//    descriptionColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
//    colors: TextFieldColors = getOutlinedTextFieldColors(),
//    searchable: Boolean = false,
//    onSearchQueryChange: (String) -> Unit = {},
//    searchPlaceholder: CharSequence = "Search...",
//    hideNoOptionsText: Boolean = false,
//    optionsLoading: Boolean = false,
//    optionsLoadingText: CharSequence = "Loading..."
//) {
//    var expanded by rememberSaveable { mutableStateOf(false) }
//    var tempSelectedIds by rememberSaveable { mutableStateOf(selectedIds) }
//    val interactionSource = remember { MutableInteractionSource() }
//
//    var searchQuery by rememberSaveable(
//        stateSaver = TextFieldValue.Saver
//    ) {
//        mutableStateOf(TextFieldValue(""))
//    }
//
//    val handleClose = {
//        expanded = false
//        searchQuery = TextFieldValue("")
//        onSearchQueryChange("")
//
//        if (multiple) {
//            onSelectionChange(tempSelectedIds)
//        }
//    }
//
//    var anchorY by rememberSaveable { mutableFloatStateOf(0f) }
//
//    val filteredOptions = remember(options, searchQuery.text, searchable) {
//        if (!searchable || searchQuery.text.isBlank()) {
//            options
//        } else {
//            options.filter { option ->
//                option.id.contains(searchQuery.text, ignoreCase = true) ||
//                option.searchLabel.contains(searchQuery.text, ignoreCase = true)
//            }
//        }
//    }
//
//    Column(
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        modifier = containerModifier
//    ) {
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = {
//                if (enabled) {
//                    if (expanded) {
//                        handleClose()
//                    } else {
//                        tempSelectedIds = selectedIds
//                        expanded = true
//                    }
//                }
//            },
//            modifier = modifier
//                .onGloballyPositioned { coordinates ->
//                    anchorY = coordinates.positionOnScreen().y
//                }
//                .fillMaxWidth()
//        ) {
//            BasicTextField(
//                value = if (selectedIds.isEmpty()) "" else " ",
//                onValueChange = {},
//                enabled = false,
//                readOnly = true,
//                interactionSource = interactionSource,
//                modifier = Modifier
//                    .menuAnchor(
//                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
//                        enabled = enabled
//                    )
//                    .fillMaxWidth(),
//                decorationBox = {
//                    OutlinedTextFieldDefaults.DecorationBox(
//                        value = if (selectedIds.isEmpty()) "" else " ",
//                        innerTextField = {
//                            Box(modifier = Modifier.fillMaxWidth()) {
//                                if (selectedIds.isNotEmpty()) {
//                                    if (multiple) {
//                                        FlowRow(
//                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                                        ) {
//                                            options
//                                                .filter { it.id in selectedIds }
//                                                .forEach { it.label(enabled) }
//                                        }
//                                    } else {
//                                        options
//                                            .firstOrNull { it.id == selectedIds.firstOrNull() }
//                                            ?.label(enabled)
//                                    }
//                                }
//                            }
//                        },
//                        enabled = enabled,
//                        singleLine = !multiple,
//                        visualTransformation = VisualTransformation.None,
//                        interactionSource = interactionSource,
//                        label = if (label != null) {
//                            {
//                                Text(
//                                    text = label.toAnnotatedString(),
//                                    overflow = TextOverflow.Ellipsis,
//                                    maxLines = 1,
//                                    color = if (enabled)
//                                        MaterialTheme.colorScheme.onBackground
//                                    else
//                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
//                                )
//                            }
//                        } else null,
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(
//                                expanded = expanded
//                            )
//                        },
//                        colors = colors,
//                        container = {
//                            OutlinedTextFieldDefaults.Container(
//                                enabled = enabled,
//                                isError = false,
//                                interactionSource = interactionSource,
//                                colors = colors,
//                                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
//                                focusedBorderThickness = FocusedBorderThickness,
//                                unfocusedBorderThickness = UnfocusedBorderThickness,
//                            )
//                        }
//                    )
//                }
//            )
//
//            var isDropdownAbove by rememberSaveable { mutableStateOf(false) }
//
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { handleClose() },
//                containerColor = Color.Transparent,
//                shadowElevation = 0.dp,
//                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
//                modifier = Modifier
//                    .onGloballyPositioned { coordinates ->
//                        val menuY = coordinates.positionOnScreen().y
//
//                        isDropdownAbove = menuY < anchorY
//                    }
//                    .padding(
//                        top = if (isDropdownAbove) 0.dp else 10.dp,
//                        bottom = if (isDropdownAbove) 10.dp else 0.dp
//                    )
//                    .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
//                    .background(MaterialTheme.colorScheme.surface)
//                    .border(
//                        width = 2.dp,
//                        color = MaterialTheme.colorScheme.outline,
//                        shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
//                    )
//            ) {
//                if (searchable) {
//                    OutlinedTextField(
//                        value = searchQuery,
//                        onValueChange = { newValue ->
//                            searchQuery = newValue
//
//                            onSearchQueryChange(newValue.text)
//                        },
//                        placeholder = {
//                            Text(
//                                text = searchPlaceholder.toAnnotatedString()
//                            )
//                        },
//                        leadingIcon = {
//                            Icon(
//                                painter = painterResource(R.drawable.search),
//                                contentDescription = "Search icon"
//                            )
//                        },
//                        trailingIcon = {
//                            if (searchQuery.text.isNotEmpty()) {
//                                IconButton(onClick = {
//                                    searchQuery = TextFieldValue("")
//
//                                    onSearchQueryChange("")
//                                }) {
//                                    Icon(
//                                        painter = painterResource(R.drawable.close),
//                                        contentDescription = "Clear search"
//                                    )
//                                }
//                            }
//                        },
//                        singleLine = true,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        colors = colors
//                    )
//                }
//
//                if (optionsLoading) {
//                    DropdownMenuItem(
//                        text = {
//                            Text(
//                                text = optionsLoadingText.toAnnotatedString(),
//                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                            )
//                        },
//                        onClick = {},
//                        enabled = false
//                    )
//                } else if (filteredOptions.isEmpty() && !hideNoOptionsText) {
//                    DropdownMenuItem(
//                        text = {
//                            Text(
//                                text = "No options found",
//                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                            )
//                        },
//                        onClick = {},
//                        enabled = false
//                    )
//                } else {
//                    filteredOptions.forEach { option ->
//                        val isSelected = if (multiple)
//                            option.id in tempSelectedIds
//                        else
//                            option.id in selectedIds
//
//                        DropdownMenuItem(
//                            text = {
//                                if (multiple) {
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Checkbox(
//                                            checked = isSelected,
//                                            modifier = Modifier
//                                                .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp)),
//                                            onCheckedChange = null
//                                        )
//
//                                        Spacer(
//                                            modifier = Modifier.width(8.dp)
//                                        )
//
//                                        option.label(enabled && option.enabled)
//                                    }
//                                } else {
//                                    option.label(enabled && option.enabled)
//                                }
//                            },
//                            onClick = {
//                                if (multiple) {
//                                    tempSelectedIds = if (isSelected) {
//                                        tempSelectedIds - option.id
//                                    } else {
//                                        tempSelectedIds + option.id
//                                    }
//                                } else {
//                                    onSelectionChange(setOf(option.id))
//                                    handleClose()
//                                }
//                            },
//                            modifier = if (isSelected && multiple) {
//                                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
//                            } else {
//                                Modifier
//                            },
//                            enabled = option.enabled
//                        )
//                    }
//                }
//            }
//        }
//
//        if (description != null) {
//            CodeText(
//                text = description.toAnnotatedString(),
//                style = MaterialTheme.typography.bodyMedium.copy(
//                    color = descriptionColor
//                ),
//                modifier = Modifier.padding(
//                    horizontal = 8.dp
//                )
//            )
//        }
//    }
//}

package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.FocusedBorderThickness
import androidx.compose.material3.OutlinedTextFieldDefaults.UnfocusedBorderThickness
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.ui.theme.getOutlinedTextFieldColors
import com.stefdp.pterodactylpanel.utils.toAnnotatedString

data class SelectOption(
    val id: String,
    val searchLabel: String = "",
    val label: @Composable (enabled: Boolean) -> Unit,
    val enabled: Boolean = true
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun Select(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    options: List<SelectOption>,
    selectedIds: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    multiple: Boolean = false,
    label: CharSequence? = null,
    enabled: Boolean = true,
    description: CharSequence? = null,
    descriptionColor: Color =
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    colors: TextFieldColors = getOutlinedTextFieldColors(),
    searchable: Boolean = false,
    onSearchQueryChange: (String) -> Unit = {},
    searchPlaceholder: CharSequence = "Search...",
    hideNoOptionsText: Boolean = false,
    optionsLoading: Boolean = false,
    optionsLoadingText: CharSequence = "Loading..."
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    var tempSelectedIds by rememberSaveable {
        mutableStateOf(selectedIds)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    var searchQuery by rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue(""))
    }

    val handleClose = {
        expanded = false
        searchQuery = TextFieldValue("")
        onSearchQueryChange("")

        if (multiple) {
            onSelectionChange(tempSelectedIds)
        }
    }

    var anchorY by rememberSaveable {
        mutableFloatStateOf(0f)
    }

    val filteredOptions = remember(
        options,
        searchQuery.text,
        searchable
    ) {
        if (!searchable || searchQuery.text.isBlank()) {
            options
        } else {
            options.filter { option ->
                option.id.contains(
                    searchQuery.text,
                    ignoreCase = true
                ) || option.searchLabel.contains(
                    searchQuery.text,
                    ignoreCase = true
                )
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = containerModifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (enabled) {
                    if (expanded) {
                        handleClose()
                    } else {
                        tempSelectedIds = selectedIds
                        expanded = true
                    }
                }
            },
            modifier = modifier
                .onGloballyPositioned { coordinates ->
                    anchorY = coordinates.positionOnScreen().y
                }
                .fillMaxWidth()
        ) {
            BasicTextField(
                value = if (selectedIds.isEmpty()) "" else " ",
                onValueChange = {},
                enabled = false,
                readOnly = true,
                interactionSource = interactionSource,
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = enabled
                    )
                    .fillMaxWidth(),
                decorationBox = {
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = if (selectedIds.isEmpty()) "" else " ",
                        innerTextField = {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (selectedIds.isNotEmpty()) {
                                    if (multiple) {
                                        FlowRow(
                                            horizontalArrangement =
                                                Arrangement.spacedBy(8.dp),
                                            verticalArrangement =
                                                Arrangement.spacedBy(8.dp)
                                        ) {
                                            options
                                                .filter {
                                                    it.id in selectedIds
                                                }
                                                .forEach {
                                                    it.label(enabled)
                                                }
                                        }
                                    } else {
                                        options
                                            .firstOrNull {
                                                it.id ==
                                                        selectedIds.firstOrNull()
                                            }
                                            ?.label(enabled)
                                    }
                                }
                            }
                        },
                        enabled = enabled,
                        singleLine = !multiple,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        label = if (label != null) {
                            {
                                Text(
                                    text = label.toAnnotatedString(),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = if (enabled) {
                                        MaterialTheme.colorScheme.onBackground
                                    } else {
                                        MaterialTheme.colorScheme.onBackground
                                            .copy(alpha = 0.5f)
                                    }
                                )
                            }
                        } else {
                            null
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        colors = colors,
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = enabled,
                                isError = false,
                                interactionSource = interactionSource,
                                colors = colors,
                                shape = RoundedCornerShape(
                                    BASE_CORNER_RADIUS.dp
                                ),
                                focusedBorderThickness =
                                    FocusedBorderThickness,
                                unfocusedBorderThickness =
                                    UnfocusedBorderThickness
                            )
                        }
                    )
                }
            )

            var isDropdownAbove by rememberSaveable {
                mutableStateOf(false)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    handleClose()
                },
                properties = PopupProperties(
                    focusable = true
                ),
                containerColor = Color.Transparent,
                shadowElevation = 0.dp,
                shape = RoundedCornerShape(
                    BASE_CORNER_RADIUS.dp
                ),
                modifier = Modifier
                    .exposedDropdownSize(
                        matchAnchorWidth = true
                    )
                    .onGloballyPositioned { coordinates ->
                        val menuY =
                            coordinates.positionOnScreen().y

                        isDropdownAbove = menuY < anchorY
                    }
                    .padding(
                        top = if (isDropdownAbove) {
                            0.dp
                        } else {
                            10.dp
                        },
                        bottom = if (isDropdownAbove) {
                            10.dp
                        } else {
                            0.dp
                        }
                    )
                    .clip(
                        RoundedCornerShape(
                            BASE_CORNER_RADIUS.dp
                        )
                    )
                    .background(
                        MaterialTheme.colorScheme.surface
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(
                            BASE_CORNER_RADIUS.dp
                        )
                    )
            ) {
                if (searchable) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                            onSearchQueryChange(newValue.text)
                        },
                        placeholder = {
                            Text(
                                text = searchPlaceholder.toAnnotatedString()
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(
                                    R.drawable.search
                                ),
                                contentDescription = "Search icon"
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.text.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = TextFieldValue("")
                                        onSearchQueryChange("")
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            R.drawable.close
                                        ),
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = colors
                    )
                }

                if (optionsLoading) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = optionsLoadingText.toAnnotatedString(),
                                color = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.6f)
                            )
                        },
                        onClick = {},
                        enabled = false
                    )
                } else if (
                    filteredOptions.isEmpty() &&
                    !hideNoOptionsText
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "No options found",
                                color = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.6f)
                            )
                        },
                        onClick = {},
                        enabled = false
                    )
                } else {
                    filteredOptions.forEach { option ->
                        val isSelected = if (multiple) {
                            option.id in tempSelectedIds
                        } else {
                            option.id in selectedIds
                        }

                        DropdownMenuItem(
                            text = {
                                if (multiple) {
                                    Row(
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            modifier = Modifier.clip(
                                                RoundedCornerShape(
                                                    BASE_CORNER_RADIUS.dp
                                                )
                                            ),
                                            onCheckedChange = null
                                        )

                                        Spacer(
                                            modifier = Modifier.width(8.dp)
                                        )

                                        option.label(
                                            enabled && option.enabled
                                        )
                                    }
                                } else {
                                    option.label(
                                        enabled && option.enabled
                                    )
                                }
                            },
                            onClick = {
                                if (multiple) {
                                    tempSelectedIds =
                                        if (isSelected) {
                                            tempSelectedIds - option.id
                                        } else {
                                            tempSelectedIds + option.id
                                        }
                                } else {
                                    onSelectionChange(
                                        setOf(option.id)
                                    )

                                    handleClose()
                                }
                            },
                            modifier = if (
                                isSelected && multiple
                            ) {
                                Modifier.background(
                                    MaterialTheme.colorScheme.primary
                                        .copy(alpha = 0.3f)
                                )
                            } else {
                                Modifier
                            },
                            enabled = option.enabled
                        )
                    }
                }
            }
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
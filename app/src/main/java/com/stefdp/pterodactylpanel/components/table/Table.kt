package com.stefdp.pterodactylpanel.components.table

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.utils.ScrollbarConfig
import com.stefdp.pterodactylpanel.utils.shimmerable

const val TABLE_BORDER_ALPHA = 0.3f

@Composable
fun Table(
    modifier: Modifier = Modifier,
    headers: List<TableHeaderData>,
    rows: List<TableRowData>,
    loading: Boolean,
    scrollbarConfig: TableScrollbarConfig = TableScrollbarConfig(),
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .shimmerable(
                enabled = loading,
                color = MaterialTheme.colorScheme.surfaceVariant,
                height = 250.dp
            )
    ) {
        TableHeader(
            scrollState = scrollState,
            headers = headers,
            borderColor = borderColor
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            color = borderColor.copy(alpha = TABLE_BORDER_ALPHA),
            thickness = 2.dp
        )

        TableContent(
            scrollState = scrollState,
            rows = rows,
            scrollbarConfig = scrollbarConfig,
            borderColor = borderColor
        )
    }
}

data class TableRowData(
    val cells: List<TableCellData>,
    val clickable: Boolean = false,
    val onClick: () -> Unit = {}
)

data class TableScrollbarConfig(
    val vertical: ScrollbarConfig = ScrollbarConfig(
        alwaysKeepScrollbar = true
    ),
    val horizontal: ScrollbarConfig = ScrollbarConfig()
)
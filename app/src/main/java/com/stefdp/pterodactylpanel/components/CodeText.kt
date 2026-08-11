package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.utils.toAnnotatedString

@Composable
fun CodeText(
    modifier: Modifier = Modifier,
    trailingModifier: Modifier = Modifier,
    text: CharSequence,
) {
    Text(
        text = text.toAnnotatedString(),
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 4.dp,
                bottom = 4.dp
            )
            .then(trailingModifier),
        style = TextStyle(
            lineBreak = LineBreak.Simple,
            fontFamily = FontFamily.Monospace
        )
    )
}
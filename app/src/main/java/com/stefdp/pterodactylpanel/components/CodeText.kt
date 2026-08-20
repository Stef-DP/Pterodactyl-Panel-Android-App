package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoutils.highlight.compose.extension.toAnnotatedString
import com.neoutils.highlight.core.Highlight
import com.neoutils.highlight.core.scheme.TextColorScheme
import com.stefdp.pterodactylpanel.utils.toAnnotatedString
import kotlin.math.max
import kotlin.math.min

@Composable
fun CodeBlockText(
    modifier: Modifier = Modifier,
    trailingModifier: Modifier = Modifier,
    text: String,
    highlightColors:  List<TextColorScheme> = emptyList()
) {
    val highlight = remember(highlightColors) {
        Highlight(highlightColors)
    }

    val annotatedString = remember(text, highlight) {
        highlight.toAnnotatedString(text)
    }

    Text(
        text = annotatedString,
        modifier = modifier
            .fillMaxWidth()
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

private const val InlineCodeSpacerId = "inline-code-horizontal-spacer"

@Composable
fun CodeText(
    text: CharSequence,
    modifier: Modifier = Modifier,
    trailingModifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    codeBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    codeCornerRadius: Dp = 4.dp,
    codeHorizontalPadding: Dp = 4.dp,
    codeOuterHorizontalPadding: Dp = 1.dp,
    fontWeight: FontWeight? = null
) {
    val parsedText = remember(text) { parseInlineCode(text) }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val spacerWidth = with(LocalDensity.current) {
        (codeHorizontalPadding + codeOuterHorizontalPadding)
            .coerceAtLeast(0.dp)
            .toSp()
    }

    val inlineContent = remember(spacerWidth) {
        mapOf(
            InlineCodeSpacerId to InlineTextContent(
                placeholder = Placeholder(
                    width = spacerWidth,
                    height = 1.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                Spacer(Modifier.fillMaxSize())
            },
        )
    }

    val backgroundModifier = Modifier.drawBehind {
        val layout = layoutResult ?: return@drawBehind

        drawInlineCodeBackgrounds(
            layout = layout,
            codeRanges = parsedText.codeRanges,
            color = codeBackgroundColor,
            cornerRadiusPx = codeCornerRadius.toPx(),
            horizontalPaddingPx = codeHorizontalPadding
                .coerceAtLeast(0.dp)
                .toPx(),
        )
    }

    Text(
        text = parsedText.text,
        modifier = modifier
            .then(backgroundModifier)
            .then(trailingModifier),
        color = color,
        style = style,
        textAlign = textAlign,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        fontWeight = fontWeight,
        inlineContent = inlineContent,
        onTextLayout = {
            layoutResult = it
        },
    )
}

private data class ParsedInlineCode(
    val text: AnnotatedString,
    val codeRanges: List<TextRange>,
)

private fun parseInlineCode(source: CharSequence): ParsedInlineCode {
    val codeRanges = mutableListOf<TextRange>()

    val annotatedText = buildAnnotatedString {
        var sourceOffset = 0

        while (sourceOffset < source.length) {
            val openingStart = source.indexOf('`', startIndex = sourceOffset)

            if (openingStart == -1) {
                append(source.subSequence(sourceOffset, source.length))

                break
            }

            append(source.subSequence(sourceOffset, openingStart))

            val delimiterLength = source.backtickRunLengthAt(openingStart)
            val codeStartInSource = openingStart + delimiterLength

            val closingStart = source.findMatchingBacktickRun(
                startIndex = codeStartInSource,
                runLength = delimiterLength,
            )

            if (closingStart == -1) {
                append(source.subSequence(openingStart, source.length))

                break
            }

            appendInlineContent(
                id = InlineCodeSpacerId,
                alternateText = " ",
            )

            val codeStartInOutput = length

            withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                append(source.subSequence(codeStartInSource, closingStart))
            }

            val codeEndInOutput = length

            if (codeStartInOutput < codeEndInOutput) {
                codeRanges += TextRange(codeStartInOutput, codeEndInOutput)
            }

            appendInlineContent(
                id = InlineCodeSpacerId,
                alternateText = " ",
            )

            sourceOffset = closingStart + delimiterLength
        }
    }

    return ParsedInlineCode(
        text = annotatedText,
        codeRanges = codeRanges,
    )
}

private fun CharSequence.backtickRunLengthAt(startIndex: Int): Int {
    var endIndex = startIndex

    while (endIndex < length && this[endIndex] == '`') {
        endIndex++
    }

    return endIndex - startIndex
}

private fun CharSequence.findMatchingBacktickRun(
    startIndex: Int,
    runLength: Int,
): Int {
    var index = startIndex

    while (index < length) {
        if (this[index] != '`') {
            index++

            continue
        }

        val candidateLength = backtickRunLengthAt(index)

        if (candidateLength == runLength) {
            return index
        }

        index += candidateLength
    }

    return -1
}

private fun DrawScope.drawInlineCodeBackgrounds(
    layout: TextLayoutResult,
    codeRanges: List<TextRange>,
    color: Color,
    cornerRadiusPx: Float,
    horizontalPaddingPx: Float,
) {
    val laidOutText = layout.layoutInput.text.text

    codeRanges.forEach { range ->
        val start = range.start.coerceIn(0, laidOutText.length)
        val end = range.end.coerceIn(start, laidOutText.length)

        if (start == end || layout.lineCount == 0) return@forEach

        val firstLine = layout.getLineForOffset(start)
        val lastLine = layout.getLineForOffset(end - 1)

        for (lineIndex in firstLine..lastLine) {
            val segmentStart = max(start, layout.getLineStart(lineIndex))
            val segmentEnd = min(end, layout.getLineEnd(lineIndex))

            if (segmentStart >= segmentEnd) continue

            var left = Float.POSITIVE_INFINITY
            var top = Float.POSITIVE_INFINITY
            var right = Float.NEGATIVE_INFINITY
            var bottom = Float.NEGATIVE_INFINITY

            for (offset in segmentStart until segmentEnd) {
                if (laidOutText[offset] == '\n' || laidOutText[offset] == '\r') {
                    continue
                }

                val bounds = layout.getBoundingBox(offset)

                left = min(left, bounds.left)
                top = min(top, bounds.top)
                right = max(right, bounds.right)
                bottom = max(bottom, bounds.bottom)
            }

            if (!left.isFinite() || !top.isFinite() || right <= left || bottom <= top) {
                continue
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(left - horizontalPaddingPx, top),
                size = Size(
                    width = right - left + (horizontalPaddingPx * 2),
                    height = bottom - top,
                ),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
            )
        }
    }
}
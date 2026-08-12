package com.stefdp.pterodactylpanel.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import com.stefdp.pterodactylpanel.ui.theme.SurfaceDark
import com.stefdp.pterodactylpanel.ui.theme.SurfaceVariantDark
import kotlin.math.max

data class ScrollbarConfig(
    val indicatorThickness: Dp = 4.dp,
    val indicatorColor: Color = SurfaceVariantDark.copy(alpha = 0.7f),
    val indicatorCornerRadius: Dp = indicatorThickness / 2,
    val alpha: Float? = null,
    val alphaAnimationSpec: AnimationSpec<Float>? = null,
    val padding: PaddingValues = PaddingValues(
        bottom = 5.dp,
        start = 5.dp,
        end = 5.dp,
    ),
    val alwaysKeepScrollbar: Boolean = false
)

fun Modifier.scrollbar(
    scrollState: ScrollState,
    direction: Orientation,
    config: ScrollbarConfig = ScrollbarConfig(),
): Modifier = composed {
    val isVertical = direction == Orientation.Vertical
    val layoutDirection = LocalLayoutDirection.current

    val isScrollingOrPanning by remember {
        derivedStateOf { scrollState.isScrollInProgress }
    }

    val alpha = when {
        config.alwaysKeepScrollbar -> 0.8f
        config.alpha != null -> config.alpha
        isScrollingOrPanning -> 0.8f
        else -> 0f
    }

    val alphaAnimationSpec = remember(isScrollingOrPanning, config.alwaysKeepScrollbar) {
        config.alphaAnimationSpec ?: tween(
            delayMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 0 else 1500,
            durationMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 150 else 500
        )
    }

    val scrollbarAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = alphaAnimationSpec,
        label = "ScrollbarAlpha"
    )

    val topPaddingDp = config.padding.calculateTopPadding()
    val bottomPaddingDp = config.padding.calculateBottomPadding()
    val startPaddingDp = config.padding.calculateStartPadding(layoutDirection)
    val endPaddingDp = config.padding.calculateEndPadding(layoutDirection)

    drawWithContent {
        drawContent()

        val isScrollable = scrollState.maxValue > 0

        val showScrollbar = isScrollable && (config.alwaysKeepScrollbar || isScrollingOrPanning || scrollbarAlpha > 0.0f)

        if (showScrollbar) {
            val topPadding = topPaddingDp.toPx()
            val bottomPadding = bottomPaddingDp.toPx()
            val startPadding = startPaddingDp.toPx()
            val endPadding = endPaddingDp.toPx()

            val isLtr = layoutDirection == LayoutDirection.Ltr
            val contentOffset = scrollState.value.toFloat()

            val viewPortLength = if (isVertical) size.height else size.width
            val viewPortCrossAxisLength = if (isVertical) size.width else size.height

            val contentLength = max(viewPortLength + scrollState.maxValue, 0.001f)
            val scrollbarLength = viewPortLength - (if (isVertical) topPadding + bottomPadding else startPadding + endPadding)

            val indicatorThicknessPx = config.indicatorThickness.toPx()
            val indicatorLength = max((scrollbarLength / contentLength) * viewPortLength, 20f.dp.toPx())
            val indicatorOffset = (scrollbarLength / contentLength) * contentOffset

            val scrollIndicatorSize = if (isVertical) {
                Size(indicatorThicknessPx, indicatorLength)
            } else {
                Size(indicatorLength, indicatorThicknessPx)
            }

            val scrollIndicatorPosition = if (isVertical) {
                Offset(
                    x = if (isLtr) viewPortCrossAxisLength - indicatorThicknessPx - endPadding else startPadding,
                    y = (indicatorOffset + topPadding).coerceIn(topPadding, max(topPadding, viewPortLength - bottomPadding - indicatorLength))
                )
            } else {
                Offset(
                    x = if (isLtr) {
                        (indicatorOffset + startPadding).coerceIn(startPadding, max(startPadding, viewPortLength - endPadding - indicatorLength))
                    } else {
                        (viewPortLength - indicatorOffset - indicatorLength - endPadding).coerceIn(startPadding, max(startPadding, viewPortLength - endPadding - indicatorLength))
                    },
                    y = viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding
                )
            }

            val cornerRadiusPx = config.indicatorCornerRadius.toPx()

            drawRoundRect(
                color = config.indicatorColor,
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                topLeft = scrollIndicatorPosition,
                size = scrollIndicatorSize,
                alpha = scrollbarAlpha
            )
        }
    }
}

fun Modifier.lazyScrollbar(
    listState: LazyListState,
    direction: Orientation,
    config: ScrollbarConfig = ScrollbarConfig(),
): Modifier = composed {
    val isVertical = direction == Orientation.Vertical
    val layoutDirection = LocalLayoutDirection.current

    val isScrollingOrPanning by remember {
        derivedStateOf { listState.isScrollInProgress }
    }

    val alpha = when {
        config.alwaysKeepScrollbar -> 0.8f
        config.alpha != null -> config.alpha
        isScrollingOrPanning -> 0.8f
        else -> 0f
    }

    val alphaAnimationSpec = remember(isScrollingOrPanning, config.alwaysKeepScrollbar) {
        config.alphaAnimationSpec ?: tween(
            delayMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 0 else 1500,
            durationMillis = if (isScrollingOrPanning || config.alwaysKeepScrollbar) 150 else 500
        )
    }

    val scrollbarAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = alphaAnimationSpec,
        label = "LazyScrollbarAlpha"
    )

    val topPaddingDp = config.padding.calculateTopPadding()
    val bottomPaddingDp = config.padding.calculateBottomPadding()
    val startPaddingDp = config.padding.calculateStartPadding(layoutDirection)
    val endPaddingDp = config.padding.calculateEndPadding(layoutDirection)

    drawWithContent {
        drawContent()

        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo

        val isScrollable = listState.canScrollForward || listState.canScrollBackward

        val showScrollbar = isScrollable && (config.alwaysKeepScrollbar || isScrollingOrPanning || scrollbarAlpha > 0.0f)

        if (showScrollbar && layoutInfo.totalItemsCount > 0 && visibleItems.isNotEmpty()) {
            val topPadding = topPaddingDp.toPx()
            val bottomPadding = bottomPaddingDp.toPx()
            val startPadding = startPaddingDp.toPx()
            val endPadding = endPaddingDp.toPx()

            val isLtr = layoutDirection == LayoutDirection.Ltr
            val totalItems = layoutInfo.totalItemsCount

            val viewPortLength = if (isVertical) size.height else size.width
            val viewPortCrossAxisLength = if (isVertical) size.width else size.height
            val scrollbarLength = viewPortLength - (if (isVertical) topPadding + bottomPadding else startPadding + endPadding)

            val averageVisibleItemSize = visibleItems.sumOf { it.size }.toFloat() / visibleItems.size
            val estimatedTotalLength = max(totalItems * averageVisibleItemSize, 1f)

            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset
            val scrollOffset = (firstVisibleIndex * averageVisibleItemSize) + firstVisibleOffset

            val indicatorThicknessPx = config.indicatorThickness.toPx()
            val indicatorLength = max((viewPortLength / estimatedTotalLength) * scrollbarLength, 20f.dp.toPx())
            val indicatorOffset = (scrollOffset / estimatedTotalLength) * scrollbarLength

            val scrollIndicatorSize = if (isVertical) {
                Size(indicatorThicknessPx, indicatorLength)
            } else {
                Size(indicatorLength, indicatorThicknessPx)
            }

            val scrollIndicatorPosition = if (isVertical) {
                Offset(
                    x = if (isLtr) viewPortCrossAxisLength - indicatorThicknessPx - endPadding else startPadding,
                    y = (indicatorOffset + topPadding).coerceIn(topPadding, max(topPadding, viewPortLength - bottomPadding - indicatorLength))
                )
            } else {
                Offset(
                    x = if (isLtr) {
                        (indicatorOffset + startPadding).coerceIn(startPadding, max(startPadding, viewPortLength - endPadding - indicatorLength))
                    } else {
                        (viewPortLength - indicatorOffset - indicatorLength - endPadding).coerceIn(startPadding, max(startPadding, viewPortLength - endPadding - indicatorLength))
                    },
                    y = viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding
                )
            }

            val cornerRadiusPx = config.indicatorCornerRadius.toPx()

            drawRoundRect(
                color = config.indicatorColor,
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                topLeft = scrollIndicatorPosition,
                size = scrollIndicatorSize,
                alpha = scrollbarAlpha
            )
        }
    }
}

fun Modifier.verticalScrollWithScrollbar(
    scrollState: ScrollState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this
    .scrollbar(
        scrollState = scrollState,
        direction = Orientation.Vertical,
        config = scrollbarConfig
    )
    .verticalScroll(
        state = scrollState,
        enabled = enabled,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling
    )

fun Modifier.horizontalScrollWithScrollbar(
    scrollState: ScrollState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this
    .scrollbar(
        scrollState = scrollState,
        direction = Orientation.Horizontal,
        config = scrollbarConfig
    )
    .horizontalScroll(
        state = scrollState,
        enabled = enabled,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling
    )

fun Modifier.verticalLazyScrollbar(
    listState: LazyListState,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this.lazyScrollbar(
    listState = listState,
    direction = Orientation.Vertical,
    config = scrollbarConfig
)

fun Modifier.horizontalLazyScrollbar(
    listState: LazyListState,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this.lazyScrollbar(
    listState = listState,
    direction = Orientation.Horizontal,
    config = scrollbarConfig
)
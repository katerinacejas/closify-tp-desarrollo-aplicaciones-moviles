package com.closify.myapplication.ui.screens.planner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.ui.viewmodel.PlannerUiState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun OutfitCarouselArea(
    uiState: PlannerUiState,
    onTopAndOuterwearCentered: (String) -> Unit,
    onBottomCentered: (String) -> Unit,
    onFootwearCentered: (String) -> Unit,
    onFullBodyCentered: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        if (uiState.useFullBody) {
            SnapGarmentCarousel(
                garments = uiState.fullBodyGarments,
                itemWidth = 270.dp,
                itemHeight = 418.dp,
                sideItemAlpha = 0.38f,
                contentScale = ContentScale.FillHeight,
                pageSpacing = 28.dp,
                selectedGarmentId = uiState.selectedFullBodyGarmentId,
                onCenteredGarmentChange = onFullBodyCentered,
                modifier = Modifier.height(430.dp)
            )
            SnapGarmentCarousel(
                garments = uiState.footwearGarments,
                itemWidth = 154.dp,
                itemHeight = 94.dp,
                sideItemAlpha = 0.48f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedFootwearGarmentId,
                onCenteredGarmentChange = onFootwearCentered,
                modifier = Modifier.height(110.dp)
            )
        } else {
            SnapGarmentCarousel(
                garments = uiState.topAndOuterwearGarments,
                itemWidth = 172.dp,
                itemHeight = 168.dp,
                sideItemAlpha = 0.38f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedTopAndOuterwearGarmentId,
                onCenteredGarmentChange = onTopAndOuterwearCentered,
                modifier = Modifier.height(178.dp)
            )
            SnapGarmentCarousel(
                garments = uiState.bottomGarments,
                itemWidth = 172.dp,
                itemHeight = 178.dp,
                sideItemAlpha = 0.38f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedBottomGarmentId,
                onCenteredGarmentChange = onBottomCentered,
                modifier = Modifier.height(188.dp)
            )
            SnapGarmentCarousel(
                garments = uiState.footwearGarments,
                itemWidth = 154.dp,
                itemHeight = 92.dp,
                sideItemAlpha = 0.48f,
                contentScale = ContentScale.Fit,
                pageSpacing = 34.dp,
                selectedGarmentId = uiState.selectedFootwearGarmentId,
                onCenteredGarmentChange = onFootwearCentered,
                modifier = Modifier.height(104.dp)
            )
        }
    }
}

@Composable
internal fun SnapGarmentCarousel(
    garments: List<Garment>,
    itemWidth: Dp,
    itemHeight: Dp,
    sideItemAlpha: Float,
    contentScale: ContentScale,
    pageSpacing: Dp,
    selectedGarmentId: String?,
    onCenteredGarmentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (garments.isEmpty()) return

    val selectedPage = selectedGarmentId
        ?.let { id -> garments.indexOfFirst { it.id == id } }
        ?.takeIf { it >= 0 }

    val pagerState = rememberPagerState(
        initialPage = selectedPage ?: if (garments.size > 1) 1 else 0,
        pageCount = { garments.size }
    )

    LaunchedEffect(pagerState, garments) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                garments.getOrNull(page)?.id?.let(onCenteredGarmentChange)
            }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sidePadding = if (maxWidth > itemWidth) (maxWidth - itemWidth) / 2 else 0.dp

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(itemWidth),
            contentPadding = PaddingValues(horizontal = sidePadding),
            pageSpacing = pageSpacing,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            Box(
                modifier = Modifier.width(itemWidth).height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                PlannerGarmentImage(
                    garment = garments[page],
                    alpha = if (pagerState.currentPage == page) 1f else sideItemAlpha,
                    contentScale = contentScale,
                    modifier = Modifier.width(itemWidth).height(itemHeight)
                )
            }
        }
    }
}

@Composable
internal fun PlannerGarmentImage(
    garment: Garment,
    alpha: Float,
    contentScale: ContentScale,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(garment.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = garment.name,
        contentScale = contentScale,
        alpha = alpha,
        modifier = modifier
    )
}

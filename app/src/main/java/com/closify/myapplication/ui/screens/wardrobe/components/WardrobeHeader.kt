package com.closify.myapplication.ui.screens.wardrobe.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.closify.myapplication.ui.components.ClosifyTopBar

@Composable
fun WardrobeHeader(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ClosifyTopBar(
        modifier = modifier,
        showBackButton = onBackClick != null,
        onBackClick = { onBackClick?.invoke() },
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChanged,
        searchPlaceholder = "¿Qué prenda buscas?"
    )
}

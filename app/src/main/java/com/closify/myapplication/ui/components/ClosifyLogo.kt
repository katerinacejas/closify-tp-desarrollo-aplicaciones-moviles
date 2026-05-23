package com.closify.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.ui.theme.ClosifyTheme

@Composable
fun ClosifyLogo(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    contentDescription: String? = "Closify"
) {
    Image(
        painter = painterResource(id = R.drawable.ic_closify_logo),
        contentDescription = contentDescription,
        modifier = modifier.size(size)
    )
}

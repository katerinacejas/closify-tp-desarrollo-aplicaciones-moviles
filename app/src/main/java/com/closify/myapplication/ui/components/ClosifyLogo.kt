package com.closify.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.ui.theme.ClosifyTheme

@Composable
fun ClosifyLogo(
    size: Dp = 72.dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Closify"
) {
    Image(
        painter = painterResource(id = R.drawable.ic_closify_logo),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(size)
            .then(modifier)
    )
}

@Preview(showBackground = true)
@Composable
private fun ClosifyLogoPreview() {
    ClosifyTheme {
        ClosifyLogo()
    }
}

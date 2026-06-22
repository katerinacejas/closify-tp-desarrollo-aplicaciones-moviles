package com.closify.myapplication.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.closify.myapplication.ui.components.ClosifyLogo

@Composable
fun AuthBrandHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ClosifyLogo(size = 116.dp)

        Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))

        Text(
            text = "Closify",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))

        Text(
            text = "Redescubr\u00ED tu closet, simplific\u00E1 tu d\u00EDa",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

package com.closify.myapplication.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.closify.myapplication.ui.theme.ClosifyTheme

@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(50.dp)
    val backgroundColor = when {
        selected && enabled  -> MaterialTheme.colorScheme.primaryContainer
        selected && !enabled -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else                 -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        selected             -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (enabled) 1f else 0.6f)
        !enabled             -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        else                 -> MaterialTheme.colorScheme.onBackground
    }
    val borderColor = when {
        selected             -> MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 0.3f else 0.2f)
        else                 -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }

    Surface(
        color = backgroundColor,
        shape = shape,
        modifier = modifier
            .clip(shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectableChipPreview() {
    ClosifyTheme {
        SelectableChip(label = "Templado", selected = false, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectableChipSelectedPreview() {
    ClosifyTheme {
        SelectableChip(label = "Templado", selected = true, onClick = {})
    }
}

package com.closify.myapplication.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.ui.components.SelectableChip

private val occasionOptions = listOf(
    Occasion.WORK     to "Trabajo",
    Occasion.ACADEMIC to "Académico",
    Occasion.CASUAL   to "Paseo",
    Occasion.PARTY    to "Fiesta",
    Occasion.CHILL    to "Chill",
    Occasion.ELEGANT  to "Elegante",
    Occasion.ANY      to "Indistinto"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OccasionSection(
    selectedOccasion: Occasion?,
    onOccasionSelected: (Occasion) -> Unit
) {
    SectionCard(title = "OCASIÓN") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            occasionOptions.forEach { (occasion, label) ->
                SelectableChip(
                    label = label,
                    selected = selectedOccasion == occasion,
                    onClick = { onOccasionSelected(occasion) }
                )
            }
        }
    }
}

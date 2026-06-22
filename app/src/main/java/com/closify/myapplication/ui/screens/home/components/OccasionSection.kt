package com.closify.myapplication.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.ui.components.SelectableChip

private val occasionOptions = listOf(
    Occasion.WORK     to R.string.occasion_work,
    Occasion.ACADEMIC to R.string.occasion_academic,
    Occasion.CASUAL   to R.string.occasion_casual,
    Occasion.PARTY    to R.string.occasion_party,
    Occasion.CHILL    to R.string.occasion_chill,
    Occasion.ELEGANT  to R.string.occasion_elegant,
    Occasion.ANY      to R.string.occasion_any
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OccasionSection(
    selectedOccasion: Occasion?,
    onOccasionSelected: (Occasion) -> Unit
) {
    SectionCard(title = stringResource(R.string.home_occasion_title)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            occasionOptions.forEach { (occasion, labelRes) ->
                SelectableChip(
                    label = stringResource(labelRes),
                    selected = selectedOccasion == occasion,
                    onClick = { onOccasionSelected(occasion) }
                )
            }
        }
    }
}

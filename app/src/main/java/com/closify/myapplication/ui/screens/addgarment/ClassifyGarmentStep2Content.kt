package com.closify.myapplication.ui.screens.addgarment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.screens.addgarment.components.ClassifyGarmentHeader
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentEvent
import com.closify.myapplication.ui.viewmodel.ClassifyGarmentUiState

private val occasionOptions = listOf(
    Occasion.PARTY    to R.string.occasion_party,
    Occasion.WORK     to R.string.occasion_work,
    Occasion.CASUAL   to R.string.occasion_casual,
    Occasion.ACADEMIC to R.string.occasion_academic,
    Occasion.CHILL    to R.string.occasion_chill,
    Occasion.ELEGANT  to R.string.occasion_elegant,
    Occasion.ANY      to R.string.occasion_any
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassifyGarmentStep2Content(
    uiState: ClassifyGarmentUiState,
    onEvent: (ClassifyGarmentEvent) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ClassifyGarmentHeader(imageUri = uiState.imageUri, imageSizeFraction = 0.45f)

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.classify_occasion_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    occasionOptions.forEach { (occasion, labelRes) ->
                        SelectableChip(
                            label = stringResource(labelRes),
                            selected = occasion in uiState.selectedOccasions,
                            onClick = { onEvent(ClassifyGarmentEvent.ToggleOccasion(occasion)) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        uiState.generalError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            ) {
                Text(
                    text = stringResource(R.string.common_cancel),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Button(
                onClick = { onEvent(ClassifyGarmentEvent.Save) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            ) {
                Text(
                    text = stringResource(R.string.classify_save_garment),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

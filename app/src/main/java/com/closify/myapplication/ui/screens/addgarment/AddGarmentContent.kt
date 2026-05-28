package com.closify.myapplication.ui.screens.addgarment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.closify.myapplication.ui.screens.home.components.AutoManualToggle
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.theme.LavandaAccent
import com.closify.myapplication.ui.viewmodel.CameraEvent
import com.closify.myapplication.ui.viewmodel.CameraMode

@Composable
fun AddGarmentContent(
    selectedMode: CameraMode,
    onEvent: (CameraEvent) -> Unit,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Nueva prenda",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Utiliza la camara o subí una foto de tu prenda\npara guardarla en tu guardarropas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle CAMARA / SUBIR DE LA GALERIA
        AutoManualToggle(
            isAuto = selectedMode == CameraMode.CAMERA,
            onToggle = { isCamera ->
                onEvent(
                    CameraEvent.SelectMode(
                        if (isCamera) CameraMode.CAMERA else CameraMode.GALLERY
                    )
                )
            },
            labelAuto = "CAMARA",
            labelManual = "SUBIR DE LA GALERIA",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card con borde punteado — clickeable para abrir galería o cámara
        DashedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (selectedMode == CameraMode.GALLERY)
                        Icons.Default.FileUpload
                    else
                        Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (selectedMode == CameraMode.GALLERY)
                        "Adjuntá una prenda de tu galeria"
                    else
                        "Captura tu prenda",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tene en cuenta una iluminación adecuada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DashedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    dashColor: Color = LavandaAccent,
    content: @Composable () -> Unit
) {
    val borderColor = dashColor
    Card(
        modifier = modifier.drawBehind {
            val strokeWidth = 2.dp.toPx()
            val cornerRadiusPx = cornerRadius.toPx()
            drawRoundRect(
                color = borderColor,
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(12f, 8f),
                        phase = 0f
                    )
                ),
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
        },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AddGarmentContentPreview() {
    ClosifyTheme {
        AddGarmentContent(
            selectedMode = CameraMode.GALLERY,
            onEvent = {}
        )
    }
}

package com.closify.myapplication.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.closify.myapplication.MainActivity
import com.closify.myapplication.R
import com.closify.myapplication.data.repository.OutfitRepository
import com.closify.myapplication.data.repository.UserRepository
import java.time.LocalDate

class PlannerWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = OutfitRepository.instance
        val userId = UserRepository.instance.getCurrentUserOrDefault().id
        val today = LocalDate.now()
        val todayStr = today.toSpanishTitle()
        
        // Buscamos el outfit planeado para hoy
        val plannedPost = repo.getPlannedPostByDate(userId, todayStr)
        
        // MOCKUP: Si no hay nada planeado, mostramos datos de prueba para ver el diseño
        val showMockup = plannedPost == null
        val title = plannedPost?.title ?: "Mi Look de Prueba"
        val garments = plannedPost?.outfit?.garments ?: listOf(
            com.closify.myapplication.domain.model.Garment(
                id = "mock_1",
                ownerUserId = "1",
                name = "Blusa",
                category = com.closify.myapplication.domain.model.GarmentCategory.TOP,
                imageUrl = "android.resource://com.closify.myapplication/drawable/blusa_1",
                suitableWeather = emptySet(),
                suitableOccasions = emptySet()
            ),
            com.closify.myapplication.domain.model.Garment(
                id = "mock_2",
                ownerUserId = "1",
                name = "Jean",
                category = com.closify.myapplication.domain.model.GarmentCategory.BOTTOM,
                imageUrl = "android.resource://com.closify.myapplication/drawable/jean_1",
                suitableWeather = emptySet(),
                suitableOccasions = emptySet()
            )
        )

        provideContent {
            GlanceTheme {
                PlannerWidgetContent(
                    context = context,
                    date = if (showMockup) "$todayStr (Vista Previa)" else todayStr,
                    title = title,
                    garments = garments
                )
            }
        }
    }

    @Composable
    private fun PlannerWidgetContent(
        context: Context,
        date: String,
        title: String?,
        garments: List<com.closify.myapplication.domain.model.Garment>
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp)
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java))), // Abre la app al tocar
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.Top
        ) {
            // Header con Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_closify_logo),
                    contentDescription = null,
                    modifier = GlanceModifier.size(24.dp)
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = "Closify",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GlanceTheme.colors.onSurface
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = "Hoy",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.primary
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Fecha
            Text(
                text = date,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Calendario (Mini fila de días)
            CalendarRow()

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Contenido del Outfit
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .background(GlanceTheme.colors.secondaryContainer)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (garments.isNotEmpty()) {
                    Text(
                        text = title ?: "Outfit para hoy",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = GlanceTheme.colors.onSecondaryContainer
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.height(6.dp))
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxWidth()
                    ) {
                        garments.take(3).forEach { garment ->
                            val resName = garment.imageUrl.substringAfterLast("/")
                            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
                            if (resId != 0) {
                                Image(
                                    provider = ImageProvider(resId),
                                    contentDescription = null,
                                    modifier = GlanceModifier.size(40.dp).padding(2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CalendarRow() {
        val today = LocalDate.now()
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            (-2..2).forEach { offset ->
                val date = today.plusDays(offset.toLong())
                val isToday = offset == 0
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier
                        .padding(horizontal = 6.dp)
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = TextStyle(
                            fontSize = 12.sp, 
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) GlanceTheme.colors.primary else GlanceTheme.colors.onSurface
                        ),
                        modifier = if (isToday) GlanceModifier.background(GlanceTheme.colors.primaryContainer) else GlanceModifier
                    )
                }
            }
        }
    }
}

class PlannerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PlannerWidget()
}

private fun LocalDate.toSpanishTitle(): String {
    val month = when (monthValue) {
        1 -> "enero"
        2 -> "febrero"
        3 -> "marzo"
        4 -> "abril"
        5 -> "mayo"
        6 -> "junio"
        7 -> "julio"
        8 -> "agosto"
        9 -> "septiembre"
        10 -> "octubre"
        11 -> "noviembre"
        else -> "diciembre"
    }

    return "$dayOfMonth de $month de $year"
}

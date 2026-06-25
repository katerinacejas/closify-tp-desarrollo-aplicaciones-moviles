package com.closify.myapplication.domain.usecase

import com.closify.myapplication.data.repository.GarmentRepository
import java.util.UUID
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.WeatherCondition

class GenerateOutfitsUseCase(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance
) {

    suspend operator fun invoke(
        weather: WeatherCondition,
        occasion: Occasion,
        userId: String
    ): List<Outfit> {
        val all = garmentRepository.getAllByUserId(userId)

        // Filtrá prendas compatibles con clima y ocasión
        // ANY actúa como "comodín" — es compatible con todo
        val filtered = all.filter { garment ->
            (weather == WeatherCondition.ANY || weather in garment.suitableWeather || WeatherCondition.ANY in garment.suitableWeather) &&
            (occasion == Occasion.ANY || occasion in garment.suitableOccasions || Occasion.ANY in garment.suitableOccasions)
        }

        val tops       = filtered.filter { it.category == GarmentCategory.TOP }
        val bottoms    = filtered.filter { it.category == GarmentCategory.BOTTOM }
        val footwear   = filtered.filter { it.category == GarmentCategory.FOOTWEAR }
        val outerwear  = filtered.filter { it.category == GarmentCategory.OUTERWEAR }
        val dresses    = filtered.filter { it.category == GarmentCategory.FULL_BODY }

        val outfits = mutableListOf<Outfit>()

        val needsOuterwear = weather == WeatherCondition.COLD || weather == WeatherCondition.WINDY
        val outwearOptions = if (needsOuterwear && outerwear.isNotEmpty()) outerwear else listOf(null)

        // Combinacion: TOP + BOTTOM + FOOTWEAR (+ OUTERWEAR si hace frio, iterando todos)
        for (top in tops) {
            for (bottom in bottoms) {
                for (shoe in footwear) {
                    for (outer in outwearOptions) {
                        val garments = mutableListOf(top, bottom, shoe)
                        outer?.let { garments.add(it) }
                        outfits.add(Outfit(id = UUID.randomUUID().toString(), garments = garments))
                    }
                }
            }
        }

        // Combinacion: FULL_BODY + FOOTWEAR (+ OUTERWEAR si hace frio, iterando todos)
        for (dress in dresses) {
            for (shoe in footwear) {
                for (outer in outwearOptions) {
                    val garments = mutableListOf(dress, shoe)
                    outer?.let { garments.add(it) }
                    outfits.add(Outfit(id = UUID.randomUUID().toString(), garments = garments))
                }
            }
        }

        return outfits.shuffled().take(5) // mezcla para evitar combinaciones repetitivas
    }
}

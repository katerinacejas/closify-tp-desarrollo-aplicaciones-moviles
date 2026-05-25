package com.closify.myapplication.domain.usecase

import com.closify.myapplication.data.repository.GarmentRepository
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.WeatherCondition

class GenerateOutfitsUseCase(
    private val garmentRepository: GarmentRepository = GarmentRepository.instance
) {

    operator fun invoke(
        weather: WeatherCondition,
        occasion: Occasion
    ): List<Outfit> {
        val all = garmentRepository.getAll()

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
        val dresses    = filtered.filter { it.category == GarmentCategory.DRESS }

        val outfits = mutableListOf<Outfit>()
        var idCounter = 1

        // Combinación: TOP + BOTTOM + FOOTWEAR (+ OUTERWEAR opcional si hace frío)
        for (top in tops) {
            for (bottom in bottoms) {
                for (shoe in footwear) {
                    val garments = mutableListOf(top, bottom, shoe)

                    // Agrega abrigo si el clima es frío o ventoso
                    if (weather == WeatherCondition.COLD || weather == WeatherCondition.WINDY) {
                        outerwear.firstOrNull()?.let { garments.add(it) }
                    }

                    outfits.add(Outfit(id = "outfit_${idCounter++}", garments = garments))
                }
            }
        }

        // Combinación: DRESS + FOOTWEAR (+ OUTERWEAR opcional)
        for (dress in dresses) {
            for (shoe in footwear) {
                val garments = mutableListOf(dress, shoe)

                if (weather == WeatherCondition.COLD || weather == WeatherCondition.WINDY) {
                    outerwear.firstOrNull()?.let { garments.add(it) }
                }

                outfits.add(Outfit(id = "outfit_${idCounter++}", garments = garments))
            }
        }

        return outfits.take(5) // máximo 5 outfits por generación
    }
}

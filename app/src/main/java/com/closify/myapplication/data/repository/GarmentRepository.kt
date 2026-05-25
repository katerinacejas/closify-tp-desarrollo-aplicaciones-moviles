package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.GarmentColor
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition

class GarmentRepository {

    companion object {
        val instance = GarmentRepository()

        // URIs de imágenes locales — Coil las carga igual que una URL remota
        // TODO: reemplazar por URLs de Firebase Storage
        private const val PKG = "com.closify.myapplication"
        private const val TOP       = "android.resource://$PKG/drawable/ic_garment_top"
        private const val BOTTOM    = "android.resource://$PKG/drawable/ic_garment_bottom"
        private const val FOOTWEAR  = "android.resource://$PKG/drawable/ic_garment_footwear"
        private const val OUTERWEAR = "android.resource://$PKG/drawable/ic_garment_outerwear"
        private const val DRESS     = "android.resource://$PKG/drawable/ic_garment_dress"
    }

    fun getAll(): List<Garment> = garments

    // ── Mock data ─────────────────────────────────────────────────────────────
    // TODO: reemplazar por Firebase Firestore

    private val garments = listOf(

        // ── Tops ──────────────────────────────────────────────────────────────
        Garment(
            id = "t1", name = "Remera blanca básica",
            category = GarmentCategory.TOP, color = GarmentColor.WHITE,
            imageUrl = TOP,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "t2", name = "Camisa azul",
            category = GarmentCategory.TOP, color = GarmentColor.BLUE,
            imageUrl = TOP,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ACADEMIC, Occasion.ELEGANT, Occasion.ANY)
        ),
        Garment(
            id = "t3", name = "Buzo gris",
            category = GarmentCategory.TOP, color = GarmentColor.GRAY,
            imageUrl = TOP,
            suitableWeather = setOf(WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ACADEMIC, Occasion.ANY)
        ),
        Garment(
            id = "t4", name = "Camisa negra",
            category = GarmentCategory.TOP, color = GarmentColor.BLACK,
            imageUrl = TOP,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.ELEGANT, Occasion.PARTY, Occasion.WORK, Occasion.ANY)
        ),
        Garment(
            id = "t5", name = "Remera rosa",
            category = GarmentCategory.TOP, color = GarmentColor.PINK,
            imageUrl = TOP,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.PARTY, Occasion.ANY)
        ),

        // ── Bottoms ───────────────────────────────────────────────────────────
        Garment(
            id = "b1", name = "Jean azul",
            category = GarmentCategory.BOTTOM, color = GarmentColor.BLUE,
            imageUrl = BOTTOM,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.ACADEMIC, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "b2", name = "Pantalón negro",
            category = GarmentCategory.BOTTOM, color = GarmentColor.BLACK,
            imageUrl = BOTTOM,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ELEGANT, Occasion.PARTY, Occasion.ANY)
        ),
        Garment(
            id = "b3", name = "Short beige",
            category = GarmentCategory.BOTTOM, color = GarmentColor.BEIGE,
            imageUrl = BOTTOM,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "b4", name = "Falda floral",
            category = GarmentCategory.BOTTOM, color = GarmentColor.PRINT,
            imageUrl = BOTTOM,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.PARTY, Occasion.ELEGANT, Occasion.ANY)
        ),
        Garment(
            id = "b5", name = "Pantalón beige",
            category = GarmentCategory.BOTTOM, color = GarmentColor.BEIGE,
            imageUrl = BOTTOM,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ACADEMIC, Occasion.CASUAL, Occasion.ANY)
        ),

        // ── Footwear ──────────────────────────────────────────────────────────
        Garment(
            id = "f1", name = "Zapatillas blancas",
            category = GarmentCategory.FOOTWEAR, color = GarmentColor.WHITE,
            imageUrl = FOOTWEAR,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ACADEMIC, Occasion.ANY)
        ),
        Garment(
            id = "f2", name = "Botas negras",
            category = GarmentCategory.FOOTWEAR, color = GarmentColor.BLACK,
            imageUrl = FOOTWEAR,
            suitableWeather = setOf(WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ELEGANT, Occasion.CASUAL, Occasion.ANY)
        ),
        Garment(
            id = "f3", name = "Sandalias beige",
            category = GarmentCategory.FOOTWEAR, color = GarmentColor.BEIGE,
            imageUrl = FOOTWEAR,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.PARTY, Occasion.ELEGANT, Occasion.ANY)
        ),
        Garment(
            id = "f4", name = "Zapatillas negras",
            category = GarmentCategory.FOOTWEAR, color = GarmentColor.BLACK,
            imageUrl = FOOTWEAR,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.ACADEMIC, Occasion.WORK, Occasion.CHILL, Occasion.ANY)
        ),

        // ── Outerwear ─────────────────────────────────────────────────────────
        Garment(
            id = "o1", name = "Campera jean",
            category = GarmentCategory.OUTERWEAR, color = GarmentColor.BLUE,
            imageUrl = OUTERWEAR,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.WINDY, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.ACADEMIC, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "o2", name = "Abrigo negro",
            category = GarmentCategory.OUTERWEAR, color = GarmentColor.BLACK,
            imageUrl = OUTERWEAR,
            suitableWeather = setOf(WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ELEGANT, Occasion.ANY)
        ),

        // ── Dresses ───────────────────────────────────────────────────────────
        Garment(
            id = "d1", name = "Vestido negro",
            category = GarmentCategory.DRESS, color = GarmentColor.BLACK,
            imageUrl = DRESS,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.HOT, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.ELEGANT, Occasion.PARTY, Occasion.ANY)
        ),
        Garment(
            id = "d2", name = "Vestido floral",
            category = GarmentCategory.DRESS, color = GarmentColor.PRINT,
            imageUrl = DRESS,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.PARTY, Occasion.ANY)
        )
    )
}

package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.GarmentCategory
import com.closify.myapplication.domain.model.GarmentColor
import com.closify.myapplication.domain.model.Occasion
import com.closify.myapplication.domain.model.WeatherCondition

class GarmentRepository {

    companion object {
        private const val PKG = "com.closify.myapplication"
        private fun res(name: String) = "android.resource://$PKG/drawable/$name"

        // ── Tops ──────────────────────────────────────────────────────────────
        private val BLUSA_1          = res("blusa_1")
        private val BLUSA_ELEGANTE_1 = res("blusa_elegante_1")
        private val BLUSA_ELEGANTE_2 = res("blusa_elegante_2")
        private val CAMISA_AZUL      = res("camisa_azul")
        private val BUZO_GRIS        = res("buzo_gris")

        // ── Bottoms ───────────────────────────────────────────────────────────
        private val JEAN_1            = res("jean_1")
        private val PANTALON_ELEGANTE = res("pantalon_elegante")
        private val PANTALON_BEIGE    = res("pantalon_beige")
        private val SHORT_BEIGE       = res("short_beige")
        private val FALDA_ELEGANTE    = res("falda_elegante")

        // ── Footwear ──────────────────────────────────────────────────────────
        private val ZAPATOS_ELEG_1   = res("zapatos_elegantes_1")
        private val BOTAS_NEGRAS     = res("botas_negras")
        private val ZAPATILLAS_BLANCAS = res("zapatillas_blancas")
        private val ZAPATILLAS_NEGRAS  = res("zapatillas_negras")

        // ── Outwear ───────────────────────────────────────────────────────────
        private val CAMPERA_JEAN = res("campera_jean")
        private val ABRIGO_NEGRO = res("abrigo_negro")

        // ── Full body ─────────────────────────────────────────────────────────
        private val VESTIDO_NEGRO   = res("vestido_negro")
        private val VESTIDO_FLORAL  = res("vestido_floral")

        // ← instance al final, cuando todas las constantes ya están inicializadas
        val instance = GarmentRepository()
    }

    fun getAll(): List<Garment> = garments

    // ── Mock data ─────────────────────────────────────────────────────────────
    // TODO: reemplazar por Firebase Firestore

    private val garments = listOf(

        // ── Tops ──────────────────────────────────────────────────────────────
        Garment(
            id = "t1",
            name = "Remera blanca básica",
            category = GarmentCategory.TOP,
            color = GarmentColor.WHITE,
            imageUrl = BLUSA_1,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "t2",
            name = "Camisa azul",
            category = GarmentCategory.TOP,
            color = GarmentColor.BLUE,
            imageUrl = CAMISA_AZUL,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ACADEMIC, Occasion.ELEGANT, Occasion.ANY)
        ),
        Garment(
            id = "t3",
            name = "Buzo gris",
            category = GarmentCategory.TOP,
            color = GarmentColor.GRAY,
            imageUrl = BUZO_GRIS,
            suitableWeather = setOf(WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ACADEMIC, Occasion.ANY)
        ),
        Garment(
            id = "t4",
            name = "Camisa negra",
            category = GarmentCategory.TOP,
            color = GarmentColor.BLACK,
            imageUrl = BLUSA_ELEGANTE_1,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.ELEGANT, Occasion.PARTY, Occasion.WORK, Occasion.ANY)
        ),
        Garment(
            id = "t5",
            name = "Remera rosa",
            category = GarmentCategory.TOP,
            color = GarmentColor.PINK,
            imageUrl = BLUSA_ELEGANTE_2,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.PARTY, Occasion.ANY)
        ),

        // ── Bottoms ───────────────────────────────────────────────────────────
        Garment(
            id = "b1",
            name = "Jean azul",
            category = GarmentCategory.BOTTOM,
            color = GarmentColor.BLUE,
            imageUrl = JEAN_1,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.ACADEMIC, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "b2",
            name = "Pantalón negro",
            category = GarmentCategory.BOTTOM,
            color = GarmentColor.BLACK,
            imageUrl = PANTALON_ELEGANTE,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ELEGANT, Occasion.PARTY, Occasion.ANY)
        ),
        Garment(
            id = "b3",
            name = "Short beige",
            category = GarmentCategory.BOTTOM,
            color = GarmentColor.BEIGE,
            imageUrl = SHORT_BEIGE,
            suitableWeather = setOf(WeatherCondition.HOT),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "b4",
            name = "Falda floral",
            category = GarmentCategory.BOTTOM,
            color = GarmentColor.PRINT,
            imageUrl = FALDA_ELEGANTE,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.PARTY, Occasion.ELEGANT, Occasion.ANY)
        ),
        Garment(
            id = "b5",
            name = "Pantalón beige",
            category = GarmentCategory.BOTTOM,
            color = GarmentColor.BEIGE,
            imageUrl = PANTALON_BEIGE,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ACADEMIC, Occasion.CASUAL, Occasion.ANY)
        ),

        // ── Footwear ──────────────────────────────────────────────────────────
        Garment(
            id = "f1",
            name = "Zapatillas blancas",
            category = GarmentCategory.FOOTWEAR,
            color = GarmentColor.WHITE,
            imageUrl = ZAPATILLAS_BLANCAS,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.CHILL, Occasion.ACADEMIC, Occasion.ANY)
        ),
        Garment(
            id = "f2",
            name = "Botas negras",
            category = GarmentCategory.FOOTWEAR,
            color = GarmentColor.BLACK,
            imageUrl = BOTAS_NEGRAS,
            suitableWeather = setOf(WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.MILD),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ELEGANT, Occasion.CASUAL, Occasion.ANY)
        ),
        Garment(
            id = "f3",
            name = "Sandalias beige",
            category = GarmentCategory.FOOTWEAR,
            color = GarmentColor.BEIGE,
            imageUrl = ZAPATOS_ELEG_1,
            suitableWeather = setOf(WeatherCondition.HOT),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.PARTY, Occasion.ELEGANT, Occasion.ANY)
        ),
        Garment(
            id = "f4",
            name = "Zapatillas negras",
            category = GarmentCategory.FOOTWEAR,
            color = GarmentColor.BLACK,
            imageUrl = ZAPATILLAS_NEGRAS,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.COLD),
            suitableOccasions = setOf(Occasion.ACADEMIC, Occasion.WORK, Occasion.CHILL, Occasion.ANY)
        ),

        // ── Outerwear ─────────────────────────────────────────────────────────
        Garment(
            id = "o1",
            name = "Campera jean",
            category = GarmentCategory.OUTERWEAR,
            color = GarmentColor.BLUE,
            imageUrl = CAMPERA_JEAN,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.WINDY, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.ACADEMIC, Occasion.CHILL, Occasion.ANY)
        ),
        Garment(
            id = "o2",
            name = "Abrigo negro",
            category = GarmentCategory.OUTERWEAR,
            color = GarmentColor.BLACK,
            imageUrl = ABRIGO_NEGRO,
            suitableWeather = setOf(WeatherCondition.COLD, WeatherCondition.WINDY, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.WORK, Occasion.ELEGANT, Occasion.ANY)
        ),

        // ── Dresses ───────────────────────────────────────────────────────────
        Garment(
            id = "d1",
            name = "Vestido negro",
            category = GarmentCategory.FULL_BODY,
            color = GarmentColor.BLACK,
            imageUrl = VESTIDO_NEGRO,
            suitableWeather = setOf(WeatherCondition.MILD, WeatherCondition.HOT, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.ELEGANT, Occasion.PARTY, Occasion.ANY)
        ),
        Garment(
            id = "d2",
            name = "Vestido floral",
            category = GarmentCategory.FULL_BODY,
            color = GarmentColor.PRINT,
            imageUrl = VESTIDO_FLORAL,
            suitableWeather = setOf(WeatherCondition.HOT, WeatherCondition.MILD, WeatherCondition.ANY),
            suitableOccasions = setOf(Occasion.CASUAL, Occasion.PARTY, Occasion.ANY)
        )
    )
}

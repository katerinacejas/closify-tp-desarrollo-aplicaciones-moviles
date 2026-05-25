package com.closify.myapplication.domain.model

enum class GarmentCategory {
    TOP,        // Parte superior (remera, camisa, buzo)
    BOTTOM,     // Parte inferior (pantalón, falda, short)
    FOOTWEAR,   // Calzado
    OUTWEAR,    // Abrigo superior (campera, saco) — solo si clima es Frío
    FULL_BODY   // Vestido, enterito, bodysuit — reemplaza TOP + BOTTOM
}

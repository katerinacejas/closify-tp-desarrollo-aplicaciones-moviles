package com.uade.closify.register

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterData(
    var usuario: String = "",
    var email: String = "",
    var contrasena: String = "",
    var nombre: String = "",
    var fechaNacimiento: String = "",
    var biografia: String = ""
) : Parcelable

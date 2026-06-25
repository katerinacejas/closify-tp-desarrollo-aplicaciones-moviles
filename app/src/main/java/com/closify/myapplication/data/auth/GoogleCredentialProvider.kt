package com.closify.myapplication.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.GoogleAuthCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class GoogleCredentialProvider(context: Context) {

    private val context = context
    private val appContext = context.applicationContext
    private val credentialManager = CredentialManager.create(context)

    suspend fun getCredential(): Result<GoogleAuthCredential> = runCatching {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(appContext.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )
        parseGoogleCredential(result.credential)
    }.recoverCatching { error ->
        throw when (error) {
            is GetCredentialCancellationException -> Exception("Inicio con Google cancelado.")
            is GetCredentialException -> Exception("No se pudo iniciar sesi\u00F3n con Google.")
            is GoogleIdTokenParsingException -> Exception("No se pudo leer la cuenta de Google.")
            else -> error
        }
    }

    private fun parseGoogleCredential(credential: androidx.credentials.Credential): GoogleAuthCredential {
        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            error("La credencial recibida no corresponde a Google.")
        }

        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return GoogleAuthCredential(
            idToken = googleCredential.idToken,
            email = googleCredential.id,
            displayName = googleCredential.displayName,
            profileImageUrl = googleCredential.profilePictureUri?.toString()
        )
    }
}

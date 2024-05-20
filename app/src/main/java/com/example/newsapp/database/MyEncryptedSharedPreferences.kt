package com.example.newsapp.database

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object MyEncryptedSharedPreferences {
    private var sharedPreferences: EncryptedSharedPreferences? = null

    fun initialize(context: Context) {

        val masterKey =
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "encrypted_prefs_file",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }


    fun deleteEncryptedSharedPreferences(context: Context) {
        context.getSharedPreferences("encrypted_prefs_file", Context.MODE_PRIVATE).edit().clear()
            .apply()
    }

    fun getEncryptedSharedPreferences(): EncryptedSharedPreferences {
        return sharedPreferences
            ?: throw IllegalStateException("SharedPreferencesManager is not initialized")
    }
}

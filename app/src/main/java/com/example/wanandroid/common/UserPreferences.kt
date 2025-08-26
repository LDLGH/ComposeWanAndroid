package com.example.wanandroid.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wanandroid.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

val Context.userDataStore by preferencesDataStore("user_prefs")

object UserPreferences {
    private val KEY_USER = stringPreferencesKey("user")

    suspend fun saveUser(context: Context, user: User) {
        val json = Json.encodeToString(user)
        context.userDataStore.edit { prefs ->
            prefs[KEY_USER] = json
        }
    }

    fun getUser(context: Context): Flow<User?> {
        return context.userDataStore.data.map { prefs ->
            prefs[KEY_USER]?.let {
                try {
                    Json.decodeFromString<User>(it)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    suspend fun clearUser(context: Context) {
        context.userDataStore.edit { it.clear() }
    }
}

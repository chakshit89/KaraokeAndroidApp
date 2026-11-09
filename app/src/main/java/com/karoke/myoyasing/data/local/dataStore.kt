package com.karoke.myoyasing.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "auth_preferences"
)

object AppDataStore {

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val accessTokenKey =
        stringPreferencesKey("access_token")

    private val isLoginKey =
        booleanPreferencesKey("is_login")


    private val userID =
        stringPreferencesKey("userid")
    suspend fun saveAccessToken(token: String) {
        appContext.dataStore.edit { preferences ->
            preferences[accessTokenKey] = token
        }
    }

    suspend fun getAccessToken(): String? {
        return appContext.dataStore.data
            .map { preferences ->
                preferences[accessTokenKey]
            }
            .first()
    }

    suspend fun clearAccessToken() {
        appContext.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
        }
    }


    suspend fun saveUserid(userid: String) {
        appContext.dataStore.edit { preferences ->
            preferences[userID] = userid
        }
    }

    suspend fun getUserid(): String? {
        return appContext.dataStore.data
            .map { preferences ->
                preferences[userID]
            }
            .first()
    }

    suspend fun clearUserid() {
        appContext.dataStore.edit { preferences ->
            preferences.remove(userID)
        }
    }

    suspend fun saveIsLogin(isLoggedIn: Boolean) {
        appContext.dataStore.edit { preferences ->
            preferences[isLoginKey] = isLoggedIn
        }
    }

    suspend fun getIsLogin(): Boolean {
        return appContext.dataStore.data
            .map { preferences ->
                preferences[isLoginKey] ?: false
            }
            .first()
    }

    suspend fun clearIsLogin() {
        appContext.dataStore.edit { preferences ->
            preferences.remove(isLoginKey)
        }
    }

    suspend fun logout() {
        appContext.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(isLoginKey)
        }
    }
}
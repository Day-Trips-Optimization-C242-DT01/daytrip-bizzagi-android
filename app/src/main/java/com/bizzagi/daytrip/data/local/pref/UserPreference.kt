package com.bizzagi.daytrip.data.local.pref

import androidx.datastore.preferences.core.stringPreferencesKey
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bizzagi.daytrip.data.retrofit.response.auth.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_token")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(userData: UserData) {
        dataStore.edit { pref ->
            pref[UID_KEY] = userData.uid
            pref[EMAIL_KEY] = userData.email
            pref[NAME_KEY] = userData.name
            pref[TOKEN_KEY] = userData.token
            Log.d("UserPreference", "Session saved for UID: ${userData.uid}, Token: ${userData.token}")
        }
    }

    fun getSession(): Flow<UserData> {
        return dataStore.data.map { pref ->
            val userData = UserData(
                pref[UID_KEY] ?: "",
                pref[EMAIL_KEY] ?: "",
                pref[NAME_KEY] ?: "",
                pref[TOKEN_KEY] ?: "",
            )
            Log.d("UserPreference", "Retrieved session: $userData")
            userData
        }
    }


    suspend fun logout() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val UID_KEY = stringPreferencesKey("uid_key")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name_key")
        private val TOKEN_KEY = stringPreferencesKey("token_key")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
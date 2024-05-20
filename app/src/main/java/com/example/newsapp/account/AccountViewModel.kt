package com.example.newsapp.newslist

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.newsapp.database.AppDatabase
import com.example.newsapp.database.DatabaseNews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(
    context: Context
) : ViewModel() {

    private val _databaseNews = MutableLiveData<List<DatabaseNews>>()
    val databaseNews: LiveData<List<DatabaseNews>> get() = _databaseNews

    private val _loginEvent = MutableLiveData<Event<LoginResult>>()
    val loginEvent: LiveData<Event<LoginResult>> get() = _loginEvent

    private val _logoutEvent = MutableLiveData<Event<Unit>>()
    val logoutEvent: LiveData<Event<Unit>> get() = _logoutEvent

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _databaseNews.postValue(AppDatabase.getDatabase(context).newsDao().getAllNews())
        }
    }

    fun setupAccountUI(
        preferences: SharedPreferences,
        sharedPreferences: EncryptedSharedPreferences
    ) {
        when {
            preferences.getInt("isUserExist", 0) == 1 && preferences.getInt(
                "isUserLog",
                0
            ) == 0 -> {
                // No need to do anything here, UI should be set to login
            }

            preferences.getInt("isUserExist", 0) == 0 && preferences.getInt(
                "isUserLog",
                0
            ) == 0 -> {
                // No need to do anything here, UI should be set to registration
            }

            preferences.getInt("isUserExist", 0) == 1 && preferences.getInt(
                "isUserLog",
                0
            ) == 1 -> {
                // User is already logged in, post a successful login event
                _loginEvent.postValue(Event(LoginResult(success = true)))
            }
        }
    }

    fun handleEnterButtonClick(
        preferences: SharedPreferences,
        sharedPreferences: EncryptedSharedPreferences,
        login: String,
        password: String
    ) {
        if (preferences.getInt("isUserExist", 0) == 1 && preferences.getInt("isUserLog", 0) == 0) {
            // Handle login
            if (sharedPreferences.getString(login, "") == password) {
                preferences.edit().putInt("isUserLog", 1).apply()
                _loginEvent.postValue(Event(LoginResult(success = true)))
            } else {
                _loginEvent.postValue(Event(LoginResult(success = false)))
            }
        } else if (preferences.getInt("isUserExist", 0) == 0 && preferences.getInt(
                "isUserLog",
                0
            ) == 0
        ) {
            // Handle registration
            sharedPreferences.edit().putString(login, password).apply()
            preferences.edit().putInt("isUserExist", 1).putInt("isUserLog", 1)
                .putString("LOGIN", login).apply()
            _loginEvent.postValue(Event(LoginResult(success = true)))
        }
    }

    fun logout(preferences: SharedPreferences) {
        preferences.edit().putInt("isUserLog", 0).apply()
        _logoutEvent.postValue(Event(Unit))
    }

    fun removeFav(context: Context, title: String) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(context).newsDao().deleteNew(title)
            _databaseNews.postValue(AppDatabase.getDatabase(context).newsDao().getAllNews())
        }
    }
}

data class LoginResult(val success: Boolean)

class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}
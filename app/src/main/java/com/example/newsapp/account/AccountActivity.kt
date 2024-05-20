package com.example.newsapp.account

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.database.MyEncryptedSharedPreferences
import com.example.newsapp.network.NewsListViewModelFactory
import com.example.newsapp.newslist.AccountViewModel
import com.example.newsapp.newslist.NewsListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AccountActivity : AppCompatActivity() {

    private lateinit var newsFavoriteRecycler: RecyclerView
    private lateinit var enterButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var loginTextField: TextInputEditText
    private lateinit var passwordTextField: TextInputEditText
    private lateinit var loginTextLayout: TextInputLayout
    private lateinit var passwordTextLayout: TextInputLayout
    private lateinit var viewModel: AccountViewModel
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var loginTitle: TextView
    private lateinit var accountName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_and_favorite_activity)
        initializeViews()
        setupBottomNavigation()

        MyEncryptedSharedPreferences.initialize(this)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val encryptedSharedPreferences =
            MyEncryptedSharedPreferences.getEncryptedSharedPreferences()

        viewModel = ViewModelProvider(
            this,
            NewsListViewModelFactory(repository = null, this)
        )[AccountViewModel::class.java]

        viewModel.setupAccountUI(preferences, encryptedSharedPreferences)

        viewModel.loginEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { loginResult ->
                if (loginResult.success) {
                    setupAccountInfo(preferences)
                } else {
                    Toast.makeText(this, "Неверный пароль!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.logoutEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                resetUI(preferences)
            }
        }

        val adapter = FavoriteAdapter(this, viewModel)
        newsFavoriteRecycler.adapter = adapter
        newsFavoriteRecycler.layoutManager = LinearLayoutManager(this)

        viewModel.databaseNews.observe(this) { list ->
            adapter.submitList(list)
        }

        enterButton.setOnClickListener {
            viewModel.handleEnterButtonClick(
                preferences,
                encryptedSharedPreferences,
                loginTextField.text.toString(),
                passwordTextField.text.toString()
            )
        }

        logoutButton.setOnClickListener {
            viewModel.logout(preferences)
        }
    }

    private fun initializeViews() {
        newsFavoriteRecycler = findViewById(R.id.favorite_list)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        enterButton = findViewById(R.id.enter_button)
        logoutButton = findViewById(R.id.logout_button)
        loginTextField = findViewById(R.id.login_text)
        passwordTextField = findViewById(R.id.password_text)
        loginTitle = findViewById(R.id.title_login)
        accountName = findViewById(R.id.account_name)
        loginTextLayout = findViewById(R.id.login)
        passwordTextLayout = findViewById(R.id.password)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.account
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed -> {
//                    finish()
                    val intent = Intent(this, NewsListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.account -> true
                else -> false
            }
        }
    }

    private fun setupAccountInfo(preferences: SharedPreferences) {
        loginTitle.visibility = View.GONE
        enterButton.visibility = View.GONE
        loginTextLayout.visibility = View.GONE
        passwordTextLayout.visibility = View.GONE
        accountName.visibility = View.VISIBLE
        accountName.text = preferences.getString("LOGIN", "")
        newsFavoriteRecycler.visibility = View.VISIBLE
        logoutButton.visibility = View.VISIBLE
    }

    private fun resetUI(preferences: SharedPreferences) {
        loginTitle.visibility = View.VISIBLE
        enterButton.visibility = View.VISIBLE
        loginTextLayout.visibility = View.VISIBLE
        loginTextField.text?.clear()
        passwordTextField.text?.clear()
        passwordTextLayout.visibility = View.VISIBLE
        accountName.visibility = View.GONE
        newsFavoriteRecycler.visibility = View.GONE
        logoutButton.visibility = View.GONE
        viewModel.setupAccountUI(
            preferences,
            MyEncryptedSharedPreferences.getEncryptedSharedPreferences()
        )
    }
}

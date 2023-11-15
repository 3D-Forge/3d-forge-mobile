package com.example.a3dforge.activities

import OkHttpConfig
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.R
import com.example.a3dforge.factories.AuthRegisterViewModelFactory
import com.example.a3dforge.factories.CatalogSearchViewModelFactory
import com.example.a3dforge.models.AuthViewModel
import com.example.a3dforge.models.CatalogSearchViewModel
import com.example.a3dforge.models.RegisterViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthRegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    private lateinit var loginEmailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var authButton: Button
    private lateinit var newPasswordTextView: TextView
    private lateinit var loginEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordRegEditText: EditText
    private lateinit var passwordRegConfirmEditText: EditText
    private lateinit var regButton: Button
    private lateinit var confirmationCheckBox: CheckBox
    private lateinit var rememberCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_register)

        loginEmailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        authButton = findViewById(R.id.authButton)
        newPasswordTextView = findViewById(R.id.newPasswordTextView)
        loginEditText = findViewById(R.id.loginEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordRegEditText = findViewById(R.id.passwordRegEditText)
        passwordRegConfirmEditText = findViewById(R.id.passwordRegConfirmEditText)
        regButton = findViewById(R.id.regButton)
        confirmationCheckBox = findViewById(R.id.confirmationCheckBox)
        rememberCheckBox = findViewById(R.id.rememberCheckBox)

        val loginOptionButt = findViewById<Button>(R.id.loginOptionButt)
        val regOptionButt = findViewById<Button>(R.id.regOptionButt)
        val round_background = ContextCompat.getDrawable(this, R.drawable.round_btn)
        val deactivated_background = ContextCompat.getDrawable(this, R.drawable.deactivated_button)

        newPasswordTextView.paintFlags = newPasswordTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        regOptionButt.setOnClickListener {
            regOptionButt.background = round_background
            loginOptionButt.background = deactivated_background
            changeVisibilityForRegistration()
        }

        loginOptionButt.setOnClickListener {
            loginOptionButt.background = round_background
            regOptionButt.background = deactivated_background
            changeVisibilityForLogin()
        }

        val okHttpConfig = OkHttpConfig

        authViewModel = ViewModelProvider(this, AuthRegisterViewModelFactory(okHttpConfig)).get(AuthViewModel::class.java)

        authViewModel.authResult.observe(this, Observer { result ->
            val (isSuccessful, login) = result
            if (isSuccessful) {
                Toast.makeText(this, "Авторизація успішна!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("user_login", login)
                startActivity(intent)
            } else {
                Log.e("AuthRegisterActivity", "Authentication failed")
                Toast.makeText(this, "Неправильна пошта, логін чи пароль!", Toast.LENGTH_SHORT).show()
            }
        })

        authButton.setOnClickListener {
            val loginOrEmail = loginEmailEditText.text.toString()
            val password = passwordEditText.text.toString()

            authViewModel.authenticateUser(loginOrEmail, password)
        }

        val registerViewModel = ViewModelProvider(this, AuthRegisterViewModelFactory(okHttpConfig)).get(RegisterViewModel::class.java)

        registerViewModel.registrationResult.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Реєстрація успішна!", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("AuthRegisterActivity", "Registration failed")
                Toast.makeText(this, "Помилка реєстрації", Toast.LENGTH_SHORT).show()
            }
        })

        regButton.setOnClickListener {
            val login = loginEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordRegEditText.text.toString()
            val confirmPassword = passwordRegConfirmEditText.text.toString()
            if (password != confirmPassword) {
                Toast.makeText(this, "Неправильний повтор паролю!", Toast.LENGTH_SHORT).show()
            } else if (!confirmationCheckBox.isChecked){
                Toast.makeText(this, "Погодьтесь з умовами!", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch {
                    registerViewModel.registerUser(login, email, password, confirmPassword)
                }
            }
        }
    }

    fun changeVisibilityForRegistration() {
        loginEmailEditText.visibility = View.GONE
        passwordEditText.visibility = View.GONE
        authButton.visibility = View.GONE
        newPasswordTextView.visibility = View.GONE
        rememberCheckBox.visibility = View.GONE
        loginEditText.visibility = View.VISIBLE
        emailEditText.visibility = View.VISIBLE
        passwordRegEditText.visibility = View.VISIBLE
        passwordRegConfirmEditText.visibility = View.VISIBLE
        regButton.visibility = View.VISIBLE
        confirmationCheckBox.visibility = View.VISIBLE
    }

    fun changeVisibilityForLogin() {
        loginEmailEditText.visibility = View.VISIBLE
        passwordEditText.visibility = View.VISIBLE
        authButton.visibility = View.VISIBLE
        newPasswordTextView.visibility = View.VISIBLE
        rememberCheckBox.visibility = View.VISIBLE
        loginEditText.visibility = View.GONE
        emailEditText.visibility = View.GONE
        passwordRegEditText.visibility = View.GONE
        passwordRegConfirmEditText.visibility = View.GONE
        regButton.visibility = View.GONE
        confirmationCheckBox.visibility = View.GONE
    }
}

package com.cge.myapplication

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import com.cge.example.ui.login.LoggedInUserView
import com.cge.example.ui.login.LoginViewModel
import com.cge.example.ui.login.LoginViewModelFactory
import com.cge.myapplication.MainActivity
import com.cge.myapplication.R

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


//        val resolutions = resources.getStringArray(R.array.array_resolutions)
//        val username = findViewById<Spinner>(R.id.spinner_resolution)
//
//        if (username != null) {
//            val adapter = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_item, resolutions
//            )
//            username.adapter = adapter
//
//            username.onItemSelectedListener = object :
//                AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>,
//                    view: View, position: Int, id: Long
//                ) {
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>) {
//                    // write code to perform some action
//                }
//            }
//        }


        val username = findViewById<EditText>(R.id.username)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = true //loginState.isDataValid

            if (loginState.usernameError != null) {
//                username.error = getString(loginState.usernameError)
            }

        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success, username.text.toString())
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            if (loginResult.success != null) {
                val main = Intent(this, MainActivity::class.java)
                main.putExtra("DISPLAY_NAME", loginResult.success.displayName)
                startActivity(main)
                finish()
            } else {
                val splash = Intent(this, MainActivity::class.java)
                startActivity(splash)
                finish()
            }
        })

        login.setOnClickListener {
            loginViewModel.login(
                username.text.toString()
            )
        }


    }

    private fun updateUiWithUser(model: LoggedInUserView, x:String) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "Device '$x' is $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}


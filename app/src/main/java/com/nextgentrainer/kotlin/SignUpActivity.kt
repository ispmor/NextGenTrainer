package com.nextgentrainer.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nextgentrainer.FirebaseLoginActivity
import com.nextgentrainer.R
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_signup)

        auth = Firebase.auth

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            val login = findViewById<EditText>(R.id.editTextLogin).text.toString()
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            val confirmedPassword = findViewById<EditText>(R.id.editTextTextRetypePassword).text.toString()

            if (login.isNotEmpty() && validEmail(email) && validatePassword(password, confirmedPassword)) {
                createAccount(email, password, login)
            }
        }
    }

    private fun validatePassword(password: String, confirmedPassword: String): Boolean {
        if (password.isNotEmpty()) {
            val pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
            val matcher = pattern.matcher(password)
            val result = matcher.matches()
            return result && password == confirmedPassword
        }
        return false
    }

    private fun validEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    private fun createAccount(email: String, password: String, login: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success on $login")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "You have been registered. Confirm your mail and sign in.",
                        Toast.LENGTH_LONG
                    ).show()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, FirebaseLoginActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val TAG = "SignUpActivity"
    }
}

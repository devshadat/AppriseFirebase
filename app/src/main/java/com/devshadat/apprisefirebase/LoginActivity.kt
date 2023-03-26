package com.devshadat.apprisefirebase

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devshadat.apprisefirebase.databinding.ActivityLoginBinding
import com.devshadat.apprisefirebase.utlil.SessionManager
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginEmail: TextInputLayout
    private lateinit var loginPassword: TextInputLayout
    private lateinit var loginBtn: Button
    private lateinit var loginRegBtn: TextView
    var firebaseDatabase: FirebaseDatabase? = null
    private lateinit var loadingbar: ProgressBar
    var databaseReference: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var sessionManager: SessionManager
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        loginEmail = binding.loginEmail
        loginPassword = binding.loginPassword
        loginBtn = binding.loginBtn
        loginRegBtn = binding.loginRegBtn
        loadingbar = binding.loadingBar

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance();
        database = Firebase.database.reference
        sessionManager = SessionManager(this)


        val user = auth.currentUser
        if (user != null) {
            // User is signed in
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out")
            Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show()
        }

        loginRegBtn.setOnClickListener {
            val i = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(i)
        }

        loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = loginEmail.editText!!.text.toString()
        val pass = loginPassword.editText!!.text.toString()

        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        loadingbar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                loadingbar.visibility = View.INVISIBLE
                updateLoginStateInDatabase(email)
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                loadingbar.visibility = View.INVISIBLE
                Toast.makeText(this, "Please provide valid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLoginStateInDatabase(email: String) {

        val token = email.takeWhile{ it != '@' }
        sessionManager.saveAuthToken(token)
        database.child("users").child(token!!).child("status").setValue("true")
        database.child("users").child(token).child("timestamp").setValue(ServerValue.TIMESTAMP)

        database!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(
                    this@LoginActivity, "Fail to add data $error", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
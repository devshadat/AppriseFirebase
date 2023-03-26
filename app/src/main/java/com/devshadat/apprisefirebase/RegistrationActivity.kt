package com.devshadat.apprisefirebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devshadat.apprisefirebase.data.User
import com.devshadat.apprisefirebase.databinding.ActivityRegistrationBinding
import com.devshadat.apprisefirebase.utlil.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var regEmail: TextInputLayout
    private lateinit var regPassword: TextInputLayout
    private lateinit var regPhone: TextInputLayout
    private lateinit var regFullname: TextInputLayout
    private lateinit var regUsername: TextInputLayout
    private lateinit var regBtn: Button
    private lateinit var regSignin: TextView
    private lateinit var loadingbar: ProgressBar
    private lateinit var database: DatabaseReference
    private lateinit var sessionManager: SessionManager
    private var exist: Boolean = false
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    private lateinit var user: User
    private var name = ""
    private var email = ""
    private var password = ""
    private var username = ""
    private var phone = ""
    var userId: String = ""


    val fullname: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regPassword = binding.regPassword
        regUsername = binding.regUsername
        regPhone = binding.regPhoneNo
        regEmail = binding.regEmail
        regFullname = binding.regName
        regBtn = binding.regBtn
        regSignin = binding.regLoginBtn
        loadingbar = binding.loadingBar
        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance();

        database = Firebase.database.reference
        userId = database.push().key!!
        binding.regBtn.setOnClickListener {

            signUpUser()
        }

        binding.regLoginBtn.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser() {
        val fullname = regFullname.editText!!.text.toString()
        val email = regEmail.editText!!.text.toString()
        val username = regUsername.editText!!.text.toString()
        val phone = regPhone.editText!!.text.toString()
        val pass = regPassword.editText!!.text.toString()
        val ref = FirebaseDatabase.getInstance().reference
        val map: MutableMap<String, String> = HashMap()
        map["timestamp"] = ServerValue.TIMESTAMP.toString()

        // check pass
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        checkEmailExistsOrNot(email)
        if (exist) {
            Toast.makeText(applicationContext, "Email already registered", Toast.LENGTH_SHORT)
            return
        } else {
            loadingbar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    userId = email.takeWhile{ it != '@' }
                    user = User(userId, fullname, username, email, phone, pass, ServerValue.TIMESTAMP, "false")
                    loadingbar.visibility = View.INVISIBLE
                    addDatatoFirebase(user, userId)
                    Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Sign Up Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun addDatatoFirebase(user: User, userId: String) {

        database.child("users").child(userId).setValue(user)
        sessionManager.saveAuthToken(userId)

        database!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Toast.makeText(this@RegistrationActivity, "Data added", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(
                    this@RegistrationActivity, "Fail to add data $error", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun checkEmailExistsOrNot(email: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(OnCompleteListener<SignInMethodQueryResult?> { task ->
                if (task.result.signInMethods?.size === 0) {
                    exist = false
                } else {
                    exist = true
                }
            }).addOnFailureListener(OnFailureListener { e -> e.printStackTrace() })
    }
}
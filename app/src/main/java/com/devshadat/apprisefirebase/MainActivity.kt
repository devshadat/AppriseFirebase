package com.devshadat.apprisefirebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devshadat.apprisefirebase.databinding.ActivityMainBinding
import com.devshadat.apprisefirebase.utlil.SessionManager
import com.devshadat.apprisefirebase.viewmodel.UserViewModel
import com.example.firebaservrealdbk.Adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var logoutBtn: Button
    private lateinit var auth: FirebaseAuth

    private lateinit var viewModel: UserViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var sessionManager: SessionManager
    lateinit var adapter: UserAdapter
    private lateinit var loadingbar: ProgressBar
    private lateinit var database: DatabaseReference
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        sessionManager = SessionManager(this)

        val user: FirebaseUser = auth.currentUser!!

        logoutBtn = binding.btnLogout
        loadingbar = binding.loadingBar

        logoutBtn.setOnClickListener {
            val token = sessionManager.fetchAuthToken()
            database.child("users").child(token!!).child("status").setValue("false")
            auth.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        userRecyclerView = binding.recyclerview
        layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        userRecyclerView.layoutManager = layoutManager
        userRecyclerView.setHasFixedSize(true)
        adapter = UserAdapter()
        userRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        viewModel.allUsers.observe(this, Observer {
            loadingbar.visibility = View.INVISIBLE
            adapter.updateUserList(it)

        })
    }
}
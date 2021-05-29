package com.example.btcanhan2_todolistapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private val TAG = "Main"
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var userPhoto: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userPhoto = findViewById(R.id.userAvatar)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        val userImageURL= mUser.photoUrl.toString()
        Glide.with(this).load(userImageURL).into(userPhoto)


        val bottonNavActionView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        bottonNavActionView.setupWithNavController(navController)

        toolBar = findViewById(R.id.toolBar)
        setSupportActionBar(toolBar)

    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)
        return true
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //logout
        if (item.itemId == R.id.logout) {
            AppUtil.isLoggedOut = true
            startActivity(Intent(this,Login::class.java))
            //Log out here
            Toast.makeText(this, "Log out success", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
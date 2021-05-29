package com.example.btcanhan2_todolistapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoadScreen : AppCompatActivity() {
    private val TAG ="LoadScreen"
    private val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_screen)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        loadData()
    }
    private fun loadData(){
        if(AppUtil.checkInternet(this)){
            getDataFromDBtoApp()
        }
        else{
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getDataFromDBtoApp() {
        val dbReference = db.collection(mUser.uid)
        dbReference.get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val dueDate = document["dueDate"].toString()
                    val taskTitle = document["taskTitle"].toString()
                    val isDoneDB= document["isDone"].toString()
                    var isDone =false
                    if(isDoneDB =="1"){
                        isDone =true
                    }
                    val task: Task = Task(taskTitle, isDone,dueDate)
                    AppUtil.listtask.add(task)
                }
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }
}
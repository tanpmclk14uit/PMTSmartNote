package com.example.btcanhan2_todolistapplication

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecycleViewAdapter(var arrData: ArrayList<Task>) :
    RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvActivityName: TextView = view.findViewById(R.id.tvActivityName)
        val cbIsDone: CheckBox = view.findViewById(R.id.cbIsDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    private fun getItem(position: Int): Any {
        return arrData[position]
    }

    public fun addTodo(temp: Task) {
        arrData.add(temp)
        notifyItemInserted(arrData.size - 1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var task: Task = arrData[position]
        holder.itemView.apply {
            holder.apply {
                tvActivityName.text = task.taskTitle
                cbIsDone.isChecked = task.taskDone
                activityDone(position, holder)
                cbIsDone.setOnCheckedChangeListener { _, isChecked ->
                    run {
                        activityDone(position, holder)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrData.size
    }

    private fun activityDone(position: Int, viewHolder: ViewHolder) {
        var task: Task = getItem(position) as Task
        var status: Int
        if (viewHolder.cbIsDone.isChecked === true) {
            task.taskDone = true
            viewHolder.tvActivityName.paintFlags =
                viewHolder.tvActivityName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            status=1
        } else {
            task.taskDone = false
            viewHolder.tvActivityName.paintFlags =
                viewHolder.tvActivityName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            status=0
        }
        chageTaskStatusOnDB(task,status)
    }

    private fun chageTaskStatusOnDB(task: Task, status: Int) {
        val db = Firebase.firestore
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val mUser: FirebaseUser = mAuth.currentUser!!
        val userReference = db.collection(mUser.uid)
        userReference.whereEqualTo("taskTitle", task.taskTitle)
            .whereEqualTo("dueDate", task.dueDate).get().addOnSuccessListener { documents ->
                for (doc in documents){
                    userReference.document(doc.id).update("isDone",status)
                }
            }.addOnFailureListener { e ->
                Log.d("ChageStatus", e.toString())
            }
    }
}
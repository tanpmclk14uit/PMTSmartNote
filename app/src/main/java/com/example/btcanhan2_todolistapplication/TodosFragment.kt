package com.example.btcanhan2_todolistapplication

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TodosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TodosFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private val TAG = "TodosFragment"
    lateinit var todoListView: RecyclerView
    lateinit var todoListAdapter: RecycleViewAdapter
    private lateinit var dialog: Dialog
    private lateinit var save: Button
    private lateinit var cancel: Button
    private lateinit var dueDate: EditText
    private lateinit var taskTitle: EditText
    private lateinit var mMainActivity: MainActivity
    private var db = Firebase.firestore


    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_todos, container, false)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!


        dialog = Dialog(this.requireContext())
        setUpDialog()

        val addTask: FloatingActionButton = view.findViewById(R.id.addTask)
        addTask.setOnClickListener {
            val currentDate: String =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            dueDate.setText(currentDate)
            dialog.show()
        }
        mMainActivity = activity as MainActivity
        todoListAdapter = RecycleViewAdapter( AppUtil.listtask)
        todoListView = view.findViewById(R.id.todoList)
        todoListView.adapter = todoListAdapter
        todoListView.layoutManager = LinearLayoutManager(this.context)
        return view
    }



    private fun addDAtaToDB() {
        val dbReference = db.collection(mUser.uid)
        val taskTitle = taskTitle.text.toString().trim()
        val dueDate = dueDate.text.toString()
        val task = hashMapOf(
            "taskTitle" to taskTitle,
            "dueDate" to dueDate,
            "isDone" to 0
        )
        dbReference.document()
            .set(task)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    private fun setUpDialog() {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_task)
        if (dialog.window == null) {
            return
        }
        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttr: WindowManager.LayoutParams = window.attributes
        windowAttr.gravity = Gravity.CENTER
        window.attributes = windowAttr

        dialog.setCancelable(false)
        save = dialog.findViewById(R.id.btnSave)
        cancel = dialog.findViewById(R.id.btnCancel)
        dueDate = dialog.findViewById(R.id.dueDate)
        taskTitle = dialog.findViewById(R.id.taskTitle)


        cancel.setOnClickListener {
            cancelDialogClick()
        }

        dueDate.setOnClickListener {
            pickDate()
        }
        save.setOnClickListener {
            onSaveClick()
        }

    }

    private fun onSaveClick() {
        if (taskTitle.text.toString().trim() != "") {
            addDAtaToDB()
            val tasktitle = taskTitle.text.toString().trim()
            val dueDate = dueDate.text.toString()
            todoListAdapter.addTodo(Task(tasktitle,false,dueDate))
            taskTitle.text.clear()
            dialog.dismiss()
        } else {
            Toast.makeText(this.context, "Title can not empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickDate() {
        val c: Calendar = Calendar.getInstance()
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        val month: Int = c.get(Calendar.MONTH)
        val year: Int = c.get(Calendar.YEAR)

        val dpd: DatePickerDialog = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { _: DatePicker, mYear: Int, mMonth: Int, mDay: Int ->

                dueDate.setText("$mDay/${mMonth + 1}/$mYear")
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun cancelDialogClick() {
        dueDate.text.clear()
        taskTitle.text.clear()
        dialog.dismiss()
    }
}
package com.example.prisonapp

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prisonapp.database.Todo
import com.example.prisonapp.database.TodoDatabase
import com.example.prisonapp.database.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TodoAdapter
    private lateinit var viewModel: MainActivityData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = TodoRepository(TodoDatabase.getInstance(this))
        val recyclerView: RecyclerView = findViewById(R.id.rvTodoList)
        viewModel = ViewModelProvider(this)[MainActivityData::class.java]
        viewModel.data.observe(this) { todos ->
            adapter = TodoAdapter(todos, repository, viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.getAllTodoItems()
            runOnUiThread {
                viewModel.setData(data)
            }
        }

        val btnAddItem: Button = findViewById(R.id.btnAddTodo)

        btnAddItem.setOnClickListener {
            displayDialog(repository)
        }
    }

    private fun displayDialog(repository: TodoRepository) {
        val builder = android.app.AlertDialog.Builder(this)

        // Set the alert dialog title and message
        builder.setTitle("Enter New criminal name:")
        builder.setMessage("Enter the id below:")

        // Create an EditText input field
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set the positive button action
        builder.setPositiveButton("OK") { dialog, which ->
            // Get the input text and display a Toast message
            val item = input.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(Todo(item))
                val data = repository.getAllTodoItems()
                runOnUiThread {
                    viewModel.setData(data)
                }
            }
        }
        // Set the negative button action
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        // Create and show the alert dialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        // Override onBackPressed to prevent the app from closing on back press
        // Instead, navigate up or handle the back button action as required
        // Here we're just calling super.onBackPressed() to handle normal back button behavior
        super.onBackPressed()
    }
}

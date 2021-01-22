package com.example.todolist

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), UpdateAndDelete {
    lateinit var database: DatabaseReference
    var toDOList : MutableList<ToDoTarefaModelo>? = null
    lateinit var adapter: ToDoAdapter
    private var listViewItem : ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab=findViewById<View>(R.id.fab) as FloatingActionButton
        listViewItem = findViewById<ListView>(R.id.item_listView)

        database=FirebaseDatabase.getInstance().reference

        fab.setOnClickListener { view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setTitle("Escreva a tarefa")
            alertDialog.setView(textEditText)
//            textEditText.isFocusableInTouchMode = true
//            textEditText.requestFocus()

            alertDialog.setPositiveButton("Adicionar"){dialog, i ->
                val toDoTarefa = ToDoTarefaModelo.createList()
                toDoTarefa.texto = textEditText.text.toString()
                toDoTarefa.feito = false

                val newItemData = database.child("todo").push()
                toDoTarefa.UID = newItemData.key

                newItemData.setValue((toDoTarefa))

                dialog.dismiss()
                Toast.makeText(this, "tarefa salva", Toast.LENGTH_LONG).show()
            }
            alertDialog.show()
        }

        toDOList = mutableListOf<ToDoTarefaModelo>()
        adapter = ToDoAdapter(this, toDOList!!)
        listViewItem!!.adapter = adapter
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Nenhum item adicionado", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                toDOList!!.clear()
                addItemToList(snapshot)

            }
        })
    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items = snapshot.children.iterator()

        if(items.hasNext()){
            val toDoIndexedValue = items.next()
            val itemsIterator = toDoIndexedValue.children.iterator()

            while (itemsIterator.hasNext()){
                val currentItem = itemsIterator.next()
                val toDoItemData = ToDoTarefaModelo.createList()
                val map = currentItem.getValue() as HashMap<String, Any>

                toDoItemData.UID = currentItem.key
                toDoItemData.feito = map.get("feito") as Boolean?
                toDoItemData.texto = map.get("texto") as String?
                toDOList!!.add(toDoItemData)
            }
        }

        adapter.notifyDataSetChanged()

    }

    override fun modifyItem(itemUID: String, estaFeito: Boolean) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.child("feito").setValue(estaFeito)
    }

    override fun onItemDelete(itemUID: String) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}
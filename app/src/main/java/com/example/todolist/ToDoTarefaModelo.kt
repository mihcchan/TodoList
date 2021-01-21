package com.example.todolist

class ToDoTarefaModelo {

    companion object Factory{
            fun createList(): ToDoTarefaModelo = ToDoTarefaModelo()
    }

    var UID: String? = null
    var texto: String? = null
    var feito: Boolean? = false


}
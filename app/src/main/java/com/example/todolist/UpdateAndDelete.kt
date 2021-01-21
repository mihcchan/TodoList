package com.example.todolist

interface UpdateAndDelete{

    fun modifyItem(itemUID: String, estaFeito: Boolean)
    fun onItemDelete(itemUID: String)
}
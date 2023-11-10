package com.example.tarea4moviles

data class Product(val id: Int, var name: String, var description: String) {
    companion object {
        private var counter = 1
    }

    constructor(name: String, description: String) : this(counter++, name, description)
}


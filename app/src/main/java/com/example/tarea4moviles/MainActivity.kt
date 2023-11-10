package com.example.tarea4moviles

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.SQLiteConnector
import java.sql.SQLException

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private var products_list: MutableList<Product> = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SQLiteConnector.initialize(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "SQLiteFarmacia"

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador antes de llamar a printProducts
        adapter = MyAdapter(products_list, this::deleteProducts, this::showDialogEdit)
        recyclerView.adapter = adapter

        printProducts() // Llama a printProducts después de inicializar el adaptador
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // Acción para el elemento de búsqueda
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val etNameProduct: EditText = dialogView.findViewById(R.id.etNameProduct)
        val etDescriptionProduct: EditText = dialogView.findViewById(R.id.etDescriptionProduct)

        val btnAgregar: Button = dialogView.findViewById(R.id.btnAddProduct)
        val btnCerrar: Button = dialogView.findViewById(R.id.btnCloseDialog)

        btnAgregar.setOnClickListener {
            val name = etNameProduct.text.toString()
            val description = etDescriptionProduct.text.toString()
            writeUsers(name, description)
            printProducts()
            dialog.dismiss()
        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showDialogEdit(product: Product) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val etEditNameProduct: EditText = dialogView.findViewById(R.id.etEditNameProduct)
        val etEditDescriptionProduct: EditText = dialogView.findViewById(R.id.etEditDescriptionProduct)

        etEditNameProduct.setText(product.name)
        etEditDescriptionProduct.setText(product.description)

        val btnEdit: Button = dialogView.findViewById(R.id.btnEditProduct)
        val btnCerrar: Button = dialogView.findViewById(R.id.btnEditCloseDialog)

        btnEdit.setOnClickListener {
            val newName = etEditNameProduct.text.toString()
            val newDescription = etEditDescriptionProduct.text.toString()
            product.name = newName
            product.description = newDescription
            updateProductInDatabase(product)
            printProducts()
            dialog.dismiss()
        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun writeUsers(name: String, description: String) {
        val db = SQLiteConnector.getWritableDatabase()
        val product = Product(name, description)
        insertProduct(db, product)
        db.close()
        SQLiteConnector.closeDatabase()
    }

    private fun insertProduct(db: SQLiteDatabase, product: Product) {
        val values = ContentValues()
        values.put("name", product.name)
        values.put("description", product.description)
        db.insert("Product", null, values)
    }

    @SuppressLint("Range")
    private fun readProducts(db: SQLiteDatabase): List<Product> {
        val products = mutableListOf<Product>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM Product", null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    products.add(Product(id, name, description))
                }
                cursor.close()
            }
        } catch (e: SQLException) {
            db.execSQL("CREATE TABLE Product (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT)")
            return emptyList()
        }

        return products
    }

    fun printProducts() {
        val context: Context = this
        val db = SQLiteConnector.getWritableDatabase()
        val products = readProducts(db)
        products_list.clear()
        for (product in products) {
            products_list.add(product)
            Log.d("Usercito", "ID: ${product.id}, Name: ${product.name}, DESCRIPTION: ${product.description}")
        }
        adapter.notifyDataSetChanged()
        db.close()
        SQLiteConnector.closeDatabase()
    }

    fun deleteProducts(id: Int) {
        val db = SQLiteConnector.getWritableDatabase()
        if (deleteProduct(db, id)) {
            Log.d("Delete", "Persona eliminada exitosamente.")
        } else {
            Log.d("Delete", "No se pudo eliminar la persona.")
        }
        printProducts()
        db.close()
        SQLiteConnector.closeDatabase()
    }

    private fun deleteProduct(db: SQLiteDatabase, productId: Int): Boolean {
        val whereClause = "id = ?"
        val whereArgs = arrayOf(productId.toString())
        val deletedRows = db.delete("Product", whereClause, whereArgs)
        return deletedRows > 0
    }

    private fun updateProductInDatabase(product: Product) {
        val db = SQLiteConnector.getWritableDatabase()
        updateProduct(db, product)
        db.close()
        SQLiteConnector.closeDatabase()
    }

    private fun updateProduct(db: SQLiteDatabase, product: Product) {
        val values = ContentValues()
        values.put("name", product.name)
        values.put("description", product.description)
        val whereClause = "id = ?"
        val whereArgs = arrayOf(product.id.toString())
        db.update("Product", values, whereClause, whereArgs)
    }
}

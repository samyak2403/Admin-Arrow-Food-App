package com.arrowwould.adminwaveoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.arrowwould.adminwaveoffood.adapter.MenuItemAdapter
import com.arrowwould.adminwaveoffood.databinding.ActivityAllitemBinding
import com.arrowwould.adminwaveoffood.model.AllMenu
import com.google.android.play.core.integrity.p
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllitemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenu> = ArrayList()

    private val binding: ActivityAllitemBinding by lazy {
        ActivityAllitemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        retrieveMenuItem()



        //back button
        binding.backButton.setOnClickListener {
            finish()
        }


    }

    private fun retrieveMenuItem() {

        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")

        //fetch data from data base
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                // clear existaing data before populating
                menuItems.clear()

                //loop for through each food item
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }

        })
    }
    private fun setAdapter() {

        val adapter = MenuItemAdapter(this@AllitemActivity,menuItems,databaseReference){position->
            deleteMenuItems(position)
        }
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter = adapter
    }

    private fun deleteMenuItems(position: Int) {
        val menuItemToDelete = menuItems[position]
        val menuItemKey =menuItemToDelete.key
        val foodMenuReference = database.reference.child("menu").child(menuItemKey!!)
        foodMenuReference.removeValue().addOnCompleteListener { task->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.MenuRecyclerView.adapter?.notifyItemRemoved(position)
            }else{
                Toast.makeText(this, "Item Not Deleted", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
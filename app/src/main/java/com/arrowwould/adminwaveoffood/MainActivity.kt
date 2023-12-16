package com.arrowwould.adminwaveoffood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arrowwould.adminwaveoffood.databinding.ActivityMainBinding
import com.arrowwould.adminwaveoffood.model.OrderDetails
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completedOrderReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //initialize Firebase Auth
        auth = Firebase.auth


        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        binding.allItemMenu.setOnClickListener {
            val intent = Intent(this, AllitemActivity::class.java)
            startActivity(intent)
        }

        binding.outForDeliveryButton.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }

        binding.profile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }

        binding.creatUser.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }

        binding.pendingOrderTextView.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }
        //logout Button
       binding.logoutButton.setOnClickListener {
           auth.signOut()
           startActivity(Intent(this,SignUpActivity::class.java))
               finish()

       }

        pendingOrders()

        completedOrders()

        wholeTimeErring()

    }

    private fun wholeTimeErring() {
        var listOfTotalPay = mutableListOf<Int>()
        completedOrderReference = FirebaseDatabase.getInstance().reference.child("CompletedOrder")
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    var completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.totalPrice?.replace("₹", "")?.toIntOrNull()
                        ?.let { i ->
                            listOfTotalPay.add(i)
                        }
                }
                binding.wholeTimeEarning.text = listOfTotalPay.sum().toString()+"₹"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun completedOrders() {
        val completedOrderReference = database.reference.child("CompletedOrder")
        var completeOrderItemCount = 0
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                completeOrderItemCount = snapshot.childrenCount.toInt()
                binding.completeOrders.text = completeOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun pendingOrders() {
        database = FirebaseDatabase.getInstance()
        val pendingOrderReference = database.reference.child("OrderDetails")
        var pendingOrderItemCount = 0
        pendingOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                pendingOrderItemCount = snapshot.childrenCount.toInt()
                binding.PendingOrders.text = pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
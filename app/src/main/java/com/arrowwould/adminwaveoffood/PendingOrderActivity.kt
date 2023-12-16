package com.arrowwould.adminwaveoffood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.arrowwould.adminwaveoffood.adapter.PendingOrederAdapter
import com.arrowwould.adminwaveoffood.databinding.ActivityPendingOrderBinding
import com.arrowwould.adminwaveoffood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(), PendingOrederAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOreder: MutableList<String> = mutableListOf()
    private var listOfOrederItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrederDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialization of databse
        database = FirebaseDatabase.getInstance()
        //Initialization of databaseReference
        databaseOrederDetails = database.reference.child("OrderDetails")

        getOrdersDetails()

        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun getOrdersDetails() {
        // retrieve order details from Firebase database
        databaseOrederDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapShot in snapshot.children) {
                    val orderDetails = orderSnapShot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrederItem.add(it)

                    }
                }
                addDataToListForRecyclerView()
            }


            override fun onCancelled(error: DatabaseError) {


            }

        })
    }

    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOrederItem) {

            // add data to respective list for populating the recyclerView
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach {
                listOfImageFirstFoodOreder.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrederAdapter(
            this,
            listOfName,
            listOfTotalPrice,
            listOfImageFirstFoodOreder,
            this
        )
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrederItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        // handle item acceptance and update databse
        val childItemPushKey = listOfOrederItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }

    override fun onItemDispatchClickListener(position: Int) {
        // handle item dispatch and update databse
        val dispatchItemPushKey = listOfOrederItem[position].itemPushKey
        val dispatchItemOrderReference =
            database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderReference.setValue(listOfOrederItem[position])
            .addOnSuccessListener {
                deleteThisItemFromOrderDetails(dispatchItemPushKey)
            }
    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemsReference =
            database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Order Is Dispatched", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Order Is Not Dispatched", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        // update order acceptance in user's BuyHistory and OrederDetails
        val userIdOfClickedItem = listOfOrederItem[position].userUid
        val pushKeyOfClickedItem = listOfOrederItem[position].itemPushKey
        val buyHistoryReference =
            database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory")
                .child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrederDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }
}
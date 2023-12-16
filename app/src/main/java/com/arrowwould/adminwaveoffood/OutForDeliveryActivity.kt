package com.arrowwould.adminwaveoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.arrowwould.adminwaveoffood.adapter.DeliveryAdapter
import com.arrowwould.adminwaveoffood.databinding.ActivityOutForDeliveryBinding
import com.arrowwould.adminwaveoffood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
        //retrieve amd display complete order
        retrieveCompleteOrderDetails()

    }

    private fun retrieveCompleteOrderDetails() {

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()
        val completeOrderReference = database.reference.child("CompletedOrder")
            .orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear the list before populating in with the new data
                listOfCompleteOrderList.clear()

                for(orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }
                // reverse the list to display list order first
                listOfCompleteOrderList.reverse()

                setDataIntoRecyclerView()


            }

            override fun onCancelled(error: DatabaseError) {


            }

        })
    }

    private fun setDataIntoRecyclerView() {
        // Initialization list to hold customers name and payment status
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()

        for (order in listOfCompleteOrderList){
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }

        val adapter = DeliveryAdapter(customerName, moneyStatus)
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)

    }


}
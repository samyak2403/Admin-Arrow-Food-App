package com.arrowwould.adminwaveoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.UserHandle
import android.util.Log
import android.widget.Toast
import com.arrowwould.adminwaveoffood.databinding.ActivityAdminProfileBinding
import com.arrowwould.adminwaveoffood.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("user")

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.saveInfoButton.setOnClickListener {
            updateUserData()
        }
        binding.name.isEnabled = false
        binding.address.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        binding.password.isEnabled = false
        binding.saveInfoButton.isEnabled = false

        var isEnable = false
        binding.editeButton.setOnClickListener {
            isEnable = !isEnable
            binding.name.isEnabled = isEnable
            binding.address.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.phone.isEnabled = isEnable
            binding.password.isEnabled = isEnable
            if (isEnable) {
                binding.name.requestFocus()
            }
            binding.saveInfoButton.isEnabled = isEnable
        }
        retrieveUserData()
    }

    private fun retrieveUserData() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        var ownerName = snapshot.child("name").getValue()
                        var email = snapshot.child("email").getValue()
                        var password = snapshot.child("password").getValue()
                        var phone = snapshot.child("phone").getValue()
                        var address = snapshot.child("address").getValue()

                        Log.d("TAG", "onDataChange: $ownerName")
                        setDataToTextView(ownerName, email, password, phone, address)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }


    }

    private fun setDataToTextView(
        ownerName: Any?,
        email: Any?,
        password: Any?,
        address: Any?,
        phone: Any?
    ) {
        binding.name.setText(ownerName.toString())
        binding.email.setText(email.toString())
        binding.password.setText(password.toString())
        binding.address.setText(address.toString())
        binding.phone.setText(phone.toString())

    }

    private fun updateUserData() {
        var updateName = binding.name.text.toString()
        var updateEmail = binding.email.text.toString()
        var updatePassword = binding.password.text.toString()
        var updatePhone = binding.phone.text.toString()
        var updateAddress = binding.address.text.toString()
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {


            val userReference = adminReference.child(currentUserUid)
            userReference.child("name").setValue(updateName)
            userReference.child("email").setValue(updateEmail)
            userReference.child("password").setValue(updatePassword)
            userReference.child("phone").setValue(updatePhone)
            userReference.child("address").setValue(updateAddress)


            Toast.makeText(this, "Profile Update Successfully", Toast.LENGTH_SHORT).show()
            // update the email and password for firebase Authentication
            auth.currentUser?.updateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)

        }else {
            Toast.makeText(this, "Profile Update failed", Toast.LENGTH_SHORT).show()
        }
    }
}
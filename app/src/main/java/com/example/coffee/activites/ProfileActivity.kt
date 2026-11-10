package com.example.coffee.activites

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.coffee.Helper.TinyDB
import com.example.coffee.R
import com.example.coffee.databinding.ActivityProfileBinding
import com.example.coffee.domain.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var tinyDB: TinyDB
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadLocalProfile()
        syncWithCloudServer()
    }

    private fun getUserCloudKey(email: String): String {
        return email.trim().lowercase().replace(".", "_").replace("@", "_at_")
    }

    private fun loadLocalProfile() {
        val isLoggedIn = tinyDB.getBoolean("is_logged_in")
        val savedEmail = tinyDB.getString("user_email")
        val savedName = tinyDB.getString("user_name")
        val savedPhone = tinyDB.getString("user_phone")
        val savedAddress = tinyDB.getString("user_address")
        val savedTier = tinyDB.getString("user_tier")
        val savedPoints = tinyDB.getInt("user_points")
        val savedOrders = tinyDB.getInt("user_orders_count")

        if (isLoggedIn && savedEmail.isNotEmpty()) {
            binding.userNameTxt.text = if (savedName.isNotEmpty()) savedName else savedEmail.substringBefore("@")
            binding.userEmailTxt.text = savedEmail
            binding.userPhoneTxt.text = if (savedPhone.isNotEmpty()) savedPhone else "+91 98765 43210"
            binding.userAddressTxt.text = if (savedAddress.isNotEmpty()) savedAddress else "Campus Hall, Block 3"
            binding.memberTierTxt.text = if (savedTier.isNotEmpty()) "⭐️ $savedTier" else "⭐️ Gold Tier"

            val points = if (savedPoints > 0) savedPoints else 240
            val orders = if (savedOrders > 0) savedOrders else 8

            binding.pointsTxt.text = "$points Pts"
            binding.ordersCountTxt.text = "$orders"
            binding.rewardsProgressBar.progress = points
            val remaining = (300 - points).coerceAtLeast(0)
            binding.rewardsHintTxt.text = "$remaining pts until your next free artisan beverage!"

            binding.cloudBadgeLayout.visibility = View.VISIBLE
            binding.rewardsCardLayout.visibility = View.VISIBLE
            binding.contactInfoCard.visibility = View.VISIBLE
            binding.editProfileHeaderBtn.visibility = View.VISIBLE
            binding.guestLoginPromptBtn.visibility = View.GONE
            binding.logoutBtn.visibility = View.VISIBLE
        } else {
            // Guest Mode
            binding.userNameTxt.text = "Guest User"
            binding.userEmailTxt.text = "Sign in to save profile on cloud server"
            binding.cloudBadgeLayout.visibility = View.GONE
            binding.rewardsCardLayout.visibility = View.GONE
            binding.contactInfoCard.visibility = View.GONE
            binding.editProfileHeaderBtn.visibility = View.GONE
            binding.guestLoginPromptBtn.visibility = View.VISIBLE
            binding.logoutBtn.visibility = View.GONE
        }
    }

    private fun syncWithCloudServer() {
        val isLoggedIn = tinyDB.getBoolean("is_logged_in")
        val email = tinyDB.getString("user_email")
        if (!isLoggedIn || email.isEmpty()) return

        val userKey = getUserCloudKey(email)
        val userRef = database.getReference("Users").child(userKey)

        binding.cloudSyncStatusTxt.text = "Connecting to Cloud..."

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        // Cache cloud data locally
                        if (user.name.isNotEmpty()) tinyDB.putString("user_name", user.name)
                        if (user.phone.isNotEmpty()) tinyDB.putString("user_phone", user.phone)
                        if (user.address.isNotEmpty()) tinyDB.putString("user_address", user.address)
                        if (user.memberTier.isNotEmpty()) tinyDB.putString("user_tier", user.memberTier)
                        if (user.points > 0) tinyDB.putInt("user_points", user.points)
                        if (user.ordersCount > 0) tinyDB.putInt("user_orders_count", user.ordersCount)

                        loadLocalProfile()
                        binding.cloudSyncStatusTxt.text = "Saved on Cloud Server"
                    }
                } else {
                    // Initialize new profile on cloud server
                    val currentName = tinyDB.getString("user_name").ifEmpty { email.substringBefore("@") }
                    val currentPhone = tinyDB.getString("user_phone").ifEmpty { "+91 98765 43210" }
                    val currentAddress = tinyDB.getString("user_address").ifEmpty { "Campus Block 3, Room 102" }

                    val newUser = UserModel(
                        name = currentName,
                        email = email,
                        phone = currentPhone,
                        address = currentAddress,
                        memberTier = "Gold Member",
                        points = 240,
                        ordersCount = 8,
                        lastUpdated = System.currentTimeMillis()
                    )

                    userRef.setValue(newUser)
                        .addOnSuccessListener {
                            binding.cloudSyncStatusTxt.text = "Saved on Cloud Server"
                        }
                        .addOnFailureListener {
                            binding.cloudSyncStatusTxt.text = "Offline (Cached Locally)"
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.cloudSyncStatusTxt.text = "Offline (Cached Locally)"
            }
        })
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.editProfileHeaderBtn.setOnClickListener { showEditProfileDialog() }
        binding.editDetailsTxtBtn.setOnClickListener { showEditProfileDialog() }
        binding.avatarEditBadge.setOnClickListener { showEditProfileDialog() }

        binding.guestLoginPromptBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            tinyDB.putBoolean("is_logged_in", false)
            tinyDB.putString("user_email", "")
            tinyDB.putString("user_name", "")

            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        binding.ordersMenuBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.favoritesMenuBtn.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val nameEdt = dialogView.findViewById<EditText>(R.id.dialogNameEdt)
        val phoneEdt = dialogView.findViewById<EditText>(R.id.dialogPhoneEdt)
        val addressEdt = dialogView.findViewById<EditText>(R.id.dialogAddressEdt)
        val cancelBtn = dialogView.findViewById<View>(R.id.dialogCancelBtn)
        val saveBtn = dialogView.findViewById<View>(R.id.dialogSaveBtn)

        val currentName = tinyDB.getString("user_name")
        val currentPhone = tinyDB.getString("user_phone")
        val currentAddress = tinyDB.getString("user_address")
        val email = tinyDB.getString("user_email")

        nameEdt.setText(currentName)
        phoneEdt.setText(if (currentPhone.isNotEmpty()) currentPhone else "+91 98765 43210")
        addressEdt.setText(if (currentAddress.isNotEmpty()) currentAddress else "Campus Block 3, Room 102")

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        cancelBtn.setOnClickListener { dialog.dismiss() }

        saveBtn.setOnClickListener {
            val newName = nameEdt.text.toString().trim()
            val newPhone = phoneEdt.text.toString().trim()
            val newAddress = addressEdt.text.toString().trim()

            if (newName.isEmpty()) {
                nameEdt.error = "Please enter your name"
                return@setOnClickListener
            }

            // Save locally
            tinyDB.putString("user_name", newName)
            tinyDB.putString("user_phone", newPhone)
            tinyDB.putString("user_address", newAddress)
            loadLocalProfile()

            // Save to Free Firebase Cloud Server
            if (email.isNotEmpty()) {
                val userKey = getUserCloudKey(email)
                val userRef = database.getReference("Users").child(userKey)

                val updatedUser = UserModel(
                    name = newName,
                    email = email,
                    phone = newPhone,
                    address = newAddress,
                    memberTier = tinyDB.getString("user_tier").ifEmpty { "Gold Member" },
                    points = if (tinyDB.getInt("user_points") > 0) tinyDB.getInt("user_points") else 240,
                    ordersCount = if (tinyDB.getInt("user_orders_count") > 0) tinyDB.getInt("user_orders_count") else 8,
                    lastUpdated = System.currentTimeMillis()
                )

                binding.cloudSyncStatusTxt.text = "Saving to Cloud..."

                userRef.setValue(updatedUser)
                    .addOnSuccessListener {
                        binding.cloudSyncStatusTxt.text = "Saved on Cloud Server"
                        Toast.makeText(this, "Profile updated & saved to Cloud Server!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        binding.cloudSyncStatusTxt.text = "Saved Locally (Offline)"
                        Toast.makeText(this, "Saved locally. Cloud sync pending.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Profile saved locally", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        dialog.show()
    }
}

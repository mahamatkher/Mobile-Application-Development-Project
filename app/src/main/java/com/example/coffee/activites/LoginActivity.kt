package com.example.coffee.activites

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.coffee.Helper.TinyDB
import com.example.coffee.databinding.ActivityLoginBinding

import com.example.coffee.domain.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tinyDB = TinyDB(this)

        setupListeners()
    }

    private fun setupListeners() {
        // Standard Email Login
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailEdt.error = "Please enter your email"
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEdt.error = "Invalid email format"
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 4) {
                binding.passwordEdt.error = "Password must be at least 4 characters"
                Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userName = email.substringBefore("@")
                .replace(".", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

            saveSessionAndNavigate(
                isLoggedIn = true,
                email = email,
                name = if (userName.isNotEmpty()) userName else "Coffee Lover",
                welcomeMessage = "Welcome, $userName!"
            )
        }

        // Quick 1-Tap Demo Login as Hiba
        binding.quickLoginBtn.setOnClickListener {
            saveSessionAndNavigate(
                isLoggedIn = true,
                email = "hiba@coffee.com",
                name = "Hiba",
                welcomeMessage = "Welcome back, Hiba!"
            )
        }

        // Continue as Guest
        binding.guestBtn.setOnClickListener {
            saveSessionAndNavigate(
                isLoggedIn = false,
                email = "guest@coffee.com",
                name = "Guest",
                welcomeMessage = "Browsing as Guest"
            )
        }

        // Forgot password
        binding.forgotPassBtn.setOnClickListener {
            val email = binding.emailEdt.text.toString().trim()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please enter your email above to reset password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSessionAndNavigate(
        isLoggedIn: Boolean,
        email: String,
        name: String,
        welcomeMessage: String
    ) {
        tinyDB.putBoolean("is_logged_in", isLoggedIn)
        tinyDB.putString("user_email", email)
        tinyDB.putString("user_name", name)

        if (isLoggedIn && email.isNotEmpty() && email != "guest@coffee.com") {
            val userKey = email.trim().lowercase().replace(".", "_").replace("@", "_at_")
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userKey)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val cloudUser = snapshot.getValue(UserModel::class.java)
                        if (cloudUser != null) {
                            if (cloudUser.name.isNotEmpty()) tinyDB.putString("user_name", cloudUser.name)
                            if (cloudUser.phone.isNotEmpty()) tinyDB.putString("user_phone", cloudUser.phone)
                            if (cloudUser.address.isNotEmpty()) tinyDB.putString("user_address", cloudUser.address)
                            if (cloudUser.points > 0) tinyDB.putInt("user_points", cloudUser.points)
                            if (cloudUser.ordersCount > 0) tinyDB.putInt("user_orders_count", cloudUser.ordersCount)
                        }
                    } else {
                        val newUser = UserModel(
                            name = name,
                            email = email,
                            phone = "+91 98765 43210",
                            address = "Campus Block 3, Room 102",
                            memberTier = "Gold Member",
                            points = 240,
                            ordersCount = 8,
                            lastUpdated = System.currentTimeMillis()
                        )
                        userRef.setValue(newUser)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

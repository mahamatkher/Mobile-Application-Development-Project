package com.example.coffee.activites

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffee.Helper.ChangeNumberItemsListener
import com.example.coffee.Helper.ManagmentCart
import com.example.coffee.Helper.TinyDB
import com.example.coffee.R
import com.example.coffee.adapters.CartAdapter
import com.example.coffee.databinding.ActivityCartBinding
import com.example.coffee.databinding.DialogOrderSuccessBinding
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart
    private lateinit var tinyDB: TinyDB

    private var appliedDiscountAmount: Double = 0.0
    private var appliedPromoCode: String = ""
    private var selectedPaymentMethod: Int = 0

    private val paymentMethods = arrayOf(
        PaymentOption("💵", "Cash on Delivery", "Pay in cash upon coffee arrival"),
        PaymentOption("☕", "Coffee Club Wallet", "Pay with loyalty balance (240 Pts)"),
        PaymentOption("💳", "Credit / Debit Card", "Instant pay via •••• 8821"),
        PaymentOption("📱", "Google Pay / UPI", "Fast one-tap UPI payment")
    )

    data class PaymentOption(val emoji: String, val title: String, val subtitle: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        tinyDB = TinyDB(this)

        setupWindowInsets()
        setupDeliveryAddress()
        setupPaymentMethod()
        setupPromoCode()
        setupListeners()
        calculateCart()
        initCartList()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            binding.headerLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = statusBar.top + 8
            }

            binding.bottomCheckoutPanel.setPadding(
                binding.bottomCheckoutPanel.paddingLeft,
                binding.bottomCheckoutPanel.paddingTop,
                binding.bottomCheckoutPanel.paddingRight,
                navBar.bottom + 16
            )
            insets
        }
    }

    private fun setupDeliveryAddress() {
        var savedAddress = tinyDB.getString("user_address")
        if (savedAddress.isNullOrBlank()) {
            savedAddress = "Sector 14, Chandigarh, India"
            tinyDB.putString("user_address", savedAddress)
        }
        binding.deliveryAddressTxt.text = savedAddress

        binding.changeAddressBtn.setOnClickListener {
            showEditAddressDialog()
        }
    }

    private fun showEditAddressDialog() {
        val input = EditText(this)
        input.setText(binding.deliveryAddressTxt.text)
        input.setSelection(input.text.length)
        input.setPadding(40, 30, 40, 30)

        AlertDialog.Builder(this)
            .setTitle("Change Delivery Address")
            .setMessage("Enter delivery address for freshly brewed coffee:")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newAddress = input.text.toString().trim()
                if (newAddress.isNotEmpty()) {
                    tinyDB.putString("user_address", newAddress)
                    binding.deliveryAddressTxt.text = newAddress
                    Toast.makeText(this, "Address updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupPaymentMethod() {
        updatePaymentDisplay()

        val paymentClick = View.OnClickListener {
            val options = paymentMethods.map { "${it.emoji} ${it.title}" }.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Select Payment Method")
                .setSingleChoiceItems(options, selectedPaymentMethod) { dialog, which ->
                    selectedPaymentMethod = which
                    updatePaymentDisplay()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.changePaymentBtn.setOnClickListener(paymentClick)
        binding.paymentMethodRow.setOnClickListener(paymentClick)
    }

    private fun updatePaymentDisplay() {
        val option = paymentMethods[selectedPaymentMethod]
        binding.paymentMethodEmoji.text = option.emoji
        binding.paymentMethodTitle.text = option.title
        binding.paymentMethodSubtitle.text = option.subtitle
    }

    private fun setupPromoCode() {
        binding.suggestedCouponChip.setOnClickListener {
            binding.discountEdt.setText("COFFEE10")
            applyPromoCode()
        }

        binding.button.setOnClickListener {
            applyPromoCode()
        }

        binding.removeCouponBtn.setOnClickListener {
            appliedDiscountAmount = 0.0
            appliedPromoCode = ""
            binding.appliedCouponBadge.visibility = View.GONE
            binding.discountRow.visibility = View.GONE
            binding.discountEdt.text.clear()
            calculateCart()
            Toast.makeText(this, "Promo code removed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyPromoCode() {
        val code = binding.discountEdt.text.toString().trim().uppercase(Locale.US)
        val cartList = managmentCart.getListCart()
        val totalFee = managmentCart.getTotalFee()

        if (cartList.isEmpty() || totalFee <= 0.0) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if (code.isEmpty()) {
            Toast.makeText(this, "Please enter a promo code", Toast.LENGTH_SHORT).show()
            return
        }

        when (code) {
            "COFFEE10" -> {
                appliedPromoCode = code
                appliedDiscountAmount = Math.round(totalFee * 0.10 * 100.0) / 100.0
                showCouponSuccess("10% discount applied!")
            }
            "FREESHIP" -> {
                appliedPromoCode = code
                appliedDiscountAmount = 2.00
                showCouponSuccess("Free delivery fee applied!")
            }
            "SPECIAL3" -> {
                appliedPromoCode = code
                appliedDiscountAmount = 3.00.coerceAtMost(totalFee)
                showCouponSuccess("$3.00 discount applied!")
            }
            "HIBA" -> {
                appliedPromoCode = code
                appliedDiscountAmount = Math.round(totalFee * 0.15 * 100.0) / 100.0
                showCouponSuccess("VIP 15% discount applied!")
            }
            else -> {
                Toast.makeText(this, "Invalid promo code. Try 'COFFEE10'", Toast.LENGTH_SHORT).show()
                return
            }
        }
        calculateCart()
    }

    private fun showCouponSuccess(msg: String) {
        binding.appliedCouponBadge.visibility = View.VISIBLE
        binding.appliedCouponTxt.text = "✓ '$appliedPromoCode' applied! You save $${String.format(Locale.US, "%.2f", appliedDiscountAmount)}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.clearCartBtn.setOnClickListener {
            if (managmentCart.getListCart().isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Clear Cart")
                    .setMessage("Are you sure you want to remove all items from your cart?")
                    .setPositiveButton("Clear") { _, _ ->
                        managmentCart.clearCart()
                        calculateCart()
                        initCartList()
                        Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "Your cart is already empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.exploreMenuBtn.setOnClickListener {
            finish()
        }

        binding.processBtn.setOnClickListener {
            processCheckout()
        }
    }

    private fun calculateCart() {
        val cartList = managmentCart.getListCart()
        val totalFee = managmentCart.getTotalFee()

        if (cartList.isEmpty()) {
            binding.emptyCartView.visibility = View.VISIBLE
            binding.cartContentLayout.visibility = View.GONE
            binding.bottomCheckoutPanel.visibility = View.GONE
            binding.clearCartBtn.visibility = View.GONE
            return
        }

        binding.emptyCartView.visibility = View.GONE
        binding.cartContentLayout.visibility = View.VISIBLE
        binding.bottomCheckoutPanel.visibility = View.VISIBLE
        binding.clearCartBtn.visibility = View.VISIBLE

        val count = cartList.sumOf { it.numberInCart }
        binding.itemCountBadge.text = "$count ${if (count == 1) "Item" else "Items"}"

        // Delivery fee: $2.00, but FREE if totalFee >= $15.00
        val deliveryFee = if (totalFee >= 15.0) 0.0 else 2.00
        val tax = Math.round(totalFee * 0.05 * 100.0) / 100.0 // 5% tax

        val discount = appliedDiscountAmount.coerceAtMost(totalFee)
        val grandTotal = (totalFee - discount + deliveryFee + tax).coerceAtLeast(0.0)

        binding.apply {
            totalFeeTxt.text = String.format(Locale.US, "$%.2f", totalFee)
            totalTaxtxt.text = String.format(Locale.US, "$%.2f", tax)

            if (deliveryFee == 0.0) {
                deliveryTxt.text = "FREE 🎉"
                deliveryTxt.setTextColor(getColor(R.color.orange))
            } else {
                deliveryTxt.text = String.format(Locale.US, "$%.2f", deliveryFee)
                deliveryTxt.setTextColor(getColor(R.color.white))
            }

            if (discount > 0.0) {
                discountRow.visibility = View.VISIBLE
                discountFeeTxt.text = String.format(Locale.US, "-$%.2f", discount)
            } else {
                discountRow.visibility = View.GONE
            }

            val totalStr = String.format(Locale.US, "$%.2f", grandTotal)
            totalTxt.text = totalStr
            bottomTotalTxt.text = totalStr
        }
    }

    private fun processCheckout() {
        val cartList = managmentCart.getListCart()
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val totalFee = managmentCart.getTotalFee()
        val deliveryFee = if (totalFee >= 15.0) 0.0 else 2.00
        val tax = Math.round(totalFee * 0.05 * 100.0) / 100.0
        val discount = appliedDiscountAmount.coerceAtMost(totalFee)
        val grandTotal = (totalFee - discount + deliveryFee + tax).coerceAtLeast(0.0)

        val earnedPoints = ((grandTotal * 5).toInt()).coerceAtLeast(20)

        // Update local points & orders in TinyDB
        val currentPoints = tinyDB.getInt("user_points")
        val currentOrders = tinyDB.getInt("user_orders_count")
        tinyDB.putInt("user_points", currentPoints + earnedPoints)
        tinyDB.putInt("user_orders_count", currentOrders + 1)

        // Show Success Dialog
        val dialogBinding = DialogOrderSuccessBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val orderNum = (10000..99999).random()
        dialogBinding.dialogOrderId.text = "Order #CF-$orderNum"
        dialogBinding.dialogDeliveryAddress.text = binding.deliveryAddressTxt.text
        dialogBinding.dialogEarnedPoints.text = "+$earnedPoints Pts ⭐"

        dialogBinding.dialogDoneBtn.setOnClickListener {
            dialog.dismiss()
            managmentCart.clearCart()
            finish()
        }

        dialog.show()
    }

    private fun initCartList() {
        binding.listView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.listView.adapter = CartAdapter(
            managmentCart.getListCart(),
            this,
            object : ChangeNumberItemsListener {
                override fun onChanged() {
                    calculateCart()
                }
            }
        )
    }
}

package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Environment
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityOrderSummaryBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class OrderSummaryActivity : AppCompatActivity(), PaymentResultListener {



    private lateinit var binding: ActivityOrderSummaryBinding
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("orderId") ?: return
        val uid = auth.uid ?: return

        loadOrderDetails(uid, orderId)

        binding.backBtn.setOnClickListener { finish() }
        binding.cancelOrderBtn.setOnClickListener { cancelOrder(uid, orderId) }

        Checkout.preload(applicationContext)

        binding.downloadReceiptLayout.setOnClickListener {
            showReceiptDialog()
        }

        Glide.with(this).asGif().load(R.drawable.downloads).into(binding.downloadIcon)
    }

    private fun disableDownloadReceipt() {
        binding.downloadReceiptLayout.isEnabled = false
        binding.downloadReceiptLayout.alpha = 0.4f
    }

    private fun enableDownloadReceipt() {
        binding.downloadReceiptLayout.isEnabled = true
        binding.downloadReceiptLayout.alpha = 1f
    }

    private fun showReceiptDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Download Receipt")
            .setMessage("Do you want to download your order receipt?")
            .setPositiveButton("Yes") { _, _ ->
                generatePDFReceipt()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun generatePDFReceipt() {
        val orderId = intent.getStringExtra("orderId") ?: return
        val uid = auth.uid ?: return

        database.child("Orders").child(uid).child(orderId).get()
            .addOnSuccessListener { snap ->

                val order = snap.getValue(OrderModel::class.java) ?: return@addOnSuccessListener

                database.child("users").child(uid).get().addOnSuccessListener { userSnap ->

                    val name = userSnap.child("name").value?.toString() ?: "-"
                    val email = userSnap.child("email").value?.toString() ?: "-"
                    val phone = userSnap.child("phone").value?.toString() ?: "-"
                    val gender = userSnap.child("gender").value?.toString() ?: "-"
                    val address = userSnap.child("address").value?.toString() ?: "-"
                    val city = userSnap.child("city").value?.toString() ?: "-"
                    val pincode = userSnap.child("pincode").value?.toString() ?: "-"

                    createPDF(order, name, email, phone, gender, address, city, pincode)
                }
            }
    }

    private fun getDownloadsFolder(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val myCafeDir = File(downloadsDir, "MyCafe")

        if (!myCafeDir.exists()) myCafeDir.mkdirs()

        return myCafeDir
    }

    private fun createPDF(
        order: OrderModel,
        name: String,
        email: String,
        phone: String,
        gender: String,
        address: String,
        city: String,
        pincode: String
    ) {

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = sdf.format(Date(order.timestamp))

        val pdfDoc = Document(PageSize.A4, 36f, 36f, 40f, 36f)
        val fileName = "Order_${order.orderId}.pdf"

        val file = File(getDownloadsFolder(), fileName)
        PdfWriter.getInstance(pdfDoc, FileOutputStream(file))
        pdfDoc.open()

        val darkCoffee = BaseColor(75, 50, 35)
        val lightCoffee = BaseColor(245, 235, 225)
        val mediumCoffee = BaseColor(210, 190, 175)

        val titleFont = Font(Font.FontFamily.HELVETICA, 26f, Font.BOLD, darkCoffee)
        val subTitleFont = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD, darkCoffee)
        val normalFont = Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL)
        val boldFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)

        val headerFontWhite = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.WHITE)

        val title = Paragraph("MY CAFÉ\n", titleFont)
        title.alignment = Element.ALIGN_CENTER
        pdfDoc.add(title)

        val subtitle = Paragraph("Order Receipt\n\n", subTitleFont)
        subtitle.alignment = Element.ALIGN_CENTER
        pdfDoc.add(subtitle)

        val line = LineSeparator()
        line.lineColor = darkCoffee
        pdfDoc.add(Chunk(line))
        pdfDoc.add(Paragraph("\n"))

        val custTable = PdfPTable(1)
        custTable.widthPercentage = 100f

        val custCell = PdfPCell()
        custCell.backgroundColor = lightCoffee
        custCell.setPadding(14f)

        val customerInfo = """
        CUSTOMER DETAILS

        Name: $name
        Email: $email
        Phone: $phone
        Gender: $gender

        Address: $address
        City: $city  |  Pincode: $pincode
    """.trimIndent()

        custCell.addElement(Paragraph(customerInfo, normalFont))
        custCell.border = Rectangle.NO_BORDER
        custTable.addCell(custCell)

        pdfDoc.add(custTable)
        pdfDoc.add(Paragraph("\n"))

        val orderDetails = """
        ORDER DETAILS
        
        Order ID: ${order.orderId}
        Order Date: $date
        Status: ${order.status}
    """.trimIndent()

        val orderTable = PdfPTable(1)
        val orderCell = PdfPCell()
        orderCell.borderColor = mediumCoffee
        orderCell.setPadding(12f)
        orderCell.addElement(Paragraph(orderDetails, normalFont))
        orderTable.addCell(orderCell)

        pdfDoc.add(orderTable)
        pdfDoc.add(Paragraph("\n"))

        val itemsTable = PdfPTable(3)
        itemsTable.widthPercentage = 100f
        itemsTable.setWidths(floatArrayOf(3f, 1f, 2f))

        val headers = listOf("Item", "Qty", "Price")
        headers.forEach {
            val hCell = PdfPCell(Phrase(it, headerFontWhite))
            hCell.backgroundColor = darkCoffee
            hCell.horizontalAlignment = Element.ALIGN_CENTER
            hCell.setPadding(10f)
            hCell.border = Rectangle.NO_BORDER
            itemsTable.addCell(hCell)
        }

        var itemsTotal = 0.0

        order.items.forEachIndexed { index, item ->
            val rowBg = if (index % 2 == 0) lightCoffee else BaseColor.WHITE

            val price = item.price * item.numberInCart
            itemsTotal += price

            val row = listOf(item.title, "${item.numberInCart}", "₹$price")

            row.forEach {
                val cell = PdfPCell(Phrase(it, normalFont))
                cell.backgroundColor = rowBg
                cell.setPadding(10f)
                cell.border = Rectangle.NO_BORDER
                itemsTable.addCell(cell)
            }
        }

        pdfDoc.add(itemsTable)
        pdfDoc.add(Paragraph("\n"))

//        val delivery = order.delivery ?: 15.0
//        val tax = order.tax ?: (order.totalAmount - (itemsTotal + delivery))

        val delivery = order.delivery ?: 15.0
        val tax = order.tax ?: 0.0
        val subtotal = order.subtotal ?: itemsTotal
        val grandTotal = order.totalAmount // ✅ FINAL amount


//        val itemsTotalFormatted = String.format("%.2f", itemsTotal)
//        val taxFormatted = String.format("%.2f", tax)
//        val deliveryFormatted = String.format("%.2f", delivery)
//        val grandFormatted = String.format("%.2f", order.totalAmount)
//
//        val summaryTable = PdfPTable(2)
//        summaryTable.widthPercentage = 60f
//        summaryTable.horizontalAlignment = Element.ALIGN_RIGHT
//
//        fun addSummaryRow(label: String, value: String, bold: Boolean = false) {
//            val f = if (bold) boldFont else normalFont
//            summaryTable.addCell(Phrase(label, f))
//
//            val vCell = PdfPCell(Phrase(value, f))
//            vCell.horizontalAlignment = Element.ALIGN_RIGHT
//            vCell.border = Rectangle.NO_BORDER
//            summaryTable.addCell(vCell)
//        }
//
//        addSummaryRow("Items Total:", "₹$itemsTotalFormatted")
//        addSummaryRow("Tax (2%):", "₹$taxFormatted")
//        addSummaryRow("Delivery:", "₹$deliveryFormatted")
//
//        if (!order.appliedOffer.isNullOrEmpty()) {
//            addSummaryRow("Offer Applied:", order.appliedOffer!!)
//            addSummaryRow("Discount:", "-₹${String.format("%.2f", order.discountAmount)}")
//        }
//
//        addSummaryRow("Grand Total:", "₹$grandFormatted", true)
//
//        pdfDoc.add(summaryTable)
        val itemsTotalFormatted = String.format("%.2f", subtotal)
        val taxFormatted = String.format("%.2f", tax)
        val deliveryFormatted = String.format("%.2f", delivery)
        val discountFormatted = String.format("%.2f", order.discountAmount)
        val grandFormatted = String.format("%.2f", grandTotal)

        val summaryTable = PdfPTable(2)
        summaryTable.widthPercentage = 60f
        summaryTable.horizontalAlignment = Element.ALIGN_RIGHT

        fun addSummaryRow(label: String, value: String, bold: Boolean = false) {
            val f = if (bold) boldFont else normalFont
            summaryTable.addCell(Phrase(label, f))

            val vCell = PdfPCell(Phrase(value, f))
            vCell.horizontalAlignment = Element.ALIGN_RIGHT
            vCell.border = Rectangle.NO_BORDER
            summaryTable.addCell(vCell)
        }

        addSummaryRow("Items Total:", "₹$itemsTotalFormatted")
        addSummaryRow("Tax:", "₹$taxFormatted")
        addSummaryRow("Delivery:", "₹$deliveryFormatted")

        if (!order.appliedOffer.isNullOrEmpty()) {
            addSummaryRow("Offer Applied:", order.appliedOffer!!)
            addSummaryRow("Discount:", "-₹$discountFormatted")
        }

        addSummaryRow("Grand Total:", "₹$grandFormatted", true)

        pdfDoc.add(summaryTable)

        pdfDoc.add(Paragraph("\n\n"))

        val footer = Paragraph("Thank you for ordering from My Café!\nVisit Again ☕", boldFont)
        footer.alignment = Element.ALIGN_CENTER
        pdfDoc.add(footer)

        pdfDoc.close()

        Toast.makeText(
            this,
            "PDF saved in: Downloads/MyCafe/$fileName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun loadOrderDetails(uid: String, orderId: String) {

        database.child("Orders").child(uid).child(orderId).get()
            .addOnSuccessListener { snap ->

                val order = snap.getValue(OrderModel::class.java) ?: return@addOnSuccessListener

                binding.orderId.text = "Order ID: ${order.orderId}"
                binding.orderStatus.text = order.status
                binding.orderAmount.text = "Amount: ₹${order.totalAmount}"

                val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                binding.orderDate.text = "Date: ${sdf.format(Date(order.timestamp))}"

                binding.orderItems.text = "Items:\n" + order.items.joinToString("\n") {
                    "• ${it.title} x${it.numberInCart}"
                }
                binding.orderItems.text = "Items:\n" + order.items.joinToString("\n") {
                    "• ${it.title} (${it.size}) x${it.numberInCart}  → ₹${it.price * it.numberInCart}"
                }


                val applied = order.appliedOffer ?: ""
                val discount = order.discountAmount

                if (applied.isNotEmpty()) {
                    binding.appliedOfferLayout.visibility = View.VISIBLE
                    binding.appliedOfferText.text = "Offer Applied: $applied"
                    binding.discountText.text = "Discount: ₹${String.format("%.2f", discount)}"
                } else {
                    binding.appliedOfferLayout.visibility = View.GONE
                }

                if (order.status == "Cancelled") {
                    disableDownloadReceipt()
                } else {
                    enableDownloadReceipt()
                }

                when (order.status) {

                    "Cancelled" -> {
                        disableCancelButton()
                        disablePaymentOptions()
                    }

                    "Approved" -> {
                        disableCancelButton()   // User cannot cancel after approved
                        disablePaymentOptions() // No payment allowed after approval
                    }

                    else -> {
                        enablePaymentOptions()
                        binding.googlePayLayout.setOnClickListener {
                            openGooglePay(order.totalAmount.toString(), order.orderId)
                        }
                        binding.razorPayLayout.setOnClickListener {
                            startRazorpayPayment(order.totalAmount.toString(), order.orderId)
                        }
                    }
                }
            }
    }

    private fun cancelOrder(uid: String, orderId: String) {
        database.child("Orders").child(uid).child(orderId)
            .child("status").setValue("Cancelled")
            .addOnSuccessListener {

                database.child("Orders").child(uid).child(orderId)
                    .get()
                    .addOnSuccessListener { snap ->

                        val redeemKey = snap.child("appliedRedeemKey").value?.toString() ?: ""

                        if (redeemKey.isNotEmpty()) {
                            FirebaseDatabase.getInstance().getReference("users")
                                .child(uid)
                                .child("rewards")
                                .child("redeemed")
                                .child(redeemKey)
                                .child("used")
                                .setValue(false) // ⭐ RESTORE OFFER
                        }
                    }

                Toast.makeText(this, "Order cancelled", Toast.LENGTH_SHORT).show()
                finish()
            }
    }



    private fun openGooglePay(amount: String, orderId: String) {
        val upiUri = android.net.Uri.parse(
            "upi://pay?pa=mycafe@okicici&pn=My Cafe&tn=Payment for Order $orderId&am=$amount&cu=INR"
        )
        startActivity(Intent(Intent.ACTION_VIEW, upiUri))
    }


    private fun updateOrderStatus(status: String) {
        val orderId = intent.getStringExtra("orderId") ?: return
        val uid = auth.uid ?: return

        val orderRef = database.child("Orders").child(uid).child(orderId)

        orderRef.child("status").setValue(status)

        if (status == "Paid") {
            orderRef.get().addOnSuccessListener { snap ->
                val redeemKey = snap.child("appliedRedeemKey").value?.toString() ?: ""

                if (redeemKey.isNotEmpty()) {
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(uid)
                        .child("rewards")
                        .child("redeemed")
                        .child(redeemKey)
                        .child("used")
                        .setValue(true)
                }
            }
        }

    }




    private fun startRazorpayPayment(amount: String, orderId: String) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_123456789")

        val options = JSONObject()
        options.put("name", "My Cafe")
        options.put("description", "Order #$orderId")
        options.put("currency", "INR")
        options.put("amount", (amount.toDouble() * 100).toInt())

        val prefill = JSONObject()
        prefill.put("email", "user@example.com")
        prefill.put("contact", "9999999999")
        options.put("prefill", prefill)

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {

        val uid = auth.uid ?: return
        val orderId = intent.getStringExtra("orderId") ?: return

        // 1️⃣ Update status
        updateOrderStatus("Paid")

        // 2️⃣ Mark offer as USED immediately
        database.child("Orders").child(uid).child(orderId).get()
            .addOnSuccessListener { snap ->
                val redeemKey = snap.child("appliedRedeemKey").value?.toString() ?: ""
                if (redeemKey.isNotEmpty()) {

                    FirebaseDatabase.getInstance().getReference("users")
                        .child(uid)
                        .child("rewards")
                        .child("redeemed")
                        .child(redeemKey)
                        .child("used")
                        .setValue(true)
                }
            }

        disablePaymentOptions()
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
    }


    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show()
    }

    private fun disableCancelButton() {
        binding.cancelOrderBtn.isEnabled = false
        binding.cancelOrderBtn.alpha = 0.4f
    }

    private fun disablePaymentOptions() {
        binding.googlePayLayout.isEnabled = false
        binding.googlePayLayout.alpha = 0.4f
        binding.razorPayLayout.isEnabled = false
        binding.razorPayLayout.alpha = 0.4f
    }

    private fun enablePaymentOptions() {
        binding.googlePayLayout.isEnabled = true
        binding.googlePayLayout.alpha = 1f
        binding.razorPayLayout.isEnabled = true
        binding.razorPayLayout.alpha = 1f
    }

}

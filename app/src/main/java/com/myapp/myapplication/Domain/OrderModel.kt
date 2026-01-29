//package com.myapp.myapplication.Domain
//
//data class OrderModel(
//
//    // ORDER INFO
//    var orderId: String = "",
//    var timestamp: Long = System.currentTimeMillis(),
//    var status: String = "Pending",
//    var totalAmount: Double = 0.0,
//
//    // USER INFO
//    var userId: String = "",
//    var userEmail: String = "",
//    var userName: String = "",
//
//    // USER FILLED DETAILS
//    var phone: String = "",
//    var gender: String = "",
//    var city: String = "",
//    var pincode: String = "",
//    var address: String = "",
//
//    // ITEMS
//    var items: ArrayList<ItemsModel> = ArrayList(),
//
//    // EXTRA (Auto-generated / optional stored fields)
//    var readableDate: String = "",
//    var orderItemsSummary: String = "",
//
//    // Pricing breakdown & applied offer (optional)
//    var subtotal: Double? = null,
//    var tax: Double? = null,
//    var delivery: Double? = null,
//    var appliedOffer: String? = null,
//    var discountAmount: Double = 0.0,
//    var appliedRedeemKey: String? = null
//) {
//    fun generateExtras() {
//        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a")
//        readableDate = sdf.format(java.util.Date(timestamp))
//        orderItemsSummary = items.joinToString { it.title }
//    }
//}
package com.myapp.myapplication.Domain

data class OrderModel(

    // ORDER INFO
    var orderId: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var status: String = "Pending",
    var totalAmount: Double = 0.0, // ✅ FINAL amount after discount

    // USER INFO
    var userId: String = "",
    var userEmail: String = "",
    var userName: String = "",

    // USER FILLED DETAILS
    var phone: String = "",
    var gender: String = "",
    var city: String = "",
    var pincode: String = "",
    var address: String = "",

    // ITEMS
    var items: ArrayList<ItemsModel> = ArrayList(),

    // EXTRA
    var readableDate: String = "",
    var orderItemsSummary: String = "",


    // PRICING BREAKDOWN
    var subtotal: Double? = null,
    var tax: Double? = null,
    var delivery: Double? = null,
    var appliedOffer: String? = null,
    var discountAmount: Double = 0.0,
    var appliedRedeemKey: String? = null
) {
    fun generateExtras() {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a")
        readableDate = sdf.format(java.util.Date(timestamp))
        orderItemsSummary = items.joinToString { it.title }
    }
}

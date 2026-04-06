package com.example.kot1041_asm.data.model

// ==========================================
// MODELS TỪ DATABASE
// ==========================================

data class Account(
    val id: String,
    val Email: String,
    val FullName: String,
    val Password: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Category(
    val _id: String,
    val CateName: String,
    val iconUrl: String? = "",
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ProductImage(
    val _id: String? = null,
    val url: String,
    val context: String? = "",
    val isDefault: Boolean = false
)

data class Product(
    val _id: String,
    val ProductName: String,
    val Description: String? = "",
    val Price: Double,
    val CateID: Any?,
    val productImage: List<ProductImage> = emptyList(),
    val rating: Double = 0.0,
    val numReviews: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Bookmark(
    val _id: String,
    val AccountID: String,
    val ProductID: Product?,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class CartItem(
    val _id: String,
    val AccountID: String,
    val ProductID: Product?,
    val Quantity: Int = 1,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Bill(
    val _id: String,
    val accountId: String,          // Thay đổi
    val RecipientName: String,      // Thay đổi
    val AddressDetail: String,      // Thay đổi
    val Date: String,
    val Note: String? = "",
    val TotalAmount: Double = 0.0,
    val Status: String = "pending",
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class BillDetail(
    val _id: String,
    val BillID: String,
    val ProductID: Product?,
    val Quantity: Int = 1,
    val UnitPrice: Double = 0.0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Address(
    val _id: String,
    val accountId: String,
    val RecipientName: String,
    val AddressDetail: String,
    val isDefault: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// ==========================================
// API RESPONSES
// ==========================================

data class ApiResponse<T>(
    val message: String,
    val data: T?,
    val action: String? = null
)

data class OrderResponse(
    val bill: Bill,
    val details: List<BillDetail>
)

// ==========================================
// REQUEST MODELS (GỬI LÊN SERVER)
// ==========================================

data class RegisterRequest(
    val Email: String,
    val Password: String,
    val FullName: String
)

data class LoginRequest(
    val Email: String,
    val Password: String
)

data class AddToCartRequest(
    val AccountID: String,
    val ProductID: String,
    val Quantity: Int = 1
)

data class BookmarkRequest(
    val AccountID: String,
    val ProductID: String
)

data class PlaceOrderRequest(
    val accountId: String,          // Thay đổi
    val RecipientName: String,      // Thay đổi
    val AddressDetail: String,      // Thay đổi
    val Note: String? = "",
    val items: List<CartItemRequest>
)

data class CartItemRequest(
    val ProductID: String,
    val Quantity: Int
)

data class UpdateCartQtyRequest(val Quantity: Int)

data class AddressRequest(
    val accountId: String,
    val RecipientName: String,
    val AddressDetail: String,
    val isDefault: Boolean = false
)
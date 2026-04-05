package com.example.kot1041_asm.data.model

/**
 * 1. Account (User)
 * Backend Schema: Account
 */
data class Account(
    val _id: String,
    val Email: String,
    val FullName: String,
    val Password: String? = null, // FE thường không nhận trường này trừ khi login/register
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 2. Category
 * Backend Schema: Category
 */
data class Category(
    val _id: String,
    val CateName: String,
    val iconUrl: String? = "",
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 3. Product & ProductImage
 * Backend Schema: Product
 */
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
    // Ánh xạ thành model Category do BE thường gọi .populate("CateID")
    val CateID: Category?,
    val productImage: List<ProductImage> = emptyList(),
    val rating: Double = 0.0,
    val numReviews: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 4. Bookmark (Yêu thích)
 * Backend Schema: Bookmark
 */
data class Bookmark(
    val _id: String,
    val AccountID: String,
    // Ánh xạ thành model Product do BE gọi .populate("ProductID")
    val ProductID: Product?,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 5. CartItem (Giỏ hàng)
 * Backend Schema: CartItem
 */
data class CartItem(
    val _id: String,
    val AccountID: String,
    // Ánh xạ thành model Product do BE gọi .populate("ProductID") để lấy ảnh, giá, tên...
    val ProductID: Product?,
    val Quantity: Int = 1,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 6. Bill (Hóa đơn)
 * Backend Schema: Bill
 */
data class Bill(
    val _id: String,
    val Date: String, // MongoDB trả về chuỗi ISO-8601 (VD: "2023-10-25T10:00:00.000Z")
    val Email: String,
    val FullName: String,
    val Phone: String,
    val Address: String,
    val Note: String? = "",
    val TotalAmount: Double = 0.0,
    val Status: String = "pending", // "pending", "confirmed", "shipping", "completed", "cancelled"
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 7. BillDetails (Chi tiết hóa đơn)
 * Backend Schema: BillDetails
 */
data class BillDetail(
    val _id: String,
    val BillID: String,
    // Ánh xạ thành model Product do BE gọi .populate("ProductID")
    val ProductID: Product?,
    val Quantity: Int = 1,
    val UnitPrice: Double = 0.0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 8. API Response Wrapper
 * Dùng để bọc mọi response trả về từ Backend (Axios/Express)
 */
data class ApiResponse<T>(
    val message: String,
    val data: T?,
    val action: String? = null // Hỗ trợ cho hàm toggleBookmark (trả về "added" hoặc "removed")
)

data class OrderResponse(
    val bill: Bill,
    val details: List<BillDetail>
)
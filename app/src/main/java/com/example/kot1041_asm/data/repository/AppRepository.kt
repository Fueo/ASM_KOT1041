package com.example.kot1041_asm.data.repository

import AddToCartRequest
import BookmarkRequest
import LoginRequest
import PlaceOrderRequest
import RegisterRequest
import com.example.kot1041_asm.data.api.ApiService
import com.example.kot1041_asm.data.api.RetrofitClient
import com.example.kot1041_asm.data.api.UpdateCartQtyRequest
import com.example.kot1041_asm.data.model.*
import retrofit2.Response

class AppRepository {

    private val api: ApiService = RetrofitClient.instance

    /**
     * Hàm dùng chung để xử lý try-catch và check lỗi HTTP cho MỌI api call.
     * Nó bóc tách 'data' từ 'ApiResponse' và gói vào 'Result' của Kotlin.
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    // Xử lý trường hợp call thành công nhưng data rỗng
                    Result.failure(Exception(body?.message ?: "Dữ liệu trả về bị rỗng"))
                }
            } else {
                // Parse lỗi từ backend (VD: Lỗi 400 trùng email, 404 không tìm thấy...)
                val errorBodyString = response.errorBody()?.string()
                // Trong thực tế bạn có thể dùng Gson để parse errorBodyString lấy ra trường "message"
                Result.failure(Exception("Lỗi máy chủ: ${response.code()} - $errorBodyString"))
            }
        } catch (e: Exception) {
            // Các lỗi mất mạng, timeout, v.v.
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // =========================================
    // AUTHENTICATION
    // =========================================
    suspend fun login(request: LoginRequest): Result<Account> = safeApiCall { api.login(request) }
    suspend fun register(request: RegisterRequest): Result<Account> = safeApiCall { api.register(request) }

    // =========================================
    // CATEGORIES
    // =========================================
    suspend fun getAllCategories(): Result<List<Category>> = safeApiCall { api.getAllCategories() }

    // =========================================
    // PRODUCTS (Đã cập nhật chuẩn BE)
    // =========================================
    // Gộp chung get all, tìm kiếm, lọc theo danh mục và phân trang vào 1 hàm duy nhất
    suspend fun getProducts(
        cateId: String? = null,
        keyword: String? = null,
        page: Int = 1,
        limit: Int = 6
    ): Result<List<Product>> = safeApiCall {
        api.getProducts(cateId, keyword, page, limit)
    }

    suspend fun getProductDetail(id: String): Result<Product> = safeApiCall { api.getProductDetail(id) }

    // =========================================
    // BOOKMARKS
    // =========================================
    suspend fun getBookmarks(accountId: String): Result<List<Bookmark>> = safeApiCall { api.getBookmarksByAccount(accountId) }
    suspend fun addBookmark(request: BookmarkRequest): Result<Bookmark> = safeApiCall { api.createBookmark(request) }
    suspend fun removeBookmark(bookmarkId: String): Result<Bookmark> = safeApiCall { api.deleteBookmark(bookmarkId) }

    // =========================================
    // CART
    // =========================================
    suspend fun getCart(accountId: String): Result<List<CartItem>> = safeApiCall { api.getCartByAccount(accountId) }
    suspend fun addToCart(request: AddToCartRequest): Result<CartItem> = safeApiCall { api.addToCart(request) }
    suspend fun updateCartQuantity(cartItemId: String, quantity: Int): Result<CartItem> = safeApiCall {
        api.updateCartItemQuantity(cartItemId, UpdateCartQtyRequest(quantity))
    }
    suspend fun removeFromCart(cartItemId: String): Result<CartItem> = safeApiCall { api.removeFromCart(cartItemId) }

    // =========================================
    // BILLS (Đã cập nhật chuẩn BE)
    // =========================================
    suspend fun placeOrder(request: PlaceOrderRequest): Result<Bill> = safeApiCall { api.placeOrder(request) }

    // Thêm hàm lấy lịch sử đơn hàng
    suspend fun getOrderHistoryByEmail(email: String): Result<List<OrderResponse>> = safeApiCall {
        api.getOrderHistoryByEmail(email)
    }

    // Thêm hàm lấy chi tiết đơn hàng
    suspend fun getBillDetail(billId: String): Result<OrderResponse> = safeApiCall {
        api.getBillDetail(billId)
    }
}
package com.example.kot1041_asm.data.repository

import com.example.kot1041_asm.data.api.ApiService
import com.example.kot1041_asm.data.api.RetrofitClient
import com.example.kot1041_asm.data.model.*
import retrofit2.Response

class AppRepository {

    private val api: ApiService = RetrofitClient.instance

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else if (body != null) {
                    @Suppress("UNCHECKED_CAST")
                    Result.success(Any() as T)
                } else {
                    Result.failure(Exception(body?.message ?: "Dữ liệu trả về bị rỗng"))
                }
            } else {
                val errorBodyString = response.errorBody()?.string()
                Result.failure(Exception("Lỗi máy chủ: ${response.code()} - $errorBodyString"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // AUTHENTICATION
    suspend fun login(request: LoginRequest): Result<Account> = safeApiCall { api.login(request) }
    suspend fun register(request: RegisterRequest): Result<Account> = safeApiCall { api.register(request) }

    // CATEGORIES
    suspend fun getAllCategories(): Result<List<Category>> = safeApiCall { api.getAllCategories() }

    // PRODUCTS
    suspend fun getProducts(
        cateId: String? = null,
        keyword: String? = null,
        page: Int = 1,
        limit: Int = 6
    ): Result<List<Product>> = safeApiCall {
        api.getProducts(cateId, keyword, page, limit)
    }
    suspend fun getProductDetail(id: String): Result<Product> = safeApiCall { api.getProductDetail(id) }
    suspend fun getProductPopular(): Result<List<Product>> = safeApiCall { api.getProductPopular() }

    // BOOKMARKS
    suspend fun getBookmarks(accountId: String): Result<List<Bookmark>> = safeApiCall { api.getBookmarksByAccount(accountId) }
    suspend fun toggleBookmark(request: BookmarkRequest): Result<Bookmark> = safeApiCall { api.toggleBookmark(request) }

    // CART
    suspend fun getCart(accountId: String): Result<List<CartItem>> = safeApiCall { api.getCartByAccount(accountId) }
    suspend fun addToCart(request: AddToCartRequest): Result<CartItem> = safeApiCall { api.addToCart(request) }
    suspend fun updateCartQuantity(cartItemId: String, quantity: Int): Result<CartItem> = safeApiCall {
        api.updateCartItemQuantity(cartItemId, UpdateCartQtyRequest(quantity))
    }
    suspend fun removeFromCart(cartItemId: String): Result<CartItem> = safeApiCall { api.removeFromCart(cartItemId) }

    // =========================================
    // BILLS (Đã cập nhật)
    // =========================================
    suspend fun placeOrder(request: PlaceOrderRequest): Result<Bill> = safeApiCall { api.placeOrder(request) }

    suspend fun getOrderHistoryByAccount(accountId: String): Result<List<OrderResponse>> = safeApiCall {
        api.getOrderHistoryByAccount(accountId)
    }

    suspend fun getBillDetail(billId: String): Result<OrderResponse> = safeApiCall {
        api.getBillDetail(billId)
    }

    // ADDRESSES
    suspend fun getAddresses(accountId: String): Result<List<Address>> = safeApiCall { api.getAddressesByAccount(accountId) }
    suspend fun createAddress(request: AddressRequest): Result<Address> = safeApiCall { api.createAddress(request) }
    suspend fun updateAddress(addressId: String, request: AddressRequest): Result<Address> = safeApiCall { api.updateAddress(addressId, request) }
    suspend fun deleteAddress(addressId: String): Result<Any> = safeApiCall { api.deleteAddress(addressId) }
}
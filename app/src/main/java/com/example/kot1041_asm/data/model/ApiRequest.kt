// --- CÁC LỚP REQUEST GỬI LÊN SERVER ---

data class RegisterRequest(
    val Email: String,
    val Password: String,
    val FullName: String
)

data class LoginRequest(
    val Email: String,
    val Password: String
)

data class ForgotPasswordRequest(
    val Email: String
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

// Khi đặt hàng, bạn sẽ gửi thông tin Bill và danh sách các Product trong Bill đó
data class PlaceOrderRequest(
    val Email: String,
    val FullName: String,
    val Phone: String,
    val Address: String,
    val Note: String? = "",
    // Đổi thành chữ thường "items" để khớp với "const { ..., items } = req.body" ở BE
    val items: List<CartItemRequest>
)

data class CartItemRequest(
    val ProductID: String,
    val Quantity: Int
    // Đã bỏ UnitPrice vì Backend tự query Product để tính TotalAmount
)
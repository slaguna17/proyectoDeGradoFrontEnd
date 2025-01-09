import com.example.proyectodegrado.data.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.Calendar;
import java.util.Date;

val currentTime = Calendar.getInstance().getTime();

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val full_name: String,
    val date_of_birth: String,
    val phone: String,
    val status: String = "active", // Default value
    val last_access: String = currentTime.toString(),
    val avatar: String? = null
)

data class RegisterResponse(
    val message: String, // Adjust based on your backend response
    val userId: Int? = null // Example of returning created user ID
)

interface ApiService {
    //Valid auth credentials
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    //Get all Users, GET
    @GET("/users")
    suspend fun getAllUsers():List<User>

    //Get specific User
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User

    //Create new User
    @POST("/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>
}
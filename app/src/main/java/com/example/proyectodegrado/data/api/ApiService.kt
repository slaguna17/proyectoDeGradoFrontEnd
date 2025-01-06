import com.example.proyectodegrado.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

data class LoginResponse(val token: String, val userId: Int)

interface ApiService {
    //Valid auth credentials
    @POST("/login")
    suspend fun login(@Body email: String, password: String): Response<LoginResponse>

    //Get all Users, GET
    @GET("/users")
    suspend fun getAllUsers():List<User>

    //Get specific User
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User

    //Create new User
    @POST("/register")
    suspend fun register()
}
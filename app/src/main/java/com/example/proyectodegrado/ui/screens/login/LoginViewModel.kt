import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.model.User
import com.example.proyectodegrado.data.repository.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

//    fun loginUser(username: String, password: String) {
//        viewModelScope.launch {
//            val isLoggedIn = authRepository.login(username, password)
//            if (isLoggedIn) {
//                // Navegar a la siguiente pantalla
//            } else {
//                // Mostrar error de login
//            }
//        }
//    }
}


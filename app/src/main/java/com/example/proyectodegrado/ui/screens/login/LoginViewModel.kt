import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.repository.AuthRepository
import kotlinx.coroutines.launch


class LoginViewModel (private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Result<String>>()
    val loginState: LiveData<Result<String>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _loginState.postValue(Result.success(it.token))
                    } ?: run {
                        _loginState.postValue(Result.failure(Exception("Empty response body")))
                    }
                } else {
                    _loginState.postValue(Result.failure(Exception("Error: ${response.code()}")))
                }
            } catch (e: Exception) {
                _loginState.postValue(Result.failure(e))
            }
        }
    }
}


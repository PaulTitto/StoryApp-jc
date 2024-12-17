import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mosalab.submissionpaai.viewmodel.StoryViewModel

class StoryViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

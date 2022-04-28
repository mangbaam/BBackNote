package mangbaam.bbacknote.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mangbaam.bbacknote.repository.NoteLocalDataSource
import mangbaam.bbacknote.repository.NoteRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val dataSource = NoteLocalDataSource(context)
            val repository = NoteRepository(dataSource)
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Attempt to create unknown ViewModel: $modelClass")
    }
}
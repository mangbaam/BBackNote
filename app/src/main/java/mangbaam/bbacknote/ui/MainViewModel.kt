package mangbaam.bbacknote.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mangbaam.bbacknote.model.NoteEntity
import mangbaam.bbacknote.repository.NoteRepository

class MainViewModel(private val repository: NoteRepository): ViewModel() {
    private var _noteList = MutableLiveData<List<NoteEntity>>()
    val noteList: LiveData<List<NoteEntity>>
        get() = _noteList

    init {
        getAllNotes()
    }

    fun getAllNotes() {
        viewModelScope.launch {
            _noteList.postValue(repository.getAllNotes())
        }
    }

    fun addNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.addNote(note)
        }
    }

    fun lockNote(note: NoteEntity) {
        note.apply { secret = true }
        viewModelScope.launch {
            repository.updateNote(note)
            getAllNotes()
        }
    }

    fun unlockNote(note: NoteEntity) {
        note.apply { secret = false }
        viewModelScope.launch {
            repository.updateNote(note)
            getAllNotes()
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            getAllNotes()
        }
    }

    fun lockAllNotes() {
        viewModelScope.launch {
            _noteList.value?.forEach { note ->
                note.secret = true
                repository.updateNote(note)
            }
            getAllNotes()
        }
    }

    fun unlockAllNotes() {
        viewModelScope.launch {
            _noteList.value?.forEach { note ->
                note.secret = false
                repository.updateNote(note)
            }
            getAllNotes()
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            repository.deleteAllNotes()
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _noteList.postValue(repository.search(query))
        }
    }
}
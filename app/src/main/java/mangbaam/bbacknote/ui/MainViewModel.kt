package mangbaam.bbacknote.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        val lockedNote = note.copy(secret = true)
        viewModelScope.launch {
            repository.updateNote(lockedNote)
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

}
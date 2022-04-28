package mangbaam.bbacknote.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mangbaam.bbacknote.model.NoteEntity
import mangbaam.bbacknote.util.Contants.TAG

class NoteListViewModel : ViewModel() {
    private val _noteList = MutableLiveData<List<NoteEntity>>()
    val noteList: LiveData<List<NoteEntity>>
        get() = _noteList

    init {
        // TODO Room에서 리스트 불러오기
        _noteList.value = emptyList()
    }

    fun addNote(note: NoteEntity) {
        Log.d(TAG, "NoteListViewModel - addNote($note) called")
        _noteList.value = _noteList.value?.plus(note)
    }

}
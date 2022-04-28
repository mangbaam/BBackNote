package mangbaam.bbacknote.repository

import androidx.lifecycle.MutableLiveData
import mangbaam.bbacknote.model.NoteEntity

class NoteRepository(private val dataSource: NoteLocalDataSource) {
    suspend fun addNote(note: NoteEntity) { dataSource.addNote(note) }

    suspend fun updateNote(note: NoteEntity) { dataSource.updateNote(note) }

    suspend fun deleteNote(note: NoteEntity) { dataSource.deleteNote(note) }

    suspend fun getAllNotes() = dataSource.getAllNotes()
}
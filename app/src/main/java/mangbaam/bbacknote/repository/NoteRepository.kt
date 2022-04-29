package mangbaam.bbacknote.repository

import mangbaam.bbacknote.model.NoteEntity

class NoteRepository(private val dataSource: NoteLocalDataSource) {
    suspend fun addNote(note: NoteEntity) { dataSource.addNote(note) }

    suspend fun updateNote(note: NoteEntity) { dataSource.updateNote(note) }

    suspend fun deleteNote(note: NoteEntity) { dataSource.deleteNote(note) }

    suspend fun search(query: String) = dataSource.search("%$query%")

    suspend fun getAllNotes() = dataSource.getAllNotes()

    suspend fun deleteAllNotes() { dataSource.deleteAllNotes() }
}
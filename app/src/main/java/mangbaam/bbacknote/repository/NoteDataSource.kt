package mangbaam.bbacknote.repository

import mangbaam.bbacknote.model.NoteEntity

interface NoteDataSource {
    suspend fun addNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
    suspend fun updateNote(note: NoteEntity)
    suspend fun search(query: String): List<NoteEntity>
    suspend fun getAllNotes(): List<NoteEntity>
    suspend fun deleteAllNotes()
}
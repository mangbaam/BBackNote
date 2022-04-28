package mangbaam.bbacknote.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mangbaam.bbacknote.database.NoteDao
import mangbaam.bbacknote.database.NoteDatabase
import mangbaam.bbacknote.model.NoteEntity

class NoteLocalDataSource(context: Context) : NoteDataSource {

    private val db: NoteDao = NoteDatabase.getInstance(context)?.noteDao()!!

    override suspend fun addNote(note: NoteEntity) = withContext(Dispatchers.IO) { db.insert(note) }

    override suspend fun deleteNote(note: NoteEntity) = withContext(Dispatchers.IO) { db.delete(note) }

    override suspend fun updateNote(note: NoteEntity) = withContext(Dispatchers.IO) { db.update(note) }

    override suspend fun getAllNotes() = withContext(Dispatchers.IO) { db.getAllNote() }
}
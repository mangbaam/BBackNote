package mangbaam.bbacknote.database

import androidx.room.*
import mangbaam.bbacknote.model.NoteEntity

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: NoteEntity)

    @Update
    fun update(note: NoteEntity)

    @Delete
    fun delete(note: NoteEntity)

    // TODO Flow 타입으로 반환하도록 개선
    @Query("SELECT * FROM NoteEntity WHERE NOT secret AND content LIKE :query OR title LIKE :query")
    fun search(query: String): List<NoteEntity>

    @Query("SELECT * FROM NoteEntity")
    fun getAllNote(): List<NoteEntity>

    @Query("DELETE FROM NoteEntity")
    fun deleteAllNotes()
}
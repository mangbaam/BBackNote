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

    @Query("SELECT * FROM NoteEntity")
    fun getAllNote(): List<NoteEntity>

    @Query("DELETE FROM NoteEntity")
    fun deleteAllNotes()
}
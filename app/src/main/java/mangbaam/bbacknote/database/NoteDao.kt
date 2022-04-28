package mangbaam.bbacknote.database

import androidx.room.*
import mangbaam.bbacknote.model.NoteEntity

@Dao
interface NoteDao {
    @Insert
    fun insert(note: NoteEntity)

    @Update
    fun update(note: NoteEntity)

    @Delete
    fun delete(note: NoteEntity)

    @Query("SELECT * FROM NoteEntity")
    fun getAllNote(): List<NoteEntity>
}
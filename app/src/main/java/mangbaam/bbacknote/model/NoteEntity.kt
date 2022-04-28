package mangbaam.bbacknote.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class NoteEntity(
    val content: String,
    val secret: Boolean,
    val noteColor: Int
): Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
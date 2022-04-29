package mangbaam.bbacknote.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class NoteEntity(
    var title: String = "",
    var content: String,
    var secret: Boolean,
    var noteColor: Int
): Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
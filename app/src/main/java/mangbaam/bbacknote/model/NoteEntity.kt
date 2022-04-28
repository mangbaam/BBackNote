package mangbaam.bbacknote.model

import java.io.Serializable

data class NoteEntity(
    val id: Int,
    val content: String,
    val secret: Boolean,
    val noteColor: Int
): Serializable
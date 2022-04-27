package mangbaam.bbacknote.model

import android.graphics.Color

data class NoteEntity(
    val id: Int,
    val content: String,
    val secret: Boolean,
    val noteColor: Color
)

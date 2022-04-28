package mangbaam.bbacknote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mangbaam.bbacknote.database.NoteDao
import mangbaam.bbacknote.database.NoteDatabase

class MainActivity : AppCompatActivity() {
    val noteDB: NoteDao? by lazy {
        NoteDatabase.getInstance(applicationContext)?.noteDao()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
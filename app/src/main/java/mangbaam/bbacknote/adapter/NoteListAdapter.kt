package mangbaam.bbacknote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.ItemNoteBinding
import mangbaam.bbacknote.databinding.ItemSecretNoteBinding
import mangbaam.bbacknote.model.NoteEntity

class NoteListAdapter(val onItemClicked: (NoteEntity, ClickedItem) -> Unit) :
    ListAdapter<NoteEntity, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UNLOCKED_NOTE -> {
                UnlockedNoteViewHolder(
                    ItemNoteBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            LOCKED_NOTE -> {
                LockedNoteViewHolder(
                    ItemSecretNoteBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("$viewType : Invalid Note Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == UNLOCKED_NOTE) {
            (holder as UnlockedNoteViewHolder).bind(currentList[position])
        } else {
            (holder as LockedNoteViewHolder).bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].secret) LOCKED_NOTE else UNLOCKED_NOTE
    }

    inner class UnlockedNoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteEntity) {
            binding.noteContent.text = note.content.trim()
            binding.root.background = ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.rectangle_corner8_white
            )
            binding.menuItemToolBar.title = note.title
            binding.noteContent.setOnClickListener {
                onItemClicked(note, ClickedItem.CONTENT)
            }
            binding.menuItemToolBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.lock_note_item -> {
                        onItemClicked(note, ClickedItem.LOCK_NOTE)
                    }
                    R.id.delete_note_item -> {
                        onItemClicked(note, ClickedItem.DELETE_NOTE)
                    }
                }
                true
            }
        }
    }

    inner class LockedNoteViewHolder(private val binding: ItemSecretNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteEntity) {
            binding.menuItemToolBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.unlock_note_item -> {
                        onItemClicked(note, ClickedItem.UNLOCK_NOTE)
                    }
                    R.id.delete_note_item -> {
                        onItemClicked(note, ClickedItem.DELETE_NOTE)
                    }
                }
                true
            }
            binding.noteContent.setOnClickListener {
                onItemClicked(note, ClickedItem.CONTENT)
            }
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<NoteEntity>() {
            override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem == newItem
            }
        }
        private const val UNLOCKED_NOTE = 0
        private const val LOCKED_NOTE = 1
    }

    enum class ClickedItem {
        CONTENT,
        LOCK_NOTE,
        UNLOCK_NOTE,
        DELETE_NOTE
    }
}
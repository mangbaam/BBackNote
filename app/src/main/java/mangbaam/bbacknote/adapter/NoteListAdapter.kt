package mangbaam.bbacknote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mangbaam.bbacknote.R
import mangbaam.bbacknote.databinding.ItemNoteBinding
import mangbaam.bbacknote.model.NoteEntity

class NoteListAdapter(val onItemClicked: (NoteEntity) -> Unit): ListAdapter<NoteEntity, NoteListAdapter.ListViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ListViewHolder(private val binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteEntity) {
            if (note.secret) {
                // TODO 문장 노출 x
                // TODO "비밀메모 입니다" 혹은 잠금 표시로 노출
                // TODO 클릭 시 암호 입력해야 상세 화면으로 이동
            } else {
                // TODO 일반 메모는 상세 화면에서 비밀 메모로 변환 가능
                // TODO 비밀메모로 변환 시 입력창이 뜨고 입력하면 바뀜
                binding.noteContent.text = note.content.trim()
                binding.root.background = ContextCompat.getDrawable(binding.root.context,R.drawable.rectangle_corner8_white)
                binding.noteContent.setOnClickListener {
                    onItemClicked(note)
                }
            }
        }
    }

    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<NoteEntity>() {
            override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
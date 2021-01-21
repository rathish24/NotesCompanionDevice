package com.dell.notescompaniondevice.cardview


import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dell.notescompaniondevice.R


class DummyNotesAdapter(
    val context: Context,
    private val notesList: ArrayList<Note>
) :
    RecyclerView.Adapter<DummyNotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.notes_card, parent, false)
        return ViewHolder(context,v)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    class ViewHolder(private val context: Context,itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(note: Note) {
            itemView.findViewById<TextView>(R.id.title).text = note.noteTitle
            itemView.findViewById<TextView>(R.id.date).text = note.noteCreated

            Glide.with(context).load(note.noteThumbnail).into(itemView.findViewById<ImageView>(R.id.thumbnail))
            itemView.findViewById<ImageView>(R.id.dots).setOnClickListener {
                showMenu(it)
            }
        }

        private fun showMenu(view: View) {
            // inflate menu
            val popup = PopupMenu(context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.card_menu, popup.menu)
            popup.setOnMenuItemClickListener(MenuItemClickListener(context))
            popup.show()
        }
    }

    class MenuItemClickListener(context: Context) : OnMenuItemClickListener {
        private val context: Context? = context
        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_share -> {
                    Toast.makeText(context, "Share : TODO", Toast.LENGTH_SHORT).show()
                    return true
                }
                R.id.action_duplicate_note -> {
                    Toast.makeText(context, "Duplicate note : TODO", Toast.LENGTH_SHORT).show()
                    return true
                }
                R.id.action_preview -> {
                    Toast.makeText(context, "Preview : TODO", Toast.LENGTH_SHORT).show()
                    return true
                }
                R.id.action_delete -> {
                    Toast.makeText(context, "Delete : TODO", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            return false
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(notesList[position])
    }
}
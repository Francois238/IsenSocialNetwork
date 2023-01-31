package fr.isen.lesnullos.isensocialnetwork.tool

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.lesnullos.isensocialnetwork.R

class CommentaireAdapter (private val listCommentaire: List<String>?) : RecyclerView.Adapter<CommentaireAdapter.ViewHolder>()
{


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewCommentaire: TextView = itemView.findViewById(R.id.idCommentaire)



    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.item_commentaire, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val commentaire = listCommentaire?.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.viewCommentaire
        textView.text = commentaire

    }





    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return listCommentaire?.size ?: 0
    }


}
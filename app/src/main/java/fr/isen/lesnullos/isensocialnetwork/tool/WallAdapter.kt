package fr.isen.lesnullos.isensocialnetwork.tool

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.lesnullos.isensocialnetwork.R
import fr.isen.lesnullos.isensocialnetwork.model.Post

class WallAdapter (private val listPost: List<Post>) : RecyclerView.Adapter<WallAdapter.ViewHolder>()
{
    // Define the listener interface
    interface OnItemClickListener {
        fun onItemClick(itemView: View?, position: Int)
    }

    // Define listener member variable
    private lateinit var listener: OnItemClickListener

    // Define the method that allows the parent activity or fragment to define the listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomPost: TextView = itemView.findViewById(R.id.nomPost)
        val iconePost : ImageView = itemView.findViewById(R.id.iconePost)
        val iconeLike : ImageView = itemView.findViewById(R.id.iconePostLike)
        val nbLike : TextView = itemView.findViewById(R.id.nbLikeList)



        init {
            // Setup the click listener
            itemView.setOnClickListener {
                // Triggers click upwards to the adapter on click
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position)
                }
            }
        }
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.item_post, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val post: Post = listPost[position]
        // Set item views based on your views and data model
        val textView = viewHolder.nomPost
        textView.text = post.title

        val imageView = viewHolder.iconePost

        var image = post.image
        if (image.isNullOrEmpty()){
            image = "a"
        }

        Picasso.get().load(image)
            .error(R.drawable.capture_d_cran_2023_01_31___11_33_39)
            .centerCrop()
            .fit()
            .into(imageView)

        val nbLike = viewHolder.nbLike
        if(post.like == null){
            nbLike.text = "0"
        }
        else{
            nbLike.text = post.like?.size.toString()
        }

        val iconeLike = viewHolder.iconeLike

        iconeLike.setImageResource(R.drawable.pouce)


    }





    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return listPost.size
    }


}
package fr.isen.lesnullos.isensocialnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityDetailBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import fr.isen.lesnullos.isensocialnetwork.model.PostTransmis
import fr.isen.lesnullos.isensocialnetwork.tool.CommentaireAdapter
import fr.isen.lesnullos.isensocialnetwork.tool.ObjectWrapperForBinder
import fr.isen.lesnullos.isensocialnetwork.tool.WallAdapter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding

    private lateinit var post : PostTransmis

    var listPost = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.post  =
            (intent.extras!!.getBinder("Post") as ObjectWrapperForBinder?)!!.data as PostTransmis

        binding.detailNomPost.text = post.post.nom

        displayCommentaire()



        Firebase.database.getReference("post").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listPost = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val recu = postSnapshot.getValue<Post>()
                    if (recu != null) {
                        println("bdd : ${recu.nom}")
                        listPost.add(recu)
                    }
                }

                listPost.reverse()

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("TAG", "Failed to read value.", error.toException())
            }
        })

        binding.boutonCht.setOnClickListener {

            val commentaire = binding.inputCommentaire.text.toString()

            val listCommentaire = post.post.commentaire

            if(listCommentaire.isNullOrEmpty()){

                val newListCommentaire = ArrayList<String>()

                newListCommentaire.add(commentaire)

                post.post.commentaire = newListCommentaire
            }

            else{
                post.post.commentaire?.add(commentaire)
            }


            this.listPost[this.post.position] = this.post.post

            this.listPost.reverse()

            val database = Firebase.database
            val myRef = database.getReference("post")


            myRef.setValue(this.listPost)
        }


    }

    private fun displayCommentaire(){

        val adapter = CommentaireAdapter(post.post.commentaire)

        val viewPost =findViewById<View>(R.id.listeCommentaire) as RecyclerView

        // Faire le lien entre l'adapter et le recycler view
        viewPost.adapter = adapter
        // Affichage de la liste
        viewPost.layoutManager = LinearLayoutManager(this)
    }
}
package fr.isen.lesnullos.isensocialnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityDetailBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import fr.isen.lesnullos.isensocialnetwork.model.PostTransmis
import fr.isen.lesnullos.isensocialnetwork.tool.ObjectWrapperForBinder

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
            this.post.post.nom = "un autre test"

            binding.detailNomPost.text = this.post.post.nom

            this.listPost[this.post.position] = this.post.post

            this.listPost.reverse()

            val database = Firebase.database
            val myRef = database.getReference("post")


            myRef.setValue(this.listPost)
        }


    }
}
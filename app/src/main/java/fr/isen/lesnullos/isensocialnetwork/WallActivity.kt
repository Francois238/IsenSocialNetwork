package fr.isen.lesnullos.isensocialnetwork

import android.content.Intent
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
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityWallBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import fr.isen.lesnullos.isensocialnetwork.model.PostTransmis
import fr.isen.lesnullos.isensocialnetwork.tool.ObjectWrapperForBinder
import fr.isen.lesnullos.isensocialnetwork.tool.WallAdapter

class WallActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWallBinding

    private var listPost = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallBinding.inflate(layoutInflater)

        setContentView(binding.root)

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

                displayList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("TAG", "Failed to read value.", error.toException())
            }
        })


    }

    private fun displayList(){
        // creation de l'adapter
        val adapter = WallAdapter(listPost)

        val viewPost =findViewById<View>(R.id.listePost) as RecyclerView

        // Faire le lien entre l'adapter et le recycler view
        viewPost.adapter = adapter
        // Affichage de la liste
        viewPost.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickListener(object : WallAdapter.OnItemClickListener {
            override fun onItemClick(itemView: View?, position: Int) {
                val post = listPost[position]

                val postTransmis = PostTransmis(post, position)

                val bundle = Bundle()
                bundle.putBinder("Post", ObjectWrapperForBinder(postTransmis))

                startActivity(Intent(this@WallActivity, DetailActivity::class.java).putExtras(bundle)) //on passe l objet a la nouvelle activite
            }
        })

    }
}
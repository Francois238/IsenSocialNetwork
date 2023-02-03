package fr.isen.lesnullos.isensocialnetwork

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityProfileBinding
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityWallBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import fr.isen.lesnullos.isensocialnetwork.model.User
import fr.isen.lesnullos.isensocialnetwork.tool.WallAdapter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding

    private val database = Firebase.database

    private lateinit var auth: FirebaseAuth

    private var listPost = ArrayList<Post>()

    private var listUser = ArrayList<User>()

    private lateinit var profil : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)


        Firebase.database.getReference("post").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listPost = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val recu = postSnapshot.getValue<Post>()
                    if (recu != null) {
                        listPost.add(recu)
                    }
                }

                listPost.reverse()

                choosePost()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("TAG", "Failed to read value.", error.toException())
            }
        })

    }

    private fun choosePost(){
        val user = auth.currentUser
        user?.uid
        val sharedPreferences = getSharedPreferences("user_id", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("user_id", user?.uid)

        Firebase.database.getReference("user").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listUser = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val recu = postSnapshot.getValue<User>()
                    if (recu != null) {
                        listUser.add(recu)


                    }
                }


                for(value in listUser){
                    if (value.id == id){
                        profil = value
                    }
                }

                val listMyPost = listPost.filter { it.namePerson == profil.name }

                displayList(listMyPost)



            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("TAG", "Failed to read value.", error.toException())
            }
        })


    }

    private fun displayList(listMyPost : List<Post>){
        // creation de l'adapter
        val adapter = WallAdapter(listMyPost)

        val viewPost =findViewById<View>(R.id.yourPostsList) as RecyclerView

        // Faire le lien entre l'adapter et le recycler view
        viewPost.adapter = adapter
        // Affichage de la liste
        viewPost.layoutManager = LinearLayoutManager(this)
    }
}
package fr.isen.lesnullos.isensocialnetwork

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityDetailBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import fr.isen.lesnullos.isensocialnetwork.model.PostTransmis
import fr.isen.lesnullos.isensocialnetwork.tool.CommentaireAdapter
import fr.isen.lesnullos.isensocialnetwork.tool.ObjectWrapperForBinder
import fr.isen.lesnullos.isensocialnetwork.tool.WallAdapter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding

    private lateinit var post : PostTransmis

    private var nbLike =0

    var listPost = ArrayList<Post>()

    private lateinit var auth: FirebaseAuth

    private var user : FirebaseUser? = null

    private lateinit var sharedPreferences: SharedPreferences

    private var id : String? = null
    private var like = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        this.post  =
            (intent.extras!!.getBinder("Post") as ObjectWrapperForBinder?)!!.data as PostTransmis

        user = auth.currentUser //recupere l'utilisateur connecté
        user?.uid
        sharedPreferences = getSharedPreferences("user_id", Context.MODE_PRIVATE)
        id = sharedPreferences.getString("user_id", user?.uid)

        binding.detailBodyPost.text = post.post.body
        binding.nomPoster.text = post.post.namePerson
        binding.titlePostDetail.text = post.post.title

        val imageView = findViewById<ImageView>(R.id.imagePost)

        if (post.post.image != null) {
            Picasso.get().load(post.post.image)
                .error(R.drawable.capture_d_cran_2023_01_31___11_33_39)
                .centerCrop()
                .fit()
                .into(imageView)
        }

        displayCommentaire()



        Firebase.database.getReference("post").addValueEventListener(object : //recupere la liste des posts
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listPost = ArrayList()

                for (postSnapshot in snapshot.children) {
                    val recu = postSnapshot.getValue<Post>()
                    if (recu != null) {
                        println("bdd : ${recu.body}")
                        listPost.add(recu)
                    }
                }

                listPost.reverse()

                preferenceUser()

                gestionLike()

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("TAG", "Failed to read value.", error.toException())
            }
        })

        binding.boutonCht.setOnClickListener {//Ajout de commentaire

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

            this.displayCommentaire()


            this.listPost[this.post.position] = this.post.post

            this.listPost.reverse()

            val database = Firebase.database
            val myRef = database.getReference("post")


            myRef.setValue(this.listPost)
        }

        binding.like.setOnClickListener { //ajout d un like

            if (!like){ //si le  user n'a pas deja liké

                val listLike = post.post.like //recupere la liste des likes

                if(listLike.isNullOrEmpty()){

                    val newListLike = ArrayList<String>()

                    if (id != null) {
                        newListLike.add(id!!)
                    }

                    post.post.like = newListLike
                }

                else{
                    if (id != null) {
                        post.post.like?.add(id!!)
                    }
                }

                this.gestionLike()

                this.listPost[this.post.position] = this.post.post //mise a jour de la liste des posts

                this.listPost.reverse()

                val database = Firebase.database
                val myRef = database.getReference("post") //envoie la liste des posts a la bdd

                myRef.setValue(this.listPost)


            }

            else{

                val listLike = post.post.like //recupere la liste des likes

                if(!listLike.isNullOrEmpty() ){
                    if (id != null) {
                        post.post.like?.remove(id!!)
                    }
                }

                this.gestionLike()

                this.listPost[this.post.position] = this.post.post //mise a jour de la liste des posts

                this.listPost.reverse()

                val database = Firebase.database
                val myRef = database.getReference("post") //envoie la liste des posts a la bdd

                myRef.setValue(this.listPost)

                like = false

                preferenceUser()
            }

        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menu != null) {
            super.onCreateOptionsMenu(menu)
        }
        menuInflater.inflate(R.menu.mon_menu, menu)


        val menuItem = menu?.findItem(R.id.menu_item_image)?.actionView


        return true
    }


    private fun displayCommentaire(){

        val adapter = CommentaireAdapter(post.post.commentaire)

        val viewPost =findViewById<View>(R.id.listeCommentaire) as RecyclerView

        // Faire le lien entre l'adapter et le recycler view
        viewPost.adapter = adapter
        // Affichage de la liste
        viewPost.layoutManager = LinearLayoutManager(this)
    }

    private fun gestionLike(){

        nbLike = post.post.like?.size ?: 0

        binding.nbLike.text = nbLike.toString()

    }

    private fun preferenceUser(){

        val listLike = post.post.like

        if(listLike.isNullOrEmpty()){ //Si aucun like pour le post

            binding.like.setImageResource(R.drawable.pouce)
        }

        else{
            if (id != null) {
                if(listLike.contains(id)){
                    like = true
                    binding.like.setImageResource(R.drawable.pouce_bleu) //si le user a deja like le post
                }
                else{
                    binding.like.setImageResource(R.drawable.pouce) //si le user n'a pas like le post
                }
            }
        }
    }
}
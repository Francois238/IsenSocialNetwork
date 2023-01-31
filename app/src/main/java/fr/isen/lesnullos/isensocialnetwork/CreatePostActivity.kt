package fr.isen.lesnullos.isensocialnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityCreatePostBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.publishButtonPost.setOnClickListener {
            //je veux recuperer le champ texte
            val text = binding.texteInputPost.text

            val database = Firebase.database
            val myRef = database.getReference("post")


            myRef.push().setValue(Post(text.toString()))


        }

    }

}
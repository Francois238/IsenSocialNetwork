package fr.isen.lesnullos.isensocialnetwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityEditProfileBinding
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar?.title = "Édition du profil"

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.modification.setOnClickListener {
            Toast.makeText(applicationContext, "Profil mis à jour", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@EditProfileActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
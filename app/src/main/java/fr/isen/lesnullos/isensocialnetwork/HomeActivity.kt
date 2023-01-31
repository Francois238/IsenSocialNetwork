package fr.isen.lesnullos.isensocialnetwork

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            connectionUser()


        }

        binding.btnRegisterAccont.setOnClickListener {
            Toast.makeText(this, "Click Register", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@HomeActivity, ProfileFormActivity::class.java)
            startActivity(intent)
        }

        binding.forgetpassword.setOnClickListener {
            Toast.makeText(this, "Click Register", Toast.LENGTH_SHORT).show()

        }


    }


    private fun connectionUser(){

        val emailEditText = findViewById<TextInputEditText>(R.id.mailaddress)
        val passwordEditText = findViewById<TextInputEditText>(R.id.motdepasse)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.uid
                    val sharedPreferences = this.getSharedPreferences("user_id", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_id", user?.uid)
                    editor.apply()
                    Log.d(ContentValues.TAG, "L'utilisateur est connecté: $user")
                    Toast.makeText(this, "CLick Login", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@HomeActivity, WallActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e(ContentValues.TAG, "Erreur de connexion: ${task.exception}")
                    // Affichez un message d'erreur en cas d'échec
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    Log.e(ContentValues.TAG, "Code d'erreur: $errorCode")
                }
            }

    }

}
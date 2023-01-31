package fr.isen.lesnullos.isensocialnetwork

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            connectionUser()


        }

        binding.btnRegisterAccont.setOnClickListener {
            Toast.makeText(this, "Click Register", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@HomeActivity, ProfileFormActivity::class.java)
            startActivity(intent);
        }

        binding.forgetpassword.setOnClickListener {
            Toast.makeText(this, "Click Register", Toast.LENGTH_SHORT).show()

        }


    }


    private fun connectionUser(){

        val emailEditText = findViewById<EditText>(R.id.adressemail)
        val passwordEditText = findViewById<EditText>(R.id.password)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Envoyez les informations d'identification de l'utilisateur à Firebase pour la vérification
        val auth = FirebaseAuth.getInstance(FirebaseApp.getInstance("firebase"))
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    //val id = user?.uid
                    val sharedPreferences = this.getSharedPreferences("user_id", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_id", user?.uid)
                    editor.apply()
                    Log.d(ContentValues.TAG, "L'utilisateur est connecté: $user")
                    Toast.makeText(this, "CLick Login", Toast.LENGTH_SHORT).show();
                   //val intent = Intent(this@HomeActivity, MainActivity::class.java)
                   //startActivity(intent);
                } else {
                    Log.e(ContentValues.TAG, "Erreur de connexion: ${task.exception}")
                    // Affichez un message d'erreur en cas d'échec
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    Log.e(ContentValues.TAG, "Code d'erreur: $errorCode")
                }
            }
    }

}
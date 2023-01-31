package fr.isen.lesnullos.isensocialnetwork

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            connectionUser()

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
                } else {
                    Log.e(ContentValues.TAG, "Erreur de connexion: ${task.exception}")
                    // Affichez un message d'erreur en cas d'échec
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    Log.e(ContentValues.TAG, "Code d'erreur: $errorCode")
                }
            }
    }

}
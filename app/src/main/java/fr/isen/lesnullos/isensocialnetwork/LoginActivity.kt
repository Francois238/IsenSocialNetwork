package fr.isen.lesnullos.isensocialnetwork

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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

/*
        //lecture bdd

        Firebase.database.getReference("message").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<String>()
                Log.d("TAG", "Value is: $value")
                findViewById<TextView>(R.id.textView).text = value
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("TAG", "Failed to read value.", error.toException())
            }
        })

        //ecriture bdd
        findViewById<Button>(R.id.button).setOnClickListener{
            val database = Firebase.database
            val myRef = database.getReference("message")
            myRef.setValue("Hello, World!")
        }*/
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
package fr.isen.lesnullos.isensocialnetwork

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityProfileFormBinding
import fr.isen.lesnullos.isensocialnetwork.model.FormInscription

class ProfileFormActivity : AppCompatActivity() {
    var name = ""
    var email = ""
    var passw = ""
    var passc = ""
    var birth = ""
    var sexe = ""

    private lateinit var binding: ActivityProfileFormBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_form)

        binding = ActivityProfileFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignIn.setOnClickListener{
            editText()
        }
    }

    private fun editText (){
        val editName = findViewById<TextView>(R.id.editNameSign)
        val editEmail = findViewById<TextView>(R.id.editEmailSign)
        val editPassw = findViewById<TextView>(R.id.editPasswordSign)
        val editPassC = findViewById<TextView>(R.id.editConfirmSign)
        val editBirth = findViewById<TextView>(R.id.editBirthSign)
        val editSexe = findViewById<TextView>(R.id.editSexeSign)

        name = editName.text.toString()
        email = editEmail.text.toString()
        passw = editPassw.text.toString()
        passc = editPassC.text.toString()
        birth = editBirth.text.toString()
        sexe = editSexe.text.toString()

        var error = false
        val message = handleErrorMessage()

        if (message.isNotEmpty()) {
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
            error = true
        }

        if (passw != passc){
            Toast.makeText(this,"Les mots de passes sont différents", Toast.LENGTH_SHORT).show()
            error = true
        }

        if (!error){
            auth.createUserWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        if(user != null) {
                            userID(user)

                            val intent = Intent(this@ProfileFormActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun handleErrorMessage() :String {
        val editName = findViewById<TextView>(R.id.editNameSign)
        val editEmail = findViewById<TextView>(R.id.editEmailSign)
        val editPassw = findViewById<TextView>(R.id.editPasswordSign)
        val editPassC = findViewById<TextView>(R.id.editConfirmSign)
        val editBirth = findViewById<TextView>(R.id.editBirthSign)
        val editSexe = findViewById<TextView>(R.id.editSexeSign)

        name = editName.text.toString()
        email = editEmail.text.toString()
        passw = editPassw.text.toString()
        passc = editPassC.text.toString()
        birth = editBirth.text.toString()
        sexe = editSexe.text.toString()

        var errorMessage = ""

        when {
            name.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Nom"
            }
            email.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Email"
            }
            passw.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Mot de Passe"
            }
            passc.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Confirmation"
            }
            birth.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Date de Naissance"
            }
            sexe.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Sexe"
            }
        }

        return errorMessage
    }

    private fun userID(user:FirebaseUser){
        val newUser = FormInscription(user.uid,name,birth,sexe)
        val database = Firebase.database
        val myRef = database.getReference("user")

        myRef.push().setValue(newUser)
    }
}
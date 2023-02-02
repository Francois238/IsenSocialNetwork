package fr.isen.lesnullos.isensocialnetwork

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityInscriptionBinding
import fr.isen.lesnullos.isensocialnetwork.model.FormInscription
import fr.isen.lesnullos.isensocialnetwork.model.Post
import java.io.IOException
import java.util.*

class ProfileFormActivity : AppCompatActivity() {
    var name = ""
    private var email = ""
    private var passw = ""
    private var passc = ""
    private var birth = ""
    private var sexe = ""

    private lateinit var binding: ActivityInscriptionBinding
    private lateinit var auth: FirebaseAuth

    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST = 22

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var image = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        binding = ActivityInscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignIn.setOnClickListener{
            uploadImage()
        }

        binding.buttonChooseImage.setOnClickListener{
            selectImage()
        }
    }

    private fun editText (){
        val editName = findViewById<TextView>(R.id.identity)
        val editEmail = findViewById<TextView>(R.id.adressemail)
        val editPassw = findViewById<TextView>(R.id.password)
        val editPassC = findViewById<TextView>(R.id.confpassword)
        val editBirth = findViewById<TextView>(R.id.birthdate)
        val editSexe = findViewById<TextView>(R.id.textsexe)

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

        val editName = findViewById<TextView>(R.id.identity)
        val editEmail = findViewById<TextView>(R.id.adressemail)
        val editPassw = findViewById<TextView>(R.id.password)
        val editPassC = findViewById<TextView>(R.id.confpassword)
        val editBirth = findViewById<TextView>(R.id.birthdate)
        val editSexe = findViewById<TextView>(R.id.textsexe)

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
        val newUser = FormInscription(user.uid,name,birth,sexe, image)
        val database = Firebase.database
        val myRef = database.getReference("user")

        myRef.push().setValue(newUser)

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }



    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )

                binding.iconeImageProfil.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            val ref = storageReference
                ?.child(
                    "images/"
                            + UUID.randomUUID().toString()
                )
            if (ref != null) {
                ref.putFile(filePath!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@ProfileFormActivity,
                                "Profil enregistre!!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                    .addOnFailureListener { e -> // Error, Image not uploaded
                        progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@ProfileFormActivity,
                                "Failed " + e.message,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = (100.0
                                * taskSnapshot.bytesTransferred
                                / taskSnapshot.totalByteCount)
                        progressDialog.setMessage(
                            "Uploaded "
                                    + progress.toInt() + "%"
                        )
                    }
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            val url = uri.toString()
                            println("url acces : $url")
                            image = url

                            editText() //post to firebase

                        }
                    }
            }
        }
    }
}
package fr.isen.lesnullos.isensocialnetwork

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityEditProfileBinding
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityProfileBinding
import fr.isen.lesnullos.isensocialnetwork.model.FormInscription
import fr.isen.lesnullos.isensocialnetwork.model.Post
import fr.isen.lesnullos.isensocialnetwork.model.User
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class EditProfileActivity : AppCompatActivity() {

    private var filePath: Uri? = null

    private var listUser : ArrayList<User> = ArrayList()

    private val PICK_IMAGE_REQUEST = 22

    private lateinit var auth: FirebaseAuth

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private val database = Firebase.database
    private val myRef = database.getReference("user")

    private var image = ""

    private var name = ""

    private var sexe = ""

    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()

        supportActionBar?.title = "Édition du profil"

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference


        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.slectionImage.setOnClickListener {
            this.selectImage()
        }

        binding.modification.setOnClickListener {
            this.uploadImage()
        }
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

                binding.imageProfilSelection.setImageBitmap(bitmap)
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
                                this@EditProfileActivity,
                                "Profil enregistre!!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                    .addOnFailureListener { e -> // Error, Image not uploaded
                        progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@EditProfileActivity,
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menu != null) {
            super.onCreateOptionsMenu(menu)
        }
        menuInflater.inflate(R.menu.mon_menu, menu)


        val menuItem = menu?.findItem(R.id.menu_item_image)?.actionView


        return true
    }


    private fun editText (){

        name = binding.nom.text.toString()
        sexe = binding.sexe.text.toString()

        var error = false
        val message = handleErrorMessage()

        if (message.isNotEmpty()) {
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
            error = true
        }

        if (!error){

            userID()
        }

    }

    private fun handleErrorMessage() :String {

        name = binding.nom.text.toString()
        sexe = binding.sexe.text.toString()

        var errorMessage = ""

        when {
            name.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Nom"
            }
            sexe.isEmpty() -> {
                errorMessage = "Veuilliez renseigner le champs Sexe"
            }
        }

        return errorMessage
    }

    private fun userID() {

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

                    val user = auth.currentUser
                    user?.uid
                    val sharedPreferences = getSharedPreferences("user_id", Context.MODE_PRIVATE)
                    val id = sharedPreferences.getString("user_id", user?.uid)


                    for (value in listUser) {
                        if (value.id == id) {
                            value.name = name
                            value.sexe = sexe
                            value.photo = image
                        }
                    }

                    myRef.setValue(listUser)

                    val intent = Intent(this@EditProfileActivity, WallActivity::class.java)
                    startActivity(intent) //redirection vers la page d'accueil
                }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

            })
    }
}
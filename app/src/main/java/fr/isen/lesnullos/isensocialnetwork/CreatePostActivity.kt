package fr.isen.lesnullos.isensocialnetwork

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityCreatePostBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import fr.isen.lesnullos.isensocialnetwork.model.User
import java.io.IOException
import java.util.*


class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private val database = Firebase.database
    private val myRef = database.getReference("user")
    private lateinit var auth: FirebaseAuth

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST = 22
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<ImageView>(R.id.accueil).setOnClickListener {
            val intent = Intent(this@CreatePostActivity, WallActivity::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.editProfile).setOnClickListener {
            val intent = Intent(this@CreatePostActivity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val user = auth.currentUser
        user?.uid
        val sharedPreferences = getSharedPreferences("user_id", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("user_id", user?.uid)

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (postSnapshot in snapshot.children) {
                    val value = postSnapshot.getValue<User>()
                    if ((value != null) && (value.id == id)) {
                        val nameuser = value.name
                        var photo = value.photo
                        binding.nameUserPost.text = nameuser

                        val imageView = findViewById<ImageView>(R.id.imageProfilPost)

                        if (photo == "") {
                            photo ="a" // pour éviter l'erreur
                        }
                        Picasso.get().load(photo)
                            .error(R.drawable.capture_d_cran_2023_01_31___11_33_39)
                            .centerCrop()
                            .fit()
                            .into(imageView)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        binding.publishButtonPost.setOnClickListener {
            uploadImage()
            Toast.makeText( applicationContext, "Poste publié", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WallActivity::class.java)
            startActivity(intent)
        }

        binding.quittePost.setOnClickListener {
            val intent = Intent(this, WallActivity::class.java)
            startActivity(intent)
        }


        binding.imagePicturePost.setOnClickListener {
            selectImage()
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
                binding.imageViewPost.setImageBitmap(bitmap)
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
                                this@CreatePostActivity,
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                    .addOnFailureListener { e -> // Error, Image not uploaded
                        progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@CreatePostActivity,
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

                            val body = binding.texteInputPost.text.toString()

                            val title = binding.titlePost.text.toString()

                            val database = Firebase.database
                            val myRef = database.getReference("post")

                            myRef.push().setValue(Post(binding.nameUserPost.text.toString(),title, body, url))
                        }
                    }
            }
        }
    }
}
package fr.isen.lesnullos.isensocialnetwork

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityCreatePostBinding
import fr.isen.lesnullos.isensocialnetwork.model.User
import java.io.File
import java.io.FileOutputStream
import com.google.firebase.auth.FirebaseAuth
import android.app.ProgressDialog
import java.io.IOException
import java.util.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.isen.lesnullos.isensocialnetwork.model.Post


class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private val database = Firebase.database
    private val myRef = database.getReference("user")
    private lateinit var auth: FirebaseAuth

    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST = 22
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance();
        storageReference = storage!!.reference;

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
                        binding.nameUserPost.text = nameuser
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

        binding.imageDocPost.setOnClickListener {
            //one day maybe...
            Toast.makeText( applicationContext, "Work in progress", Toast.LENGTH_SHORT).show()
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

                            val text = binding.texteInputPost.text.toString()

                            val database = Firebase.database
                            val myRef = database.getReference("post")

                            myRef.push().setValue(Post(binding.nameUserPost.text.toString(), text, url))
                        }
                    }
            }
        }
    }
}
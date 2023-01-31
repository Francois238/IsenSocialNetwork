package fr.isen.lesnullos.isensocialnetwork

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityCreatePostBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import java.io.IOException
import java.util.*


class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var imageViewPost : ImageView
    var errorEditText: TextInputEditText? = null
    val requestSelectedImage = 1

    // instance for firebase storage and StorageReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    // Uri indicates, where the image will be picked from
    private var filePath: Uri? = null

    // request code
    private val PICK_IMAGE_REQUEST = 22
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage!!.reference;

        //recuperer le nom et la photo du user qui post

        //recuperer le texte et le title user
        val text = binding.texteInputPost.text.toString()
        val title = binding.titlePost.text.toString()

        binding.publishButtonPost.setOnClickListener {
           // publishPost(text,title)
            //Toast.makeText( applicationContext, "Post published", Toast.LENGTH_SHORT).show()

            uploadImage()
        }

        //bouton quitter -> retour sur fil
        binding.quittePost.setOnClickListener {
            Toast.makeText( applicationContext, "Home", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WallActivity::class.java)
            startActivity(intent)
        }

        binding.imageDocPost.setOnClickListener {
            //one day maybe...
        }


        binding.imagePicturePost.setOnClickListener {
            selectImage()
        }

    }


    // Select Image method
    private fun selectImage() {

        // Defining Implicit Intent to mobile gallery
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

    // Override onActivityResult method
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

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.data != null) {

            // Get the Uri of data
            filePath = data.data
            try {

                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )
                binding.imageViewPost.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }


    // UploadImage method
    private fun uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference
                ?.child(
                    "images/"
                            + UUID.randomUUID().toString()
                )


            // adding listeners on upload
            // or failure of image
            if (ref != null) {
                ref.putFile(filePath!!)
                    .addOnSuccessListener {  // Image uploaded successfully
                        // Dismiss dialog

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

                        // Progress Listener for loading
                        // percentage on the dialog box
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

                            myRef.push().setValue(Post(text,url))
                            }
                        }
                    }

            }

    }
}
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


class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private val requestSelectedImage = 1
    private val database = Firebase.database
    private val myRef = database.getReference("user")
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recuperer le nom et la photo du user qui post
        val user = auth.currentUser
        user?.uid
        val sharedPreferences = getSharedPreferences("user_id", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("user_id", user?.uid)

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (postSnapshot in snapshot.children) {
                    val value = postSnapshot.getValue<User>()
                    if ((value != null) && (value.id == id)) {
                        println("id: ${value.id}")
                        println("name : ${value.name}")
                        val nameuser = value.name
                        binding.nameUserPost.text = nameuser
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        //recuperer le texte et le title user
        val text = binding.texteInputPost.text.toString()
        val title = binding.titlePost.text.toString()

        binding.publishButtonPost.setOnClickListener {
            publishPost(text,title)
            Toast.makeText( applicationContext, "Poste publié", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WallActivity::class.java)
            startActivity(intent)
        }

        //bouton quitter -> retour sur fil
        binding.quittePost.setOnClickListener {
            val intent = Intent(this, WallActivity::class.java)
            startActivity(intent)
        }

        binding.imageDocPost.setOnClickListener {
            //one day maybe...
            Toast.makeText( applicationContext, "Work in progress", Toast.LENGTH_SHORT).show()
        }


        binding.imagePicturePost.setOnClickListener {
            dispatchSelectPictureIntent()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestSelectedImage && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data

            if (selectedImageUri != null) {
                saveSelectedImageToFile(selectedImageUri)
                displaySavedImage()
            }
        }
    }
    private fun dispatchSelectPictureIntent() {
        val selectPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (selectPictureIntent.resolveActivity(packageManager) != null) {
            selectPictureIntent.type="image/*"
            startActivityForResult(selectPictureIntent, requestSelectedImage)
        }
    }
    private fun saveSelectedImageToFile(selectedImageUri: Uri) {
        val inputStream = contentResolver.openInputStream(selectedImageUri)
        val imageBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, "selected_image.jpg")
        val outputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }
    private fun displaySavedImage() {
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, "selected_image.jpg")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.imageViewPost.setImageBitmap(bitmap)
        }
    }

    private fun publishPost(text:String, title: String) {
        val database = Firebase.database
        val myRef = database.getReference("postCreate")
        data class StringData(val string1: String, val string2: String)
        val stringData = StringData(text,title)

        myRef.push().setValue(stringData)
    }

}
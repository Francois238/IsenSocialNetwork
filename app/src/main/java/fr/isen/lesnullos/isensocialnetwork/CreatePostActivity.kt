package fr.isen.lesnullos.isensocialnetwork

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.lesnullos.isensocialnetwork.databinding.ActivityCreatePostBinding
import fr.isen.lesnullos.isensocialnetwork.model.Post
import java.io.File
import java.io.FileOutputStream

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var imageViewPost : ImageView
    var errorEditText: TextInputEditText? = null
    val requestSelectedImage = 1
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recuperer le nom et la photo du user qui post

        //recuperer le texte et le title user
        val text = binding.texteInputPost.text.toString()
        val title = binding.titlePost.text.toString()

        binding.publishButtonPost.setOnClickListener {
            publishPost(text,title)
            Toast.makeText( applicationContext, "Post published", Toast.LENGTH_SHORT).show()
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
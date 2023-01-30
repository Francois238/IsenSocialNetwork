package fr.isen.lesnullos.isensocialnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        findViewById<Button>(R.id.button).setOnClickListener{
            val database = Firebase.database
            val myRef = database.getReference("message")
            myRef.setValue("Hello, World!")
        }
    }
}
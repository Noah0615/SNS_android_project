package com.example.week10_2

import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.Serializable

data class ReservedMovie(
    val _id: Int?,
    val name: String?,
    val poster_image: String?,
    val director: String?,
    val synopsis: String?,
    val reserved_time: String?
) : Serializable

class ReservedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserved)

        val viewButton = findViewById<Button>(R.id.viewbutton)
        val posterImageView = findViewById<ImageView>(R.id.posterImageView)
        val input1 = findViewById<TextView>(R.id.input1)
        val input2 = findViewById<TextView>(R.id.input2)
        val input3 = findViewById<TextView>(R.id.input3)
        val input4 = findViewById<TextView>(R.id.input4)

        processIntent(intent, posterImageView, input1, input2, input3, input4)

        viewButton.setOnClickListener {
            finish()
        }
    }

    private fun processIntent(intent: Intent?, posterImageView: ImageView, input1: TextView, input2: TextView, input3: TextView, input4: TextView) {
        val movies = intent?.getSerializableExtra("movies") as ArrayList<ReservedMovie>?
        val movie = movies?.get(0)

        if (movie != null) {
            posterImageView.setImageURI(Uri.parse(movie.poster_image))
            input1.text = movie.name
            input2.text = movie.director
            input3.text = movie.synopsis
            input4.text = movie.reserved_time
        } else {
            input1.text = "null"
            input2.text = "null"
            input3.text = "null"
            input4.text = "null"
        }
    }
}

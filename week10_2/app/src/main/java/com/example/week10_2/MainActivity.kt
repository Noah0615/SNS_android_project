package com.example.week10_2

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.week10_2.R
import com.example.week10_2.ReservedActivity

import java.io.*

class MainActivity : AppCompatActivity() {
    val databaseName = "movie0"
    var database: SQLiteDatabase? = null
    val tableName = "movie_reserved"
    lateinit var saveButton: Button
    lateinit var viewButton: Button
    lateinit var movieTitleTextView: TextView
    lateinit var movieTimeTextView: TextView
    lateinit var movieCastTextView: TextView
    lateinit var input4: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveButton = findViewById(R.id.savebutton)
        viewButton = findViewById(R.id.viewbutton)
        movieTitleTextView = findViewById(R.id.movieTitleTextView)
        movieTimeTextView = findViewById(R.id.movieTimeTextView)
        movieCastTextView = findViewById(R.id.movieCastTextView)
        input4 = findViewById(R.id.input4)

        createDatabase()
        createTable()

        saveButton.setOnClickListener {
            saveMovie()
        }

        viewButton.setOnClickListener {
            loadMovie()
        }
        // TODO : 버튼 이벤트 처리

    }

    fun saveMovie(){
        val posterImageUri = savePosterToFile(R.drawable.dd)

        val name = movieTitleTextView.text.toString()
        val director = movieTimeTextView.text.toString()
        val synopsis = movieCastTextView.text.toString()
        val reserved_time = input4.text.toString()
        // TODO : record에 저장할 데이터를 위젯으로부터 가져와 데이터베이스에 추가
        addData(name,posterImageUri.toString(),director,synopsis,reserved_time)

    }

    fun savePosterToFile(drawable:Int):Uri{
        val drawable = ContextCompat.getDrawable(applicationContext,drawable)
        val bitmap = (drawable as BitmapDrawable).bitmap

        val wrapper = ContextWrapper(applicationContext)
        val imagesFolder = wrapper.getDir("images", Context.MODE_PRIVATE)
        val file = File(imagesFolder,"dd.jpg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    fun addData(name:String, poster_image:String, director:String, synopsis:String, reserved_time:String){
        if(database==null){
            println("데이터베이스를 먼저 오픈하세요\n")
            return
        }
        val sql = "INSERT INTO ${tableName} (name, poster_image, director, synopsis, reserved_time) VALUES (?, ?, ?, ?, ?)"
        val values = arrayOf(name, poster_image, director, synopsis, reserved_time)
        database?.execSQL(sql, values)
        // TODO : 데이터 추가 sql문 작성 및 실행, INSERT INTO sql문을 사용
        println("데이터 추가함\n")
    }

    fun createDatabase(){
        // TODO : database 생성
        database = openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        println("데이터베이스 생성 또는 오픈함")
    }

    fun createTable(){
        val sql = "create table if not exists ${tableName}"+
                "(_id integer PRIMARY KEY autoincrement, "+
                "name text, "+
                "poster_image text, "+
                "director text, "+
                "synopsis text, "+
                "reserved_time text)"

        if(database == null){
            return
        }
        database?.execSQL(sql)
        // TODO : sql 실행
        println("데이터베이스 생성\n")
    }

    fun loadMovie(){
        val movies = queryData()
        val intent = Intent(this, ReservedActivity::class.java)
        intent.putExtra("movies", movies)
        startActivity(intent)
        // TODO : query로 조회한 데이터를 ReservedActivity로 전달
    }

    fun queryData():ArrayList<ReservedMovie>?{
        val sql = "select _id, name, poster_image, director, synopsis, reserved_time from ${tableName}"

        if(database == null){
            println("데이터베이스를 먼저 오픈하세요.\n")
            return null
        }
        val list= arrayListOf<ReservedMovie>()
        val cursor = database?.rawQuery(sql,null)
        if(cursor!=null){
            for (index in 0 until cursor.count){
                cursor.moveToNext()
                // TODO : 레코드에서 데이터 추출
                val _id = cursor.getInt(0)
                val name = cursor.getString(1)
                val poster_image = cursor.getString(2)
                val director = cursor.getString(3)
                val synopsis = cursor.getString(4)
                val reserved_time = cursor.getString(5)


                println("레코드# ${index}: $_id, $name, $poster_image, $director, $synopsis, $reserved_time\n")

                val movie = ReservedMovie(_id,name,poster_image,director,synopsis,reserved_time)
                list.add(movie)
            }
            if (cursor.count == 0) {
                return null
            }
            cursor.close()
        }
        println("데이터 조회함\n")
        return list
    }
}



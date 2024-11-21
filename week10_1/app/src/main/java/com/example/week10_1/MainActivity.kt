package com.example.week10_1

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {
    var database: SQLiteDatabase? = null
    var tableName = "people"
    lateinit var output1: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val doButton1: Button = findViewById<Button>(R.id.doButton1)
        val doButton2: Button = findViewById(R.id.doButton2)
        val doButton3: Button = findViewById(R.id.doButton3)
        val doButton4: Button = findViewById(R.id.doButton4)
        val doButton5: Button = findViewById(R.id.doButton5)
        val doButton6: Button = findViewById(R.id.doButton6)
        output1 = findViewById(R.id.output1)

        doButton1.setOnClickListener {
            createDatabase()
        }
        doButton2.setOnClickListener {
            createTable()
        }
        doButton3.setOnClickListener {
            addData()
        }
        doButton4.setOnClickListener {
            updateData()
        }
        doButton5.setOnClickListener {
            queryData()
        }
        doButton6.setOnClickListener {
            deleteData()
        }
    }


    //데이터 베이스 생성
    fun createDatabase() {
        database = openOrCreateDatabase("people", MODE_PRIVATE, null)
        output1.append("데이터베이스 생성 또는 오픈함\n")
    }

    fun checkDatabase(): Boolean {
        if (database == null) {
            output1.append("데이터베이스를 먼저 오픈하세요.\n")
            return true
        }
        return false
    }

    fun closeDatabase() {
        database?.close()
    }

    //테이블 생성
    fun createTable() {
        if (database == null) {
            output1.append("데이터베이스를 먼저 오픈하세요.\n")
            return
        }
        database?.execSQL("CREATE TABLE IF NOT EXISTS ${tableName}" + "( _id integer PRIMARY KEY autoincrement, " + "name text, " + "age integer, " + "mobile text)")
        output1.append("테이블 생성함\n")
    }



    //데이터 추가
    fun addData() {
        if (database == null) {
            output1.append("데이터베이스를 먼저 오픈하세요.\n")
            return
        }
        database?.execSQL("INSERT INTO ${tableName}" + "(name, age, mobile)" + "values" + "('John', 20, '010-1234-5678');")
        output1.append("데이터 추가함\n")
    }

    fun updateData() {
        if (checkDatabase()) return

        val values = ContentValues()
        values.put("name", "mike")
        values.put("age", "24")
        values.put("mobile", "010-4000-4000")
        var arr: Array<String> = arrayOf("john")
        database?.update(tableName, values, "name=?", arr)
        output1.append("데이터 갱신\n")
    }


    //데이터 조회
    fun queryData() {
        if (checkDatabase()) return

        val query = "SELECT id, name, age, mobile FROM $tableName"
        database?.rawQuery(query, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val age = cursor.getInt(cursor.getColumnIndexOrThrow("age"))
                val mobile = cursor.getString(cursor.getColumnIndexOrThrow("mobile"))
                output1.append("레코드#${cursor.position} : $id, $name, $age, $mobile\n")
            }
        }
    }



    fun deleteData() {
        if (checkDatabase()) return

        val sql = "select id,name,age,mobile from ${tableName}"
        val cursor = database?.rawQuery(sql, null)
        if (cursor != null) {
            cursor.count
            val count = cursor.count
            cursor.close()

            val delete = "delete from ${tableName} where id = ${count}"
            database?.execSQL(delete)
            output1.append("데이터 삭제\n")
        }
    }

}

package com.example.myfirebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val EditName = findViewById<EditText>(R.id.name)
        val EditEmail = findViewById<EditText>(R.id.email)
        val EditId = findViewById<EditText>(R.id.id)
        val EditPw = findViewById<EditText>(R.id.pw)

        val readBtn = findViewById<Button>(R.id.readBtn)
        val writeBtn = findViewById<Button>(R.id.writeBtn)
        val output1 = findViewById<TextView>(R.id.output1)


        readBtn.setOnClickListener{
            readFirebase()
        }

        writeBtn.setOnClickListener{
            var Name : String
            var Email : String
            var Id : String
            var Pw : String

            if(EditName.length() == 0) Name = "null" else Name = EditName.text.toString()
            if(EditEmail.length() == 0) Email = "null" else Email = EditEmail.text.toString()
            if(EditId.length() == 0) Id = "null" else Id = EditEmail.text.toString()
            if(EditPw.length() == 0) Pw = "null" else Pw = EditEmail.text.toString()

            writeFirebase(Name, Email, Id, Pw)
        }
    }
    fun readFirebase()
    {
        val output1 = findViewById<TextView>(R.id.output1)
        db.collection("users")
            .get()
            .addOnSuccessListener {
                    result -> for (document in result)
                output1.append("${document.data} \n")
            }
            .addOnFailureListener {
                output1.append("Failure \n")
            }
    }
    fun writeFirebase(Name: String, Email: String, Id:String, Pw:String)
    {
        val output1 = findViewById<TextView>(R.id.output1)
        val user = mapOf(
            "name" to Name,
            "email" to Email,
            "id" to Id,
            "pw" to Pw
        )
        val colRef: CollectionReference = db.collection("users")
        val docRef: Task<DocumentReference> = colRef.add(user)
        docRef.addOnSuccessListener {
                documentReference -> output1.append("Success : " + "${documentReference.id} \n")
        }
        docRef.addOnFailureListener{
            output1.append("Failure \n")
        }
    }
}

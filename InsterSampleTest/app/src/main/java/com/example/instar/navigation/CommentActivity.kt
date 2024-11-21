package com.example.instar.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instar.R
import com.example.instar.databinding.ActivityCommentBinding
import com.example.instar.databinding.ItemCommentBinding
import com.example.instar.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding
    private var contentUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        contentUid = intent.getStringExtra("contentUid")

        binding.commentRecyclerview.adapter = CommentRecyclerviewAdapter()
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this)

        binding.commentBtnSend.setOnClickListener {
            val comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = binding.commentEditMessage.text.toString()
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)

            binding.commentEditMessage.setText("")
        }
    }

    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<CommentRecyclerviewAdapter.CustomViewHolder>() {

        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()

        init {
            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val comment = comments[position]
            holder.bind(comment)
        }

        inner class CustomViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(comment: ContentDTO.Comment) {
                binding.commentviewitemTextviewComment.text = comment.comment
                binding.commentviewitemTextviewProfile.text = comment.userId

                FirebaseFirestore.getInstance()
                    .collection("profileImages")
                    .document(comment.uid!!)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val url = task.result?.get("image") as String?
                            Glide.with(binding.root.context)
                                .load(url)
                                .apply(RequestOptions().circleCrop())
                                .into(binding.commentviewitemImageviewProfile)
                        }
                    }
            }
        }
    }
}

package com.example.instar.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instar.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.instar.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import android.widget.ImageView // ImageView import 추가
import androidx.fragment.app.FragmentActivity


class DetailViewFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    var uid: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance() // firestore 변수 초기화
        uid = FirebaseAuth.getInstance().currentUser?.uid

        val recyclerView = view.findViewById<RecyclerView>(R.id.detailviewfragment_recyclerview)
        recyclerView.adapter = DetailViewRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        return view
    }


    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            val detailViewFragment = this@DetailViewFragment
            detailViewFragment.firestore.collection("images").orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()

                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        val item = snapshot.toObject(ContentDTO::class.java)
                        item?.let {
                            contentDTOs.add(it)
                            contentUidList.add(snapshot.id)
                        }
                    }
                    notifyDataSetChanged()
                }
        }


        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(p0.context).inflate(R.layout.item_detail, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            val viewholder = (p0 as CustomViewHolder).itemView

            //UserId
            val textViewUserId =
                viewholder.findViewById<TextView>(R.id.detailviewitem_profile_textview)
            textViewUserId.text = contentDTOs!![p1].userId

            //Image
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl)
                .into(viewholder.findViewById(R.id.detailviewitem_imageview_content))

            //Explain of content
            viewholder.findViewById<TextView>(R.id.detailviewitem_explain_textview).text =
                contentDTOs[p1].explain

            //likes
            viewholder.findViewById<TextView>(R.id.detailviewitem_favoritecounter_textview).text =
                "Likes " + contentDTOs[p1].favoriteCount

            //ProfileImage
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl)
                .into(viewholder.findViewById(R.id.detailviewitem_profile_image))

            // Favorite button click listener
            val imageViewFavorite =
                viewholder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
            imageViewFavorite.setOnClickListener {
                favoriteEvent(p1)
            }

            if (contentDTOs[p1].favorites.containsKey(uid)) {
                imageViewFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                imageViewFavorite.setImageResource(R.drawable.ic_favorite_border)
            }

            //This code is when the profile image is clicked
            viewholder.findViewById<ImageView>(R.id.detailviewitem_profile_image).setOnClickListener {
                val fragment = UserFragment()
                val bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[p1].uid)
                bundle.putString("userId", contentDTOs[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content, fragment)?.commit()
            }

            val profileImage = viewholder.findViewById<ImageView>(R.id.detailviewitem_profile_image)
            profileImage.setOnClickListener {
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[p1].uid)
                bundle.putString("userId", contentDTOs[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_content, fragment)?.commit()
            }

            // Comment Image Click Listener
            val commentImageView = viewholder.findViewById<ImageView>(R.id.detailviewitem_comment_imageview)
            commentImageView.setOnClickListener { v ->
                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[p1])
                startActivity(intent)
            }
        }

        fun favoriteEvent(position: Int) {
            val tsDoc = firestore.collection("images").document(contentUidList[position])
            firestore.runTransaction { transaction ->
                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)
                contentDTO?.let {
                    if (it.favorites.containsKey(uid)) {
                        it.favoriteCount -= 1
                        it.favorites.remove(uid)
                    } else {
                        it.favoriteCount += 1
                        it.favorites[uid!!] = true
                    }
                    transaction.set(tsDoc, it)
                }
            }
        }
    }
}


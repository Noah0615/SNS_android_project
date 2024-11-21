package com.example.instar.navigation

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


class DetailViewFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    var uid: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance()
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

            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = holder.itemView

            //UserId
            val textViewUserId =
                viewholder.findViewById<TextView>(R.id.detailviewitem_profile_textview)
            textViewUserId.text = contentDTOs!![position].userId

            //Image
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl)
                .into(viewholder.findViewById(R.id.detailviewitem_imageview_content))

            //Explain of content
            viewholder.findViewById<TextView>(R.id.detailviewitem_explain_textview).text =
                contentDTOs[position].explain

            //likes
            viewholder.findViewById<TextView>(R.id.detailviewitem_favoritecounter_textview).text =
                "Likes " + contentDTOs[position].favoriteCount

            //ProfileImage
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl)
                .into(viewholder.findViewById(R.id.detailviewitem_profile_image))

            // Favorite button click listener
            val imageViewFavorite =
                viewholder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
            imageViewFavorite.setOnClickListener {
                favoriteEvent(position)
            }

            if (contentDTOs[position].favorites.containsKey(uid)) {
                imageViewFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                imageViewFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        fun favoriteEvent(position: Int) {
            val tsDoc = firestore.collection("images").document(contentUidList[position])
            firestore.runTransaction { transaction ->
                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)
                contentDTO?.let {
                    if (it.favorites.containsKey(uid)) {
                        // When the button is clicked
                        it.favoriteCount = it.favoriteCount - 1
                        it.favorites.remove(uid)
                    } else {
                        // When the button is not clicked
                        it.favoriteCount = it.favoriteCount + 1
                        it.favorites[uid!!] = true
                    }
                    transaction.set(tsDoc, it)
                }
            }
        }
    }
}

package com.example.instar.navigation

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instar.LoginActivity
import com.example.instar.MainActivity
import com.example.instar.R
import com.example.instar.navigation.model.ContentDTO
import com.example.instar.navigation.model.FollowDTO
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserFragment : Fragment() {
    private var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    var currentUserUid : String? = null
    //private lateinit var getContent: ActivityResultLauncher<String>
    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        val accountBtnFollowSignout = fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)
        if (uid == currentUserUid) {
            //MyPage
            accountBtnFollowSignout?.text = getString(R.string.signout)
            accountBtnFollowSignout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, LoginActivity::class.java))
                auth?.signOut()
            }
        }
        else {
            //OtherUserPage
            accountBtnFollowSignout?.text = getString(R.string.follow)
            val mainActivity = (activity as MainActivity)
            val toolbarUsername = mainActivity.findViewById<TextView>(R.id.toolbar_username)
            val toolbarBtnBack : ImageView = mainActivity.findViewById(R.id.toolbar_button_back)
            val toolbarTitleImage : ImageView = mainActivity.findViewById(R.id.toolbar_title_image)
            val bottomNavigation : BottomNavigationView = mainActivity.findViewById(R.id.bottom_navigation)
            toolbarUsername?.text = arguments?.getString("userId")
            toolbarBtnBack.setOnClickListener{
                bottomNavigation.selectedItemId = R.id.action_home
            }
            toolbarTitleImage.visibility = View.GONE
            toolbarUsername.visibility = View.VISIBLE
            toolbarBtnBack.visibility = View.VISIBLE
            fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.setOnClickListener {
                requestFollow()
            }
        }

        val accountRecyclerView = fragmentView?.findViewById<RecyclerView>(R.id.account_recyclerview)
        accountRecyclerView?.adapter = UserFragmentRecyclerViewAdapter()
        accountRecyclerView?.layoutManager = GridLayoutManager(requireActivity(), 3)

        fragmentView?.findViewById<ImageView>(R.id.account_iv_profile)?.setOnClickListener{
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }
        getProfileImage()
        getFollowerAndFollowing()
        return fragmentView
    }

    private fun getFollowerAndFollowing(){
        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapShot, firebaseFirestorException ->
            if(documentSnapShot == null) return@addSnapshotListener
            var followDTO = documentSnapShot.toObject(FollowDTO::class.java)
            if (followDTO?.followingCount != null){
                fragmentView?.findViewById<TextView>(R.id.account_tv_following_count)?.text = followDTO.followingCount.toString()
            }
            if (followDTO?.followerCount != null){
                fragmentView?.findViewById<TextView>(R.id.account_tv_follower_count)?.text = followDTO.followerCount.toString()
                if(followDTO.followers.containsKey(currentUserUid!!)){
                    fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.text = getString(R.string.follow_cancel)
                    fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.background?.setColorFilter(
                        ContextCompat.getColor(requireActivity(), R.color.colorLightGray), PorterDuff.Mode.MULTIPLY)
                } else {
                    if(uid != currentUserUid){
                        fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.text = getString(R.string.follow)
                        fragmentView?.findViewById<Button>(R.id.account_btn_follow_signout)?.background?.colorFilter = null
                    }
                }
            }
        }
    }

    private fun requestFollow(){
        //Save data to my account
        val tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followers[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }

            if(followDTO.followings.containsKey(uid)) {
                //remove following third person when a third person is already following
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followers.remove(uid)
            } else {
                //add following third person
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followers[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }
        //Save data to third person
        val tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true

                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if(followDTO!!.followers.containsKey(currentUserUid)) {
                //cancel my follower
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid)
            } else {
                //add my follower
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }

    private fun getProfileImage() {
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapShot, firebaseFirestoreException ->
            if(documentSnapShot == null) return@addSnapshotListener
            if(documentSnapShot.data != null) {
                var url = documentSnapShot.data!!["image"]
                Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop()).into(fragmentView?.findViewById<ImageView>(R.id.account_iv_profile)!!)
            }
        }
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        init {
            firestore?.collection("images")?.whereEqualTo("uid", uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreExeption ->
                //Sometimes, This code return null
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for (snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                val accountTvPostCount = fragmentView?.findViewById<TextView>(R.id.account_tv_post_count)
                accountTvPostCount?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            val imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var imageview = (p0 as CustomViewHolder).imageview
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }
}

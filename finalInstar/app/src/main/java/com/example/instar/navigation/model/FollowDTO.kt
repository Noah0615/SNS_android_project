package com.example.instar.navigation.model

class FollowDTO {
    var followerCount : Int = 0
    var followers : MutableMap<String, Boolean> = HashMap()

    var followingCount : Int = 0
    var followings : MutableMap<String, Boolean> = HashMap()
}
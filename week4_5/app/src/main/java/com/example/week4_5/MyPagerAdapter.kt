package com.example.week4_5


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class MyPagerAdapter(fragmentActivity: FragmentActivity, val datas:MutableList<String>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = datas.size
    override fun createFragment(position: Int): Fragment {
        //1번 viewpager
        if (position % 2 == 0) {
            return MainFragment()

        }
        //2번 viewpager
        else {
            return SubFragment()
        }
    }
}


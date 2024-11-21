package com.example.week4_5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.example.week4_5.Onefragment
import com.example.week4_5.R

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_main, container, false)
        // Get the button from fragment_main.xml
        val fragButton = view.findViewById<AppCompatButton>(R.id.frag_btn)
        val fragmentManager = requireActivity().supportFragmentManager
        var onClick = false
        fragButton.setOnClickListener {
            val transaction = fragmentManager.beginTransaction()
            if (onClick) {
                onClick = false
                // Remove the fragment
                val fragment = fragmentManager.findFragmentById(R.id.fragment_content)
                if (fragment != null) {
                    transaction.remove(fragment).commit()
                }
            } else {
                onClick = true
                // Add the fragment
                transaction.replace(R.id.fragment_content, Onefragment()).commit()
            }
        }
        return view
    }
}

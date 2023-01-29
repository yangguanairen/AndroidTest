package com.sena.module_one.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sena.module_one.R
import com.sena.module_one.databinding.FragmentTestBinding


class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        println("fragment onResume ${this.hashCode()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("fragment onDestroy ${this.hashCode()}")
    }

    override fun onDetach() {
        super.onDetach()
        println("fragment onDetach ${this.hashCode()}")

    }


}
package com.example.romeojtask.ui.positions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.romeojtask.databinding.FragmentPositionsBinding

class PositionsFragment : Fragment() {

    private lateinit var binding: FragmentPositionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPositionsBinding.inflate(inflater, container, false)
        return binding.root
    }
}
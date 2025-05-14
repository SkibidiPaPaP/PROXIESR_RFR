package com.example.proxiesr_rfr.ui.trees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proxiesr_rfr.databinding.FragmentDatainsightBinding

class TreeSpeciesFragment : Fragment() {

    private var _binding: FragmentDatainsightBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val treeSpeciesViewModel =
            ViewModelProvider(this).get(TreeSpeciesViewModel::class.java)

        _binding = FragmentDatainsightBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        treeSpeciesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
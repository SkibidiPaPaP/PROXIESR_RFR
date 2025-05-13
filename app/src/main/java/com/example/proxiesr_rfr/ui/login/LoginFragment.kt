package com.example.proxiesr_rfr.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proxiesr_rfr.R
import com.example.proxiesr_rfr.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the action bar for the login screen
        activity?.actionBar?.hide()

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                // TODO: Implement actual authentication
                // For now, just navigate to home
                findNavController().navigate(R.id.action_loginFragment_to_nav_home)
            }
        }

        binding.registerButton.setOnClickListener {
            // TODO: Implement registration
            Snackbar.make(view, "Registration feature coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.guestButton.setOnClickListener {
            // Navigate directly to home screen
            findNavController().navigate(R.id.action_loginFragment_to_nav_home)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailEditText.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordEditText.error = "Password is required"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
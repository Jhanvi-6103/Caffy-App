package com.myapp.myapplication.Admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.databinding.FragmentSizePricingBinding

class SizePricingFragment : Fragment() {

    private lateinit var binding: FragmentSizePricingBinding
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSizePricingBinding.inflate(inflater, container, false)

        binding.btnSavePrice.setOnClickListener {
            savePrice()
        }

        return binding.root
    }

    private fun savePrice() {

        val size = binding.etSize.text.toString().trim().lowercase()
        val price = binding.etPrice.text.toString().trim()

        if (size.isEmpty() || price.isEmpty()) {
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val map = HashMap<String, Any>()
        map["size"] = size.replaceFirstChar { it.uppercase() }
        map["price"] = price.toInt()

        database.child("sizesPricing").child(size).setValue(map)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Price saved", Toast.LENGTH_SHORT).show()
                binding.etSize.text?.clear()
                binding.etPrice.text?.clear()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

        binding.btnDeletePrice.setOnClickListener {

            val size = binding.etSize.text.toString().trim().lowercase()

            if (size.isEmpty()) {
                Toast.makeText(requireContext(), "Enter size to delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            database.child("sizesPricing").child(size).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Price deleted", Toast.LENGTH_SHORT).show()
                    binding.etSize.text?.clear()
                    binding.etPrice.text?.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
        }

    }
}

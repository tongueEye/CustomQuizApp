package com.example.customquizapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.customquizapp.databinding.ActivityGateBinding

class GateActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGateBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
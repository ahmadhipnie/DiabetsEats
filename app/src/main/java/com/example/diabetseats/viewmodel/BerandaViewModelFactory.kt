package com.example.diabetseats.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.diabetseats.local.room.DiabetsDao

class BerandaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BerandaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BerandaViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

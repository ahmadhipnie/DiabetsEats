package com.sindi.diabetseats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sindi.diabetseats.local.room.DiabetsDao

class MakananViewModelFactory(private val dao: DiabetsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MakananViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MakananViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

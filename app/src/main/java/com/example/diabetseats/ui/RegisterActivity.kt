package com.example.diabetseats.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.diabetseats.R
import com.example.diabetseats.databinding.ActivityRegisterBinding
import com.example.diabetseats.local.entity.UserEntity
import com.example.diabetseats.local.room.DiabetsDao
import com.example.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding

    private lateinit var dao : DiabetsDao
    private var jenisKelamin: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menambahkan listener ke RadioGroup
        binding.rgJenisKelaminRegister.setOnCheckedChangeListener { group, checkedId ->
            jenisKelamin = when (checkedId) {
                R.id.rb_laki_laki -> "pria"
                R.id.rb_perempuan -> "perempuan"
                else -> ""
            }
        }

        dao = DiabetsDatabase.getDatabase(applicationContext).diabetsDao()

        binding.btnRegister.setOnClickListener { registerAdmin() }

    }

    private fun registerAdmin() {
        val namaRegister = binding.etNamaRegister.text.toString().trim()
        val emailRegister = binding.etEmailRegister.text.toString().trim()
        val nomorHpRegister = binding.etNomorHpRegister.text.toString().trim()
        val usiaRegister = binding.etUsiaRegister.text.toString().trim()
        val beratBadanRegister = binding.etBeratBadanRegister.text.toString().trim()
        val tinggiBadanRegister = binding.etTinggiBadanRegister.text.toString().trim()
        val passwordRegister = binding.etPasswordRegister.text.toString().trim()



        if (emailRegister.isEmpty() || passwordRegister.isEmpty() ||namaRegister.isEmpty() || nomorHpRegister.isEmpty() || usiaRegister.isEmpty() || beratBadanRegister.isEmpty() || tinggiBadanRegister.isEmpty() || jenisKelamin.isEmpty()) {
            binding.etEmailRegister.error = "Masukkan email"
            binding.etPasswordRegister.error = "Masukkan password"
            binding.etNamaRegister.error = "Masukkan nama"
            binding.etNomorHpRegister.error = "Masukkan nomor hp"
            binding.etUsiaRegister.error = "Masukkan usia"
            binding.etBeratBadanRegister.error = "Masukkan berat badan"
            binding.etTinggiBadanRegister.error = "Masukkan tinggi badan"
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show()

            return
        }

        if (usiaRegister.toInt() < 40) {
            Toast.makeText(this, "hanya bisa untuk usia lebih dari 40 tahun", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek apakah email sudah terdaftar
        lifecycleScope.launch {
            val existingUser = withContext(Dispatchers.IO) {
                dao.checkRegister(emailRegister)
            }
            if (existingUser != null) {
                // Username sudah terdaftar
                withContext(Dispatchers.Main) {
                    binding.etEmailRegister.error = "email sudah terdaftar"
                    Toast.makeText(this@RegisterActivity, "Email sudah terdaftar", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Username belum terdaftar, tambahkan admin baru ke database
                val admin = UserEntity(emailRegister, namaRegister, passwordRegister, nomorHpRegister, jenisKelamin, usiaRegister.toInt(), tinggiBadanRegister.toInt(), beratBadanRegister.toInt())
                insertUser(admin)
                Intent(this@RegisterActivity, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    private suspend fun insertUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            dao.insertUser(user)
        }
    }
}
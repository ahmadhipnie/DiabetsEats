package com.example.diabetseats.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.diabetseats.R
import com.example.diabetseats.databinding.ActivityLupaSandiBinding
import com.example.diabetseats.local.room.DiabetsDao
import com.example.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LupaSandiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLupaSandiBinding
    private lateinit var dao : DiabetsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaSandiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = DiabetsDatabase.getDatabase(this).diabetsDao()


        binding.apply {
            btnUbahPassword.setOnClickListener {
                val email = etEmailLupaSandi.text.toString()
                val password = etPasswordLupaSandi.text.toString()
                val confirmPassword = etKonfirmasiPasswordLupaSandi.text.toString()

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this@LupaSandiActivity, "Data tidak boleh kosong" , Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(this@LupaSandiActivity, "password tidak sama" , Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val user = dao.checkRegister(email)
                            if (user != null) {
                                dao.updatePassword(email, password)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@LupaSandiActivity, "password berhasil diubah" , Toast.LENGTH_SHORT).show()

                                    val intentKeLogin = Intent(this@LupaSandiActivity, LoginActivity::class.java)
                                    startActivity(intentKeLogin)
                                    finish()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@LupaSandiActivity, "email tidak terdaftar" , Toast.LENGTH_SHORT).show()

                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
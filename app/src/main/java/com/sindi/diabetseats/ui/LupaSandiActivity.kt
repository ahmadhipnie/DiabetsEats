package com.sindi.diabetseats.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sindi.diabetseats.databinding.ActivityLupaSandiBinding
import com.sindi.diabetseats.local.room.DiabetsDao
import com.sindi.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LupaSandiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLupaSandiBinding
    private lateinit var dao: DiabetsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout menggunakan view binding
        binding = ActivityLupaSandiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan objek DiabetsDao dari database
        dao = DiabetsDatabase.getDatabase(this).diabetsDao()

        // Menambahkan listener untuk tombol kembali
        binding.tvKembaliLupaPassword.setOnClickListener {
            val intentKeLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentKeLogin)
        }

        // Menangani klik tombol "Simpan"
        binding.apply {
            tvSimpan.setOnClickListener {
                // Mengambil data dari input pengguna
                val email = etEmailLupaSandi.text.toString()
                val password = etPasswordLupaSandi.text.toString()
                val confirmPassword = etKonfirmasiPasswordLupaSandi.text.toString()

                // Validasi input
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(
                        this@LupaSandiActivity,
                        "Data tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(
                        this@LupaSandiActivity,
                        "Password tidak sama",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Proses reset password menggunakan coroutine
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            // Memeriksa apakah email terdaftar
                            val user = dao.checkRegister(email)
                            if (user != null) {
                                // Jika email terdaftar, update password dalam database
                                dao.updatePassword(email, password)
                                // Tampilkan pesan sukses
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@LupaSandiActivity,
                                        "Password berhasil diubah",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Redirect ke LoginActivity setelah berhasil reset password
                                    val intentKeLogin =
                                        Intent(this@LupaSandiActivity, LoginActivity::class.java)
                                    startActivity(intentKeLogin)
                                    finish()
                                }
                            } else {
                                // Tampilkan pesan bahwa email tidak terdaftar
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@LupaSandiActivity,
                                        "Email tidak terdaftar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.sindi.diabetseats.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sindi.diabetseats.databinding.ActivityLoginBinding
import com.sindi.diabetseats.local.entity.*
import com.sindi.diabetseats.local.room.DiabetsDao
import com.sindi.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dao: DiabetsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        dao = DiabetsDatabase.getDatabase(applicationContext).diabetsDao()

        binding.btnLoginUser.setOnClickListener { loginUser() }
        binding.tvDaftar.setOnClickListener {
            val intentKeRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentKeRegister)
        }

        binding.tvLupaSandi.setOnClickListener {
            val intentKeLupaSandi = Intent(this, LupaSandiActivity::class.java)
            startActivity(intentKeLupaSandi)
        }

        val rasioIndex = listOf(
            IndexRasioEntity(1, 0f),
            IndexRasioEntity(2, 0f),
            IndexRasioEntity(3, 0.58f),
            IndexRasioEntity(4, 0.9f),
            IndexRasioEntity(5, 1.12f),
            IndexRasioEntity(6, 1.24f),
            IndexRasioEntity(7, 1.32f),
            IndexRasioEntity(8, 1.41f),
            IndexRasioEntity(9, 1.45f),
            IndexRasioEntity(10, 1.48f),
            IndexRasioEntity(11, 1.51f),
            IndexRasioEntity(12, 1.48f),
            IndexRasioEntity(13, 1.56f),
            IndexRasioEntity(14, 1.57f),
            IndexRasioEntity(15, 1.59f),
        )

        val perbadinganKriteria = listOf(
            PerbandinganKriteriaEntity(1, 28, 29, 5f),
            PerbandinganKriteriaEntity(2, 28, 30, 3f),
            PerbandinganKriteriaEntity(3, 29, 30, 0.5f),
        )

        val pvKriteriaEntity = listOf(
            PvKriteriaEntity(1, 0.647947f),
            PvKriteriaEntity(2, 0.122182f),
            PvKriteriaEntity(3, 0.229871f),
        )

        val nutrisiEntity = listOf(
            NutrisiEntity(1, "pria", "30-49", 70, 70, 394),
            NutrisiEntity(2, "pria", "50-64", 65, 60, 349),
            NutrisiEntity(3, "pria", "65-80", 64, 50, 309),
            NutrisiEntity(4, "pria", "81-120", 64, 45, 248),
            NutrisiEntity(5, "perempuan", "30-49", 60, 60, 323),
            NutrisiEntity(6, "perempuan", "50-64", 60, 50, 285),
            NutrisiEntity(7, "perempuan", "65-80", 58, 45, 252),
            NutrisiEntity(8, "perempuan", "81-120", 58, 40, 232),
        )

        lifecycleScope.launch(Dispatchers.IO) {
            rasioIndex.forEach { dao.insertRasio(it) }
            perbadinganKriteria.forEach { dao.insertPerbandinganKriteriaEntity(it) }
            pvKriteriaEntity.forEach { dao.insertPvKriteria(it) }
            nutrisiEntity.forEach { dao.insertNutrisiEntity(it) }
        }

        checkLoginStatus()
    }

    private fun loginUser() {
        val email = binding.etEmailSignIn.text.toString()
        val password = binding.etPasswordSignIn.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            // Tampilkan pesan error jika email atau password kosong
            binding.etEmailSignIn.error = "Masukkan email"
            binding.etPasswordSignIn.error = "Masukkan password"
            return
        }

        // Lakukan proses login dengan memanggil fungsi login dari DAO
        lifecycleScope.launch(Dispatchers.IO) {
            val user = dao.login(email, password)
            if (user != null) {
                // Simpan data login ke SharedPreferences jika login berhasil
                saveLoginData(user)
                // Redirect ke MainActivity
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                // Tampilkan pesan error jika login gagal
                withContext(Dispatchers.Main) {
                    binding.etPasswordSignIn.error = "Email atau password salah"
                }
            }
        }
    }

    private fun saveLoginData(user: UserEntity) {
        val editor = sharedPreferences.edit()
        editor.putString("nama", user.nama)
        editor.putString("email", user.email)
        editor.putString("password", user.password)
        editor.putString("jenis_kelamin", user.jenisKelamin)
        editor.putString("tanggal_lahir", user.tanggalLahir)
        editor.putInt("berat_badan", user.beratBadan)
        editor.putInt("tinggi_badan", user.tinggiBadan)
        editor.putString("waktu_makan", "pagi")
        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        // Redirect ke MainActivity setelah login berhasil
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        // Menutup aplikasi saat tombol kembali ditekan
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        System.exit(1)
        super.onBackPressed()
    }
}

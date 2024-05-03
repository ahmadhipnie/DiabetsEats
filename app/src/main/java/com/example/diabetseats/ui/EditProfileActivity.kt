package com.example.diabetseats.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.diabetseats.databinding.ActivityEditProfileBinding
import com.example.diabetseats.local.entity.UserEntity
import com.example.diabetseats.local.room.DiabetsDao
import com.example.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dao : DiabetsDao




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = DiabetsDatabase.getDatabase(this).diabetsDao()

        sharedPreferences = this.getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        val nomorHp = sharedPreferences.getString("nomor_hp", "")
        val usia = sharedPreferences.getInt("usia", 0)
        val beratBadan = sharedPreferences.getInt("berat_badan", 0)
        val tinggiBadan = sharedPreferences.getInt("tinggi_badan", 0)

        binding.etNomorHpEditProfile.setText(nomorHp)
        binding.etUsiaEditProfile.setText(usia.toString())
        binding.etBeratBadanEditProfile.setText(beratBadan.toString())
        binding.etTinggiBadanEditProfile.setText(tinggiBadan.toString())



        binding.apply {
            btnEditProfile.setOnClickListener {
                editProfile()
            }

            btnKembaliKeProfile.setOnClickListener {
                finish()
            }
        }



    }

    private fun editProfile() {
        sharedPreferences = this.getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        val emailEditProfile = sharedPreferences.getString("email", "")
        val nomorHpEditProfile = binding.etNomorHpEditProfile.text.toString().trim()
        val usiaEditProfile = binding.etUsiaEditProfile.text.toString().trim().toIntOrNull()
        val beratBadanEditProfile = binding.etBeratBadanEditProfile.text.toString().trim().toIntOrNull()
        val tinggiBadanEditProfile = binding.etTinggiBadanEditProfile.text.toString().trim().toIntOrNull()

        if (nomorHpEditProfile.isEmpty() || usiaEditProfile == null || beratBadanEditProfile == null || tinggiBadanEditProfile == null) {
            if (nomorHpEditProfile.isEmpty()) binding.etNomorHpEditProfile.error = "Masukkan nomor hp"
            if (usiaEditProfile == null) binding.etUsiaEditProfile.error = "Masukkan usia"
            if (beratBadanEditProfile == null) binding.etBeratBadanEditProfile.error = "Masukkan berat badan"
            if (tinggiBadanEditProfile == null) binding.etTinggiBadanEditProfile.error = "Masukkan tinggi badan"
        } else {
            val dao = DiabetsDatabase.getDatabase(this@EditProfileActivity).diabetsDao()
            lifecycleScope.launch {
                val user = dao.getUserByEmail(emailEditProfile ?: "") // Ambil data user berdasarkan email
                user?.let {
                    // Update data user
                    it.nomorHp = nomorHpEditProfile
                    it.usia = usiaEditProfile
                    it.beratBadan = beratBadanEditProfile
                    it.tinggiBadan = tinggiBadanEditProfile
                    // Panggil fungsi untuk melakukan pembaruan data pada database
                    editUser(it)

                    // Update nilai SharedPreferences
                    sharedPreferences.edit().apply {
                        putString("nomor_hp", nomorHpEditProfile)
                        putInt("usia", usiaEditProfile)
                        putInt("berat_badan", beratBadanEditProfile)
                        putInt("tinggi_badan", tinggiBadanEditProfile)
                        apply()
                    }


                    // Navigasi kembali ke MainActivity
                    val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                    intent.putExtra("tab", "profile") // Menambahkan ekstra "tab" untuk membuka tab Perkembangan
                    startActivity(intent)
                    finish() // Mengakhiri activity TambahPerkembanganActivity


                    // Kembali ke halaman profile
                    finish()
                }
            }
        }
    }


    private suspend fun editUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            dao.updateUserDetails(user.email, user.nomorHp, user.usia, user.tinggiBadan, user.beratBadan)
        }
    }

}
package com.example.diabetseats.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.diabetseats.databinding.ActivityEditProfileBinding
import com.example.diabetseats.local.entity.UserEntity
import com.example.diabetseats.local.room.DiabetsDao
import com.example.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dao: DiabetsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = DiabetsDatabase.getDatabase(this).diabetsDao()

        sharedPreferences = this.getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        val nomorHp = sharedPreferences.getString("nomor_hp", "")
        val tanggalLahir = sharedPreferences.getString("tanggal_lahir", "")
        val beratBadan = sharedPreferences.getInt("berat_badan", 0)
        val tinggiBadan = sharedPreferences.getInt("tinggi_badan", 0)

        binding.etNomorHpEditProfile.setText(nomorHp)
        binding.etTanggalLahirEditProfile.setText(tanggalLahir)
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
        val tanggalLahirEditProfile = binding.etTanggalLahirEditProfile.text.toString().trim()
        val beratBadanEditProfile = binding.etBeratBadanEditProfile.text.toString().trim().toIntOrNull()
        val tinggiBadanEditProfile = binding.etTinggiBadanEditProfile.text.toString().trim().toIntOrNull()

        if (validateInputs(nomorHpEditProfile, tanggalLahirEditProfile, beratBadanEditProfile, tinggiBadanEditProfile)) {
            lifecycleScope.launch {
                val user = dao.getUserByEmail(emailEditProfile ?: "") // Ambil data user berdasarkan email
                user?.let {
                    // Update data user
                    it.nomorHp = nomorHpEditProfile
                    it.tanggalLahir = tanggalLahirEditProfile
                    it.beratBadan = beratBadanEditProfile!!
                    it.tinggiBadan = tinggiBadanEditProfile!!

                    // Panggil fungsi untuk melakukan pembaruan data pada database
                    editUser(it)

                    // Update nilai SharedPreferences
                    sharedPreferences.edit().apply {
                        putString("nomor_hp", nomorHpEditProfile)
                        putString("tanggal_lahir", tanggalLahirEditProfile)
                        putInt("berat_badan", beratBadanEditProfile)
                        putInt("tinggi_badan", tinggiBadanEditProfile)
                        apply()
                    }

                    // Navigasi kembali ke MainActivity
                    val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                    intent.putExtra("tab", "profile") // Menambahkan ekstra "tab" untuk membuka tab Perkembangan
                    startActivity(intent)
                    finish() // Mengakhiri activity
                }
            }
        }
    }

    private fun validateInputs(
        nomorHp: String,
        tanggalLahir: String,
        beratBadan: Int?,
        tinggiBadan: Int?
    ): Boolean {
        if (TextUtils.isEmpty(nomorHp)) {
            binding.etNomorHpEditProfile.error = "Masukkan nomor hp"
            return false
        }

        if (TextUtils.isEmpty(tanggalLahir)) {
            binding.etTanggalLahirEditProfile.error = "Masukkan tanggal lahir"
            return false
        } else {
            // Validate tanggal lahir format
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            try {
                LocalDate.parse(tanggalLahir, formatter)
            } catch (e: DateTimeParseException) {
                binding.etTanggalLahirEditProfile.error = "Format tanggal salah (dd-MM-yyyy)"
                return false
            }
        }

        if (beratBadan == null) {
            binding.etBeratBadanEditProfile.error = "Masukkan berat badan"
            return false
        }

        if (tinggiBadan == null) {
            binding.etTinggiBadanEditProfile.error = "Masukkan tinggi badan"
            return false
        }

        return true
    }

    private suspend fun editUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            dao.updateUserDetails(user.email, user.nomorHp, user.tanggalLahir, user.tinggiBadan, user.beratBadan)
        }
    }
}

package com.sindi.diabetseats.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sindi.diabetseats.databinding.ActivityEditProfileBinding
import com.sindi.diabetseats.local.entity.UserEntity
import com.sindi.diabetseats.local.room.DiabetsDao
import com.sindi.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class EditProfileActivity : AppCompatActivity() {

    // Variabel untuk binding view
    private lateinit var binding: ActivityEditProfileBinding

    // Variabel untuk SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    // Variabel untuk DAO
    private lateinit var dao: DiabetsDao

    // Metode ini dipanggil saat Activity dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi DAO
        dao = DiabetsDatabase.getDatabase(this).diabetsDao()

        // Mengambil data dari SharedPreferences
        sharedPreferences = this.getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        val tanggalLahir = sharedPreferences.getString("tanggal_lahir", "")
        val beratBadan = sharedPreferences.getInt("berat_badan", 0)
        val tinggiBadan = sharedPreferences.getInt("tinggi_badan", 0)

        // Mengisi data pada edit text
        binding.etTanggalLahirEditProfile.setText(tanggalLahir)
        binding.etBeratBadanEditProfile.setText(beratBadan.toString())
        binding.etTinggiBadanEditProfile.setText(tinggiBadan.toString())

        // Menambahkan aksi untuk tombol simpan dan kembali
        binding.apply {
            tvSimpanEditProfil.setOnClickListener {
                editProfile()
            }
            tvKembaliEditProfil.setOnClickListener {
                finish()
            }
        }
    }

    // Metode untuk mengedit profil pengguna
    private fun editProfile() {
        // Mengambil data dari SharedPreferences
        sharedPreferences = this.getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        val emailEditProfile = sharedPreferences.getString("email", "")
        val tanggalLahirEditProfile = binding.etTanggalLahirEditProfile.text.toString().trim()
        val beratBadanEditProfile = binding.etBeratBadanEditProfile.text.toString().trim().toIntOrNull()
        val tinggiBadanEditProfile = binding.etTinggiBadanEditProfile.text.toString().trim().toIntOrNull()

        // Memvalidasi input
        if (validateInputs( tanggalLahirEditProfile, beratBadanEditProfile, tinggiBadanEditProfile)) {
            lifecycleScope.launch {
                // Mengambil data user berdasarkan email
                val user = dao.getUserByEmail(emailEditProfile ?: "")
                user?.let {
                    // Mengupdate data user

                    it.tanggalLahir = tanggalLahirEditProfile
                    it.beratBadan = beratBadanEditProfile!!
                    it.tinggiBadan = tinggiBadanEditProfile!!

                    // Memanggil fungsi untuk melakukan pembaruan data pada database
                    editUser(it)

                    // Mengupdate nilai SharedPreferences
                    sharedPreferences.edit().apply {
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

    // Metode untuk memvalidasi input
    private fun validateInputs( tanggalLahir: String, beratBadan: Int?, tinggiBadan: Int?): Boolean {


        if (TextUtils.isEmpty(tanggalLahir)) {
            binding.etTanggalLahirEditProfile.error = "Masukkan tanggal lahir"
            return false
        } else {
            // Memvalidasi format tanggal lahir
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

    // Metode untuk mengedit data user dalam database
    private suspend fun editUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            dao.updateUserDetails(
                user.email,
                user.tanggalLahir,
                user.tinggiBadan,
                user.beratBadan
            )
        }
    }
}

package com.sindi.diabetseats.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sindi.diabetseats.R
import com.sindi.diabetseats.databinding.ActivityRegisterBinding
import com.sindi.diabetseats.local.entity.UserEntity
import com.sindi.diabetseats.local.room.DiabetsDao
import com.sindi.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dao: DiabetsDao
    private var jenisKelamin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout menggunakan view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menambahkan listener ke RadioGroup untuk menentukan jenis kelamin
        binding.rgJenisKelaminRegister.setOnCheckedChangeListener { group, checkedId ->
            jenisKelamin = when (checkedId) {
                R.id.rb_laki_laki -> "pria"
                R.id.rb_perempuan -> "perempuan"
                else -> ""
            }
        }

        // Mendapatkan instance dari Dao
        dao = DiabetsDatabase.getDatabase(applicationContext).diabetsDao()

        // Menambahkan listener ke tombol register
        binding.btnRegister.setOnClickListener { registerAdmin() }

        // Menambahkan listener ke TextView "Masuk" untuk kembali ke halaman login
        binding.tvMasuk.setOnClickListener {
            val intentKeLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentKeLogin)
        }
    }

    // Fungsi untuk menghitung usia berdasarkan tanggal lahir
    fun hitungUsia(tanggalLahir: LocalDate): Int {
        val today = LocalDate.now()
        val usia = Period.between(tanggalLahir, today).years

        // Jika bulan dan tanggal lahir belum terjadi pada tahun ini, kurangi satu tahun
        if (tanggalLahir.monthValue > today.monthValue ||
            (tanggalLahir.monthValue == today.monthValue && tanggalLahir.dayOfMonth > today.dayOfMonth)
        ) {
            return usia
        }
        return usia
    }

    // Fungsi untuk melakukan registrasi
    private fun registerAdmin() {
        val namaRegister = binding.etNamaRegister.text.toString().trim()
        val emailRegister = binding.etEmailRegister.text.toString().trim()
        val tanggalLahirRegister = binding.etTanggalLahirRegister.text.toString().trim()
        val beratBadanRegister = binding.etBeratBadanRegister.text.toString().trim()
        val tinggiBadanRegister = binding.etTinggiBadanRegister.text.toString().trim()
        val passwordRegister = binding.etPasswordRegister.text.toString().trim()

        // Validasi input
        if (emailRegister.isEmpty() || passwordRegister.isEmpty() || namaRegister.isEmpty() ||
            tanggalLahirRegister.isEmpty() || beratBadanRegister.isEmpty() ||
            tinggiBadanRegister.isEmpty() || jenisKelamin.isEmpty()
        ) {
            Toast.makeText(this, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Parsing tanggal lahir menjadi LocalDate
            val parsedDate =
                LocalDate.parse(tanggalLahirRegister, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val usia = hitungUsia(parsedDate)

            // Validasi usia minimal
            if (usia < 40) {
                binding.etTanggalLahirRegister.error = "Usia minimal 40 tahun"
                Toast.makeText(this, "Usia minimal 40 tahun", Toast.LENGTH_SHORT).show()
                return
            }

            // Cek apakah email sudah terdaftar
            lifecycleScope.launch {
                val existingUser = withContext(Dispatchers.IO) {
                    dao.checkRegister(emailRegister)
                }
                if (existingUser != null) {
                    // Email sudah terdaftar
                    binding.etEmailRegister.error = "Email sudah terdaftar"
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email sudah terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Email belum terdaftar, tambahkan pengguna baru ke database
                    val user = UserEntity(
                        emailRegister,
                        namaRegister,
                        passwordRegister,
                        jenisKelamin,
                        tanggalLahirRegister,
                        tinggiBadanRegister.toInt(),
                        beratBadanRegister.toInt()
                    )
                    insertUser(user)
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT)
                        .show()
                    Intent(this@RegisterActivity, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
            }

        } catch (e: DateTimeParseException) {
            // Jika format tanggal lahir tidak sesuai, tampilkan pesan kesalahan
            binding.etTanggalLahirRegister.error = "Format tanggal lahir salah"
            Toast.makeText(this, "Format tanggal lahir salah", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk memasukkan pengguna baru ke database
    private suspend fun insertUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            dao.insertUser(user)
        }
    }
}

package com.sindi.diabetseats.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sindi.diabetseats.databinding.FragmentProfileBinding
import com.sindi.diabetseats.ui.EditProfileActivity
import com.sindi.diabetseats.ui.LoginActivity

class ProfileFragment : Fragment() {

    // Variabel untuk binding view
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Variabel untuk SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    // Memanggil ketika view dibuat pertama kali
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Meng-inflate layout untuk fragment ini
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Fungsi untuk menghitung IMT
    private fun hitungIMT(beratBadan: Int, tinggiBadan: Int): Double {
        val tinggiBadanMeter = tinggiBadan / 100.0
        return beratBadan.toDouble() / (tinggiBadanMeter * tinggiBadanMeter)
    }

    // Fungsi untuk mengklasifikasikan IMT
    private fun klasifikasiIMT(imt: Double): String {
        return when {
            imt < 18.5 -> "Berat Badan Kurang"
            imt < 23.0 -> "Berat Badan Normal"
            else -> "Berat Badan Lebih"
        }
    }

    // Memanggil ketika view sudah dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mendapatkan instance SharedPreferences
        sharedPreferences =
            requireActivity().getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)

        // Mengambil data dari SharedPreferences
        val nama = sharedPreferences.getString("nama", "")
        val email = sharedPreferences.getString("email", "")
        val nomorHp = sharedPreferences.getString("nomor_hp", "")
        val jenisKelamin = sharedPreferences.getString("jenis_kelamin", "")
        val tanggalLahir = sharedPreferences.getString("tanggal_lahir", "")
        val beratBadan = sharedPreferences.getInt("berat_badan", 0)
        val tinggiBadan = sharedPreferences.getInt("tinggi_badan", 0)

        // Menampilkan data di TextView
        binding.tvNamaPengguna.text = nama ?: "N/A"
        binding.tvEmail.text = email ?: "N/A"
        binding.tvNomorTelepon.text = nomorHp ?: "N/A"
        binding.tvJenisKelamin.text = jenisKelamin ?: "N/A"
        binding.tvTanggalLahir.text = tanggalLahir ?: "N/A"
        binding.tvBeratBadan.text = if (beratBadan > 0) "$beratBadan kg" else "N/A"
        binding.tvTinggiBadan.text = if (tinggiBadan > 0) "$tinggiBadan cm" else "N/A"

        // Menghitung dan menampilkan IMT jika berat dan tinggi badan valid
        if (beratBadan > 0 && tinggiBadan > 0) {
            val imt = hitungIMT(beratBadan, tinggiBadan)
            binding.tvImt.text = "IMT: ${String.format("%.1f", imt)} (${klasifikasiIMT(imt)})"
        } else {
            binding.tvImt.text = "IMT: N/A"
        }

        // Menangani klik pada TextView edit profil
        binding.tvEditProfil.setOnClickListener {
            Intent(requireContext(), EditProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        // Menangani klik pada TextView keluar
        binding.tvKeluar.setOnClickListener {
            logoutUser()
        }
    }

    // Fungsi untuk logout pengguna
    private fun logoutUser() {
        // Menghapus nilai Shared Preferences
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Navigasi ke halaman login
        startActivity(Intent(requireActivity(), LoginActivity::class.java))
        requireActivity().finish()
    }

    // Membersihkan binding ketika view dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.diabetseats.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.diabetseats.databinding.FragmentProfileBinding
import com.example.diabetseats.ui.EditProfileActivity
import com.example.diabetseats.ui.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        return binding.root
    }
    private fun hitungIMT(beratBadan: Int, tinggiBadan: Int): Double {
        val tinggiBadanMeter = tinggiBadan / 100.0
        return beratBadan.toDouble() / (tinggiBadanMeter * tinggiBadanMeter)
    }

    private fun klasifikasiIMT(imt: Double): String {
        return when {
            imt < 18.5 -> "Berat Badan Kurang"
            imt < 23.0 -> "Berat Badan Normal"
            else -> "Berat Badan Lebih"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)

        val nama = sharedPreferences.getString("nama", "")
        val email = sharedPreferences.getString("email", "")
        val nomorHp = sharedPreferences.getString("nomor_hp", "")
        val jenisKelamin = sharedPreferences.getString("jenis_kelamin", "")
        val tanggalLahir = sharedPreferences.getString("tanggal_lahir", "")
        val beratBadan = sharedPreferences.getInt("berat_badan", 0)
        val tinggiBadan = sharedPreferences.getInt("tinggi_badan", 0)

        binding.tvNamaUser.text = nama
        binding.tvEmailUser.text = email
        binding.tvNomorHpUser.text = nomorHp
        binding.tvJenisKelaminUser.text = jenisKelamin
        binding.tvUsiaUser.text = tanggalLahir
        binding.tvBeratBadanUser.text = "$beratBadan kg"
        binding.tvTinggiBadanUser.text = "$tinggiBadan cm"


        val imt = hitungIMT(beratBadan, tinggiBadan)
        binding.tvImtUser.text = "IMT: ${String.format("%.1f", imt)} (${klasifikasiIMT(imt)})"


        binding.btnEditProfile.setOnClickListener {
            Intent(requireContext(), EditProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        // Menghapus nilai Shared Preferences
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Navigasi ke halaman login atau halaman awal aplikasi
        // Gantikan "LoginActivity" dengan nama kelas LoginActivity jika menggunakan aktivitas, atau ganti dengan Fragment lain jika menggunakan Fragment
        startActivity(Intent(requireActivity(), LoginActivity::class.java))
        requireActivity().finish()
    }
    private fun exitApp() {
        requireActivity().finishAffinity() // Keluar dari aplikasi
    }


}
package com.sindi.diabetseats.ui

import android.os.Bundle
import android.os.Process
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sindi.diabetseats.R
import com.sindi.diabetseats.databinding.ActivityMainBinding
import com.sindi.diabetseats.ui.fragments.BerandaFragment
import com.sindi.diabetseats.ui.fragments.MakananFragment
import com.sindi.diabetseats.ui.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    // Deklarasi fragment
    var berandaFragment: BerandaFragment = BerandaFragment()
    var profileFragment: ProfileFragment = ProfileFragment()
    var makananFragment: MakananFragment = MakananFragment()

    // Deklarasi binding untuk layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout menggunakan view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi BottomNavigationView
        binding.bottomNavigationView.apply {
            setOnNavigationItemSelectedListener(this@MainActivity)

            // Mengecek ekstra "tab" yang diterima dari intent untuk menentukan tab yang akan dibuka
            val tabToOpen = intent.getStringExtra("tab")
            if (tabToOpen != null) {
                when (tabToOpen) {
                    "profile" -> {
                        // Buka tab profil jika "tab" == "profile"
                        supportFragmentManager.beginTransaction().replace(R.id.flFragment, profileFragment)
                            .commit()
                        selectedItemId = R.id.menu_profile
                    }
                    "makanan" -> {
                        // Buka tab makanan jika "tab" == "makanan"
                        supportFragmentManager.beginTransaction().replace(R.id.flFragment, makananFragment)
                            .commit()
                        selectedItemId = R.id.menu_makanan
                    }
                    else -> {
                        // Buka tab beranda sebagai default
                        supportFragmentManager.beginTransaction().replace(R.id.flFragment, berandaFragment)
                            .commit()
                    }
                }
            } else {
                // Buka tab beranda sebagai default jika tidak ada ekstra "tab"
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, berandaFragment)
                    .commit()
            }
        }
    }

    // Metode yang dipanggil saat item navigasi dipilih
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId

        // Tampilkan fragment yang sesuai berdasarkan item yang dipilih
        when (itemId) {
            R.id.menu_beranda -> {
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, berandaFragment)
                    .commit()
                return true
            }
            R.id.menu_profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, profileFragment)
                    .commit()
                return true
            }
            R.id.menu_makanan -> {
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, makananFragment)
                    .commit()
                return true
            }
        }
        return false
    }

    // Metode yang dipanggil saat tombol kembali ditekan
    override fun onBackPressed() {
        // Menutup aplikasi
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        System.exit(1)
        super.onBackPressed()
    }
}

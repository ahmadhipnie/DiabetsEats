package com.example.diabetseats.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.view.MenuItem
import com.example.diabetseats.R
import com.example.diabetseats.databinding.ActivityMainBinding
import com.example.diabetseats.ui.fragments.BerandaFragment
import com.example.diabetseats.ui.fragments.MakananFragment
import com.example.diabetseats.ui.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    var bottomNavigationView: BottomNavigationView? = null

    var berandaFragment: BerandaFragment = BerandaFragment()

    var profileFragment: ProfileFragment = ProfileFragment()

    var makananFragment: MakananFragment = MakananFragment()

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
            setOnNavigationItemSelectedListener(this@MainActivity)

            val tabToOpen = intent.getStringExtra("tab")
            if (tabToOpen != null && tabToOpen == "profile") {
                // Open the Akun tab
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, profileFragment)
                    .commit()
                bottomNavigationView?.setSelectedItemId(R.id.menu_profile)

            } else if (tabToOpen != null && tabToOpen == "makanan") {
                // Open the chat tab
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, makananFragment)
                    .commit()
                bottomNavigationView?.setSelectedItemId(R.id.menu_makanan)
            } else if (tabToOpen != null && tabToOpen == "beranda") {
                // Open the dashboard tab
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, berandaFragment)
                    .commit()
                bottomNavigationView?.setSelectedItemId(R.id.menu_beranda)

            } else {
                // Set default fragment
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, berandaFragment)
                    .commit()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId // Mendapatkan ID item yang dipilih


        if (itemId == R.id.menu_beranda) {
            supportFragmentManager.beginTransaction().replace(R.id.flFragment, berandaFragment)
                .commit()
            return true
        } else if (itemId == R.id.menu_profile) {
            supportFragmentManager.beginTransaction().replace(R.id.flFragment, profileFragment)
                .commit()
            return true
        } else if (itemId == R.id.menu_makanan) {
            supportFragmentManager.beginTransaction().replace(R.id.flFragment, makananFragment)
                .commit()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        // Menutup aplikasi saat tombol kembali ditekan
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        System.exit(1)
        super.onBackPressed()
    }
}
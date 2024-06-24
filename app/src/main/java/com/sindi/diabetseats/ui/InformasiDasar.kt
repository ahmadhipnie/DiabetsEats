package com.sindi.diabetseats.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sindi.diabetseats.databinding.ActivityInformasiDasarBinding

class InformasiDasar : AppCompatActivity() {
    private lateinit var binding: ActivityInformasiDasarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformasiDasarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvKembaliInformasi.setOnClickListener {
            finish() // Menutup ProfileSayaActivity dan kembali ke aktivitas sebelumnya
        }
    }
}

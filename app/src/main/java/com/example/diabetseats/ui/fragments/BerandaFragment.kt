package com.example.diabetseats.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diabetseats.adapter.BerandaMakananMalamAdapter
import com.example.diabetseats.adapter.BerandaMakananPagiAdapter
import com.example.diabetseats.adapter.BerandaMakananSiangAdapter
import com.example.diabetseats.adapter.BerandaMakananSnackAdapter
import com.example.diabetseats.databinding.FragmentBerandaBinding
import com.example.diabetseats.local.repo.FoodWithScore
import com.example.diabetseats.local.repo.NutrientData
import com.example.diabetseats.viewmodel.BerandaViewModel
import com.example.diabetseats.viewmodel.BerandaViewModelFactory
import com.example.diabetseats.local.room.DiabetsDao
import com.example.diabetseats.local.room.DiabetsDatabase
import com.example.diabetseats.ui.MainActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private lateinit var berandaViewModel: BerandaViewModel
    private lateinit var berandaMakananPagiAdapter: BerandaMakananPagiAdapter
    private lateinit var berandaMakananSiangAdapter: BerandaMakananSiangAdapter
    private lateinit var berandaMakananMalamAdapter: BerandaMakananMalamAdapter
    private lateinit var berandaMakananSnackAdapter: BerandaMakananSnackAdapter

    private var foodNamesWithScores = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = BerandaViewModelFactory(requireContext())
        berandaViewModel = ViewModelProvider(this, factory).get(BerandaViewModel::class.java)

        val dao = DiabetsDatabase.getDatabase(requireContext()).diabetsDao()


        berandaMakananPagiAdapter = BerandaMakananPagiAdapter(emptyList(), berandaViewModel, requireActivity().lifecycleScope)
        berandaMakananSiangAdapter = BerandaMakananSiangAdapter(emptyList(), berandaViewModel, requireActivity().lifecycleScope)
        berandaMakananMalamAdapter = BerandaMakananMalamAdapter(emptyList(), berandaViewModel, requireActivity().lifecycleScope)
        berandaMakananSnackAdapter = BerandaMakananSnackAdapter(emptyList(), berandaViewModel, requireActivity().lifecycleScope)

        berandaViewModel.getMorningFood().observe(viewLifecycleOwner) { makananPagiList ->
            berandaMakananPagiAdapter.setData(makananPagiList)
        }

        berandaViewModel.getAfternoonFood().observe(viewLifecycleOwner) { makananSiangList ->
            berandaMakananSiangAdapter.setData(makananSiangList)
        }

        berandaViewModel.getEveningFood().observe(viewLifecycleOwner) { makananMalamList ->
            berandaMakananMalamAdapter.setData(makananMalamList)
        }

        berandaViewModel.getSnackFood().observe(viewLifecycleOwner) { makananList ->
            berandaMakananSnackAdapter.setData(makananList)
        }


            binding.rvMakananPagiBeranda.layoutManager = LinearLayoutManager(requireContext())
            binding.rvMakananPagiBeranda.adapter = berandaMakananPagiAdapter

            binding.rvMakananSiangBeranda.layoutManager = LinearLayoutManager(requireContext())
            binding.rvMakananSiangBeranda.adapter = berandaMakananSiangAdapter

            binding.rvMakananMalamBeranda.layoutManager = LinearLayoutManager(requireContext())
            binding.rvMakananMalamBeranda.adapter = berandaMakananMalamAdapter

            binding.rvMakananCemilanBeranda.layoutManager = LinearLayoutManager(requireContext())
            binding.rvMakananCemilanBeranda.adapter = berandaMakananSnackAdapter







        // Mengambil data dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        val gender = sharedPreferences.getString("jenis_kelamin", "")
        val weight = sharedPreferences.getInt("berat_badan", 0)
        val height = sharedPreferences.getInt("tinggi_badan", 0)
        val tanggalLahir = sharedPreferences.getString("tanggal_lahir", "")

        if (gender.isNullOrEmpty() || weight == 0 || height == 0 || tanggalLahir.isNullOrEmpty()) {
            // Handle null or empty values
            Log.e(TAG, "SharedPreferences values are null or empty.")
            return
        }


        val intentKeMakanan = Intent(requireContext(), MainActivity::class.java)
        intentKeMakanan.putExtra("tab", "makanan") // Menambahkan ekstra "tab" untuk membuka tab Perkembangan


        binding.btnAddMakananPagiBeranda.setOnClickListener {
            sharedPreferences.edit().putString("waktu_makan", "pagi").apply()

            Toast.makeText(requireContext(), "Pilih Makanan Pagi", Toast.LENGTH_SHORT).show()
            startActivity(intentKeMakanan)
        }

        binding.btnAddMakananSiangBeranda.setOnClickListener {
            sharedPreferences.edit().putString("waktu_makan", "siang").apply()

            Toast.makeText(requireContext(), "Pilih Makanan Siang", Toast.LENGTH_SHORT).show()
            startActivity(intentKeMakanan)
        }

        binding.btnAddMakananMalamBeranda.setOnClickListener {
            sharedPreferences.edit().putString("waktu_makan", "malam").apply()

            Toast.makeText(requireContext(), "Pilih Makanan Malam", Toast.LENGTH_SHORT).show()
            startActivity(intentKeMakanan)
        }

        binding.btnAddMakananCemilanBeranda.setOnClickListener {
            sharedPreferences.edit().putString("waktu_makan", "snack").apply()

            Toast.makeText(requireContext(), "Pilih Makanan Cemilan", Toast.LENGTH_SHORT).show()
            startActivity(intentKeMakanan)

        }



        val parsedDate = LocalDate.parse(tanggalLahir, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val age = hitungUsia(parsedDate)

        Log.d(TAG, "Usia: $age tahun")

        val ageRange = when (age) {
            in 30..49 -> "30-49"
            in 50..64 -> "50-64"
            in 65..80 -> "65-80"
            in 81..120 -> "81-120"
            else -> "Unknown"
        }

        berandaViewModel.getNutrisiDataByGenderAndAge(gender.toString(), ageRange).observe(viewLifecycleOwner) { nutrisi ->
            // Lakukan sesuatu dengan data nutrisi yang diperoleh
            // Lakukan sesuatu dengan data nutrisi yang diperoleh
            nutrisi?.let {
                val chartData = mutableListOf<Pair<String, Float>>()
                chartData.add("Lemak" to it.lemakHarian.toFloat())
                chartData.add("Karbohidrat" to it.karbohidratHarian.toFloat())
                chartData.add("Protein" to it.proteinHarian.toFloat())

                // Menampilkan data ke chart atau grafik
                val chartLabels = chartData.map { it.first }
                val chartValues = chartData.map { it.second }

                // Misalkan Anda memiliki BarChartView, Anda bisa mengatur data dan animasinya seperti ini:
                binding.barChartView.animation.duration = 1000L
                binding.barChartView.animate(chartValues.mapIndexed { index, value -> chartLabels[index] to value })
            }
        }




        // Perhitungan Berat Badan Ideal
        val idealWeight = calculateIdealWeight(gender, height)
        Log.d(TAG, "ideal weight: $idealWeight")

        // Perhitungan Kebutuhan Kalori Berdasarkan Berat Badan Ideal
        val basalCalories = calculateBasalCalories(gender, idealWeight)

        Log.d(TAG, "basalCalories: $basalCalories")
        val ageAdjustedCalories = adjustCaloriesByAge(basalCalories, age)
        Log.d(TAG, "ageAdjustedCalories:  $ageAdjustedCalories")

        val caloriesBasedOnBodyType = calculateBasalCalories(gender, idealWeight)?.toFloat()
        Log.d(TAG, "caloriesBasedOnBodyType: $caloriesBasedOnBodyType")



        berandaViewModel.getNutrientData().observe(viewLifecycleOwner) { nutrientDataList ->
            val matrixRatio = calculateMatrixRatio(nutrientDataList)
            Log.d(TAG, "matrixRatio: $matrixRatio")

            lifecycleScope.launch {
                val criteriaValues = dao.getPvKriteriaValues()
                if (criteriaValues.isNotEmpty()) {
                    val suitabilityScores = calculateSuitabilityScores(matrixRatio, criteriaValues)
                    Log.d(TAG, "Suitability Scores: $suitabilityScores")

                    val foodsWithScores = getFoodsWithScores(nutrientDataList, suitabilityScores)
                    val sortedFoodsWithScores = foodsWithScores.sortedByDescending { it.score }

                    foodNamesWithScores = sortedFoodsWithScores.joinToString("\n\n") {
                        "${sortedFoodsWithScores.indexOf(it) + 1}. ${it.foodName}: ${it.score}"
                    }

                    binding.tvPeringkatMakanan.text = foodNamesWithScores

//                    val listOfScores = sortedFoodsWithScores.map { it.score }
//                    binding.barChartView.animation.duration = 1000L
//                    binding.barChartView.animate(listOfScores.mapIndexed { index, score -> "Food $index" to score })

                } else {
                    Log.d(TAG, "Criteria values not found.")
                }
            }
        }

        var consumedCalories = caloriesBasedOnBodyType

        if (caloriesBasedOnBodyType != null) {
            if (gender == "pria" && caloriesBasedOnBodyType < 1100.00) {
                binding.tvKaloriPerhari.text = "Kebutuhan Kalori Per Hari: 1100-1400 kkal"
                consumedCalories = 1100.0f
            } else if (gender == "perempuan" && caloriesBasedOnBodyType < 1000.00) {
                binding.tvKaloriPerhari.text = "Kebutuhan Kalori Per Hari: 1000-1300 kkal"
                consumedCalories = 1000.0f
            } else{
                // Menampilkan hasil perhitungan kebutuhan kalori pada TextView
                binding.tvKaloriPerhari.text = "Kebutuhan Kalori Per Hari: $caloriesBasedOnBodyType kkal"
                consumedCalories = caloriesBasedOnBodyType
            }
        }

        binding.tvPeringkatMakanan.text = foodNamesWithScores

        berandaViewModel.getTotalCalories().observe(viewLifecycleOwner) { totalCalories ->
            // Calculate remaining calories here based on selected food
            val consumedCalories = caloriesBasedOnBodyType?.toFloat() ?: 0.0f
            val remainingCalories = consumedCalories - (totalCalories ?: 0.0f)

            binding.tvSisaKaloriPerhari.text = "Sisa Kalori Per Hari: $remainingCalories kkal"

            if (remainingCalories < 0.0f) {
                binding.tvSisaKaloriPerhari.text = "Anda sudah kelebihan kalori"
                binding.tvSisaKaloriPerhari.setTextColor(resources.getColor(android.R.color.holo_red_dark))

                // Inisialisasi PieChart
                val pieChart = binding.pieChartKaloriTersisa
                pieChart.setUsePercentValues(true) // Menggunakan nilai dalam persentase
                pieChart.description.isEnabled = false // Menonaktifkan deskripsi

                // Menyiapkan data untuk chart (misalnya, semua kalori digunakan)
                val entries = listOf(
                    PieEntry(100f, "Sisa Kalori"), // Masukkan data sisa kalori
                    PieEntry(0f, "Kelebihan Kalori") // Tidak ada kelebihan kalori
                )

                // Atur properti PieDataSet
                val dataSet = PieDataSet(entries, "")
                dataSet.colors = listOf(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark), // Warna untuk sisa kalori
                    ContextCompat.getColor(requireContext(), android.R.color.transparent) // Tidak ada warna untuk kelebihan kalori
                )
                dataSet.sliceSpace = 3f // Ruang antara potongan
                dataSet.selectionShift = 5f // Geser ketika dipilih
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 15f

                // Atur properti PieData
                val pieData = PieData(dataSet)
                pieData.setValueFormatter(PercentFormatter(pieChart)) // Format nilai menjadi persentase
                pieData.setValueTextSize(12f) // Ukuran teks nilai

                // Terapkan data ke chart dan refresh chart
                pieChart.data = pieData
                pieChart.invalidate()
                pieChart.centerText = "Kalori Tersisa" // Teks di tengah chart
                pieChart.animateY(1000) // Animasi chart
            } else {
                binding.tvSisaKaloriPerhari.text = "Sisa Kalori Per Hari: $remainingCalories kkal"
                binding.tvSisaKaloriPerhari.setTextColor(resources.getColor(android.R.color.holo_green_dark))

                // Inisialisasi PieChart
                val pieChart = binding.pieChartKaloriTersisa
                pieChart.setUsePercentValues(true) // Menggunakan nilai dalam persentase
                pieChart.description.isEnabled = false // Menonaktifkan deskripsi

                // Menyiapkan data untuk chart
                val remainingPercentage = (remainingCalories / consumedCalories) * 100 // Hitung persentase sisa kalori
                val excessPercentage = 100f - remainingPercentage // Hitung persentase kelebihan kalori

                val entries = listOf(
                    PieEntry(remainingPercentage, "Sisa Kalori"), // Masukkan data sisa kalori
                    PieEntry(excessPercentage, "Kelebihan Kalori") // Masukkan data kelebihan kalori
                )

                // Atur properti PieDataSet
                val dataSet = PieDataSet(entries, "")
                dataSet.colors = listOf(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark), // Warna untuk sisa kalori
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark) // Warna untuk kelebihan kalori
                )
                dataSet.sliceSpace = 3f // Ruang antara potongan
                dataSet.selectionShift = 15f // Geser ketika dipilih
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 20f

                // Atur properti PieData
                val pieData = PieData(dataSet)
                pieData.setValueFormatter(PercentFormatter(pieChart)) // Format nilai menjadi persentase
                pieData.setValueTextSize(20f) // Ukuran teks nilai
                pieData.setValueTextColor(Color.BLACK) // Warna teks nilai

                // Terapkan data ke chart dan refresh chart
                pieChart.data = pieData
                pieChart.invalidate()
                pieChart.centerText = "Kalori harian" // Teks di tengah chart
                pieChart.setCenterTextColor(Color.BLACK) // Warna teks di tengah chart
                pieChart.setCenterTextSize(20f) // Ukuran teks di tengah chart
                pieChart.animateY(1000) // Animasi chart
            }
        }






    }
    private fun calculateMatrixRatio(nutrientDataList: List<NutrientData>): List<List<Float>> {
        val totalKarbohidrat = nutrientDataList.sumByDouble { it.karbohidratMakanan.toDouble() }.toFloat()
        val totalProtein = nutrientDataList.sumByDouble { it.proteinMakanan.toDouble() }.toFloat()
        val totalLemak = nutrientDataList.sumByDouble { it.lemakMakanan.toDouble() }.toFloat()

        val matrixRatio = mutableListOf<List<Float>>()

        // Loop melalui setiap data nutrisi
        nutrientDataList.forEach { nutrientData ->
            // Hitung perbandingan karbohidrat_makanan, protein_makanan, dan lemak_makanan dari setiap item terhadap total masing-masing nutrisi
            val ratioKarbohidrat = nutrientData.karbohidratMakanan / totalKarbohidrat
            val ratioProtein = nutrientData.proteinMakanan / totalProtein
            val ratioLemak = nutrientData.lemakMakanan / totalLemak

            // Tambahkan perbandingan nutrisi ke dalam list matrixRatio sebagai satu baris
            val row = listOf(ratioKarbohidrat, ratioProtein, ratioLemak)
            matrixRatio.add(row)
        }

        return matrixRatio
    }

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

    private suspend fun calculateSuitabilityScores(matrixRatio: List<List<Float>>, criteriaValues: List<Float>): List<Float> {
        val suitabilityScores = mutableListOf<Float>()
        for (row in matrixRatio) {
            var score = 0.0f
            for (i in row.indices) {
                score += row[i] * criteriaValues[i]
            }
            suitabilityScores.add(score)
        }
        return suitabilityScores
    }

    private fun getFoodsWithScores(nutrientDataList: List<NutrientData>, suitabilityScores: List<Float>): List<FoodWithScore> {
        val foodsWithScores = mutableListOf<FoodWithScore>()
        for ((index, nutrientsData) in nutrientDataList.withIndex()) {
            val score = suitabilityScores[index]
            foodsWithScores.add(FoodWithScore(nutrientsData.namaMakanan, score))
        }
        return foodsWithScores
    }
    private fun showRankedFoods(foodsWithScores: List<FoodWithScore>) {
        val rankedFoodNames = mutableListOf<String>()
        for (foodWithScore in foodsWithScores) {
            rankedFoodNames.add(foodWithScore.foodName)
        }
        val rankedFoodString = rankedFoodNames.joinToString(separator = "\n")
        Log.d(TAG, "Ranked Foods:\n$rankedFoodString")
    }



    // Fungsi untuk menghitung berat badan ideal berdasarkan jenis kelamin dan tinggi badan
    private fun calculateIdealWeight(gender: String?, height: Int): Double {
        return when (gender) {
            "pria" -> (height - 100) - ((height - 100) * 0.10)
            "perempuan" -> (height - 100) - ((height - 100) * 0.15)
            else -> 0.0
        }
    }

    // Fungsi untuk menghitung kebutuhan kalori basal berdasarkan berat badan ideal
    private fun calculateBasalCalories(gender: String?, idealWeight: Double): Double {
        return when (gender) {
            "pria" -> 30 * idealWeight
            "perempuan" -> 25 * idealWeight
            else -> 0.0
        }
    }

    // Fungsi untuk menyesuaikan kebutuhan kalori berdasarkan usia
    private fun adjustCaloriesByAge(basalCalories: Double, age: Int): Double {
        return when {
            age in 40..59 -> basalCalories * 0.95
            age in 60..69 -> basalCalories * 0.90
            age >= 70 -> basalCalories * 0.80
            else -> basalCalories
        }
    }

    // Fungsi untuk menyesuaikan kebutuhan kalori berdasarkan jenis kelamin, berat badan, dan berat badan ideal
    private fun adjustCaloriesByBodyType(gender: String?, weight: Int, idealWeight: Double, basalCalories: Double): Double {
        return when {
            weight >= idealWeight -> basalCalories * 0.80 // Gemuk
            weight < idealWeight -> basalCalories * 1.20 // Kurus
            else -> basalCalories
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

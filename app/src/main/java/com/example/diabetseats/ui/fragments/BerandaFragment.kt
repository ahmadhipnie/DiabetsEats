package com.example.diabetseats.ui.fragments

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diabetseats.adapter.BerandaAdapter
import com.example.diabetseats.databinding.FragmentBerandaBinding
import com.example.diabetseats.local.repo.FoodWithScore
import com.example.diabetseats.local.repo.NutrientData
import com.example.diabetseats.viewmodel.BerandaViewModel
import com.example.diabetseats.viewmodel.BerandaViewModelFactory
import com.example.diabetseats.local.room.DiabetsDao
import com.example.diabetseats.local.room.DiabetsDatabase
import kotlinx.coroutines.launch


class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private lateinit var berandaViewModel: BerandaViewModel
    private lateinit var berandaAdapter: BerandaAdapter
    private lateinit var diabetsDao: DiabetsDao

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


        val layoutManager = LinearLayoutManager(requireContext())
        berandaAdapter = BerandaAdapter(emptyList(), berandaViewModel, requireActivity().lifecycleScope)

        berandaViewModel.getAllFood().observe(viewLifecycleOwner) { makananList ->
            berandaAdapter.setData(makananList)
        }

        binding.rvMakananBeranda.layoutManager = layoutManager
        binding.rvMakananBeranda.adapter = berandaAdapter




        // Mengambil data dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        val gender = sharedPreferences.getString("jenis_kelamin", "")
        val age = sharedPreferences.getInt("usia", 0)
        val weight = sharedPreferences.getInt("berat_badan", 0)
        val height = sharedPreferences.getInt("tinggi_badan", 0)

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

        val caloriesBasedOnBodyType = adjustCaloriesByBodyType(gender, weight, idealWeight, ageAdjustedCalories)
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


        if (gender == "pria" && caloriesBasedOnBodyType < 1100.00) {
            binding.tvKaloriPerhari.text = "Kebutuhan Kalori Per Hari: 1100.00-1400 kkal"
        } else if (gender == "perempuan" && caloriesBasedOnBodyType < 1000.00) {
            binding.tvKaloriPerhari.text = "Kebutuhan Kalori Per Hari: 1000.00-1300 kkal"
        } else{
        // Menampilkan hasil perhitungan kebutuhan kalori pada TextView
            binding.tvKaloriPerhari.text = "Kebutuhan Kalori Per Hari: $caloriesBasedOnBodyType kkal"
        }

        binding.tvPeringkatMakanan.text = foodNamesWithScores
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

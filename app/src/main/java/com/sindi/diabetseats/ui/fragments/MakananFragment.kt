package com.sindi.diabetseats.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sindi.diabetseats.adapter.MakananAdapter
import com.sindi.diabetseats.databinding.FragmentMakananBinding
import com.sindi.diabetseats.local.room.DiabetsDatabase
import com.sindi.diabetseats.viewmodel.MakananViewModel
import com.sindi.diabetseats.viewmodel.MakananViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MakananFragment : Fragment() {

    // Variabel untuk binding view
    private var _binding: FragmentMakananBinding? = null
    private val binding get() = _binding!!

    // Variabel untuk ViewModel dan Adapter
    private lateinit var makananViewModel: MakananViewModel
    private lateinit var makananAdapter: MakananAdapter

    // CoroutineScope untuk menjalankan tugas-tugas asinkron
    private val coroutineScope: CoroutineScope = MainScope()

    // Dipanggil ketika view dibuat pertama kali
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakananBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Dipanggil setelah view dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi database, ViewModel, dan adapter
        val dao = DiabetsDatabase.getDatabase(requireContext()).diabetsDao()
        val factory = MakananViewModelFactory(dao)
        makananViewModel = ViewModelProvider(this, factory).get(MakananViewModel::class.java)
        makananAdapter = MakananAdapter(
            emptyList(),
            makananViewModel,
            coroutineScope
        ) // Teruskan CoroutineScope ke adapter

        // Observasi isLoading dan makanan dari ViewModel
        makananViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        makananViewModel.makanan.observe(viewLifecycleOwner) { makananList ->
            makananAdapter.setData(makananList)
        }

        // Menerapkan aksi pada tombol cari
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                val foodName = binding.searchBar.text.toString()
                makananViewModel.showSearchedFood(foodName)
                false
            }
        }

        // Atur RecyclerView dan adapter
        binding.rvMakanan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMakanan.adapter = makananAdapter

        // Panggil showFood untuk menampilkan daftar makanan
        makananViewModel.showFood()
    }

    // Dipanggil ketika view akan dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        coroutineScope.cancel() // Batalkan CoroutineScope pada onDestroyView untuk menghindari memory leak
    }

    // Menampilkan atau menyembunyikan ProgressBar
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

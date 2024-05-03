package com.example.diabetseats.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diabetseats.adapter.MakananAdapter
import com.example.diabetseats.databinding.FragmentMakananBinding
import com.example.diabetseats.local.room.DiabetsDatabase
import com.example.diabetseats.viewmodel.MakananViewModel
import com.example.diabetseats.viewmodel.MakananViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MakananFragment : Fragment() {

    private var _binding: FragmentMakananBinding? = null
    private val binding get() = _binding!!
    private lateinit var makananViewModel: MakananViewModel
    private lateinit var makananAdapter: MakananAdapter
    private val coroutineScope: CoroutineScope = MainScope() // Buat instance dari CoroutineScope

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMakananBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = DiabetsDatabase.getDatabase(requireContext()).diabetsDao()
        val factory = MakananViewModelFactory(dao)
        makananViewModel = ViewModelProvider(this, factory).get(MakananViewModel::class.java)
        makananAdapter = MakananAdapter(emptyList(), makananViewModel, coroutineScope) // Teruskan CoroutineScope ke adapter


        // Pengamatan isLoading dan makanan dari ViewModel
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




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        coroutineScope.cancel() // Batalkan CoroutineScope pada onDestroyView untuk menghindari memory leak
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
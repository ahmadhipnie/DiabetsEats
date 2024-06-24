package com.sindi.diabetseats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sindi.diabetseats.data.response.HintsItem
import com.sindi.diabetseats.databinding.ItemMakananBerandaBinding
import com.sindi.diabetseats.local.entity.MakananEntity
import com.sindi.diabetseats.viewmodel.BerandaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BerandaMakananSnackAdapter(dataList: List<HintsItem>, private val berandaViewModel: BerandaViewModel,
                                 private val coroutineScope: CoroutineScope)
    : RecyclerView.Adapter<BerandaMakananSnackAdapter.BerandaMakananSnackViewHolder>() {
    private val dataList = ArrayList<MakananEntity>()

    fun setData(data: List<MakananEntity>) {
        this.dataList.clear()
        this.dataList.addAll(data)
        notifyDataSetChanged()
    }

    inner class BerandaMakananSnackViewHolder(val binding: ItemMakananBerandaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(makananItem: MakananEntity) {
            binding.apply {
                tvItemLabelMakananBeranda.text = makananItem.namaMakanan
                tvItemEnergiBeranda.text = "Kalori: ${makananItem.kaloriMakanan} kkal"
                tvItemKarbohidratBeranda.text = "Karbohidrat: ${makananItem.karbohidratMakanan} g"
                tvItemLemakBeranda.text = "Lemak: ${makananItem.lemakMakanan} g"
                tvItemProteinBeranda.text = "Protein: ${makananItem.proteinMakanan} g"

                Glide.with(binding.root)
                    .load(makananItem.image)
                    .into(binding.imgItemFotoMakananBeranda)

                binding.btnFavoriteBeranda.setOnClickListener {
                    coroutineScope.launch {
                        berandaViewModel.deleteFood(makananItem)
                    }
                }
            }
        }

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : BerandaMakananSnackAdapter.BerandaMakananSnackViewHolder {
        val binding = ItemMakananBerandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BerandaMakananSnackViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BerandaMakananSnackAdapter.BerandaMakananSnackViewHolder,
        position: Int
    ) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}
package com.example.diabetseats.data.response
import com.google.gson.annotations.SerializedName

data class MakananResponse(
	@SerializedName("hints")
	val hints: List<HintsItem>,

	@SerializedName("parsed")
	val parsed: List<Any>,

	@SerializedName("text")
	val text: String
)

data class Nutrients(
	@SerializedName("PROCNT")
	val pROCNT: Float, // Ubah tipe data menjadi Double
	@SerializedName("ENERC_KCAL")
	val eNERCKCAL: Float,
	@SerializedName("FAT")
	val fAT: Float, // Ubah tipe data menjadi Double
	@SerializedName("CHOCDF")
	val cHOCDF: Float, // Ubah tipe data menjadi Double
	@SerializedName("FIBTG")
	val fIBTG: Float // Ubah tipe data menjadi Double
)

data class Food(
	@SerializedName("image")
	val image: String,

	@SerializedName("knownAs")
	val knownAs: String,

	@SerializedName("foodId")
	val foodId: String,

	@SerializedName("categoryLabel")
	val categoryLabel: String,

	@SerializedName("label")
	val label: String,

	@SerializedName("category")
	val category: String,

	@SerializedName("nutrients")
	val nutrients: Nutrients
)

data class HintsItem(
	@SerializedName("food")
	val food: Food
)


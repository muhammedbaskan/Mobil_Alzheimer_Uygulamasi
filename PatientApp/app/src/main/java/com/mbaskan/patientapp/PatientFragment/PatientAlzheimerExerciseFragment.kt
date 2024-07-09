package com.mbaskan.patientapp.PatientFragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mbaskan.patientapp.PatientAdapter.QuizAdapter
import com.mbaskan.patientapp.R
import com.mbaskan.patientapp.databinding.FragmentPatientAlzheimerExerciseBinding
import java.lang.ref.WeakReference

class PatientAlzheimerExerciseFragment : Fragment() {
    private var _binding: FragmentPatientAlzheimerExerciseBinding? = null
    private val binding get() = _binding!!
    private var trueCount = 0
    private var falseCount = 0
    private val questions = listOf(
        Question("Bugün hangi mevsimdeyiz?", listOf("İlkbahar", "Yaz", "Sonbahar", "Kış"), 1),
        Question("Hangi aydayız?", listOf("Ocak", "Nisan", "Temmuz", "Ekim"), 2),
        Question("Bugün hangi yıldayız?", listOf("2022", "2023", "2024", "2025"), 2),
        Question("Aşağıdakilerden hangisi bir renk değildir?", listOf("Kırmızı","Mavi","Sarı","Kedi"),3),
        Question("Hangi hayvan suda yaşar?", listOf("Köpek", "Kedi", "Balık", "Kuş"), 2),
        Question("Bir çiftlikte 12 inek ve 7 koyun var. Toplamda kaç hayvan var?", listOf("18", "19", "20", "21"), 1),
        Question("Bir otobüste 15 yolcu var. Bir durakta 5 yolcu indi ve 8 yolcu bindi. Şimdi otobüste kaç yolcu var?", listOf("17", "18", "19", "20"), 2),
        Question("Bir pastayı 8 eşit dilime kestiniz. 3 dilim yedikten sonra kaç dilim kaldı?", listOf("4", "5", "6", "7"), 1),
        Question("Bir markette 3 elma 1 liradır. 6 elma kaç lira eder?", listOf("1 lira", "2 lira", "3 lira", "4 lira"), 1),
        Question("Saat 2:00'de bir etkinlik başlıyor ve 2 saat sürüyor. Etkinlik saat kaçta biter?", listOf("3:00", "4:00", "5:00", "6:00"), 2),
        Question("Resimdeki hayvanın adı nedir?", listOf("Kedi", "Köpek", "Kuş", "Balık"), 0, R.drawable.cat),
        Question("Resimdeki taşıtın adı nedir?", listOf("Araba", "Uçak", "Gemi", "Tren"), 1, R.drawable.plane),
        Question("Resimdeki meyvenin adı nedir?", listOf("Elma", "Armut", "Çilek", "Muz"), 2, R.drawable.strawberry),
        Question("Resimdeki yapının adı nedir?", listOf("Bina", "Ağaç", "Köprü", "Nehir"), 2, R.drawable.bridge),
        Question("Resimdeki nesnenin adı nedir?", listOf("Kalem", "Silgi", "Kitap", "Tebeşir"), 2, R.drawable.book),
        Question("Resimdeki nesnenin adı nedir?", listOf("Güneş", "Ay", "Yıldız", "Gezegen"), 0, R.drawable.image_sun),
        Question("Resimdeki taşıtın adı nedir?", listOf("Kamyon", "Otobüs", "Traktör", "Tır"), 3, R.drawable.image_truck),
        Question("Resimdeki yapının adı nedir?", listOf("Gökdelen", "Apartman", "Villa", "Konak"), 1, R.drawable.image_apartment),
        Question("Resimdeki hayvanın adı nedir?", listOf("Kuş", "Kelebek", "Arı", "Karınca"), 1, R.drawable.image_butterfly),
        Question("Bu nedir?", listOf("Deniz", "Göl", "Nehir", "Gölge"), 2, R.drawable.image_river),
        Question("Resimdeki yiyecek nedir?", listOf("Bal", "Peynir", "Zeytin", "Reçel"), 0, R.drawable.image_honey),
        Question("Resimdeki nesnenin adı nedir?", listOf("Balon", "Yelkenli", "Uçurtma", "Drone"), 2, R.drawable.image_kite),
        Question("Resimdeki giysinin adı nedir?", listOf("Pantolon", "Etek", "Şort", "Takım Elbise"), 3, R.drawable.image_dress),
        Question("Resimdeki hayvanın adı nedir?", listOf("Köpekbalığı", "Yunus", "Balina", "Mors"), 1, R.drawable.image_dolphin),
        Question("Resimdeki nesnenin adı nedir?", listOf("Puzzle", "Kart", "Dama", "Satranç"), 0, R.drawable.image_puzzle),
        Question("Resimdeki nesnenin adı nedir?", listOf("Gözlük", "Kask", "Şapka", "Maske"), 0, R.drawable.image_glasses),
        Question("Resimdeki çalgı aleti nedir?", listOf("Gitar", "Piyano", "Davul", "Keman"), 1, R.drawable.image_guitar),
        Question("Resimdeki eşya nedir?", listOf("Ayakkabı", "Çanta", "Cüzdan", "Kemer"), 3, R.drawable.image_belt),
        Question("Resimdeki giysinin adı nedir?", listOf("Çorap", "Eldiven", "Şal", "Kravat"), 2, R.drawable.image_scarf),
        Question("Resimdeki nesnenin adı nedir?", listOf("Masa", "Sandalye", "Koltuk", "Sehpa"), 3, R.drawable.image_table),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Türkiye", "Almanya", "Fransa", "İtalya"), 0, R.drawable.flag_turkey),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Amerika Birleşik Devletleri", "Kanada", "Avustralya", "İngiltere"), 0, R.drawable.flag_usa),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Japonya", "Çin", "Güney Kore", "Hindistan"), 0, R.drawable.flag_japan),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Rusya", "Norveç", "İsveç", "Finlandiya"), 0, R.drawable.flag_russia),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Brezilya", "Arjantin", "Şili", "Kolombiya"), 0, R.drawable.flag_brazil),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Meksika", "İspanya", "Portekiz", "Arnavutluk"), 1, R.drawable.flag_spain),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Portekiz", "Mısır", "Nijerya", "Cezayir"), 0, R.drawable.flag_portugal),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("İran", "Suudi Arabistan", "Türkmenistan", "Birleşik Arap Emirlikleri"), 0, R.drawable.flag_iran),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Pakistan", "Hindistan", "Bangladeş", "Sri Lanka"), 1, R.drawable.flag_india),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Yunanistan", "İtalya", "Portekiz", "İspanya"), 0, R.drawable.flag_greece),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Kanada", "Avustralya", "Yeni Zelanda", "İrlanda"), 0, R.drawable.flag_canada),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Fransa", "İtalya", "Almanya", "İsviçre"), 2, R.drawable.flag_germany),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("İngiltere", "Avustralya", "Kanada", "Yeni Zelanda"), 0, R.drawable.flag_england),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("İsveç", "Danimarka", "Norveç", "İtalya"), 2, R.drawable.flag_italy),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("İspanya", "Portekiz", "İtalya", "Fransa"), 3, R.drawable.flag_france),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Hindistan", "Bangladeş", "Sri Lanka", "Pakistan"), 3, R.drawable.flag_pakistan),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Çin", "Güney Kore", "Japonya", "Tayvan"), 0, R.drawable.flag_china),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Kolombiya", "Brezilya", "Arjantin", "Peru"), 2, R.drawable.flag_argentina),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Mısır", "Fas", "Nijerya", "Gana"), 0, R.drawable.flag_egypt),
        Question("Bu hangi ülkenin bayrağıdır ??", listOf("Suudi Arabistan", "Katar", "Kuveyt", "Birleşik Arap Emirlikleri"), 0, R.drawable.flag_saudi)



    ).shuffled().take(10)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientAlzheimerExerciseBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = QuizAdapter(requireContext(), questions , WeakReference(this))


        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarAlzheimerExercise)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_arrow_back)

        binding.toolbarAlzheimerExercise.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        return binding.root
    }

    fun setImageViewVisible(isCorrect: Boolean) {
       if(isCorrect) {
           binding.exerciseImageView.setImageResource(R.drawable.ic_correct)
           binding.exerciseImageView.bringToFront()
       }
        else {
           binding.exerciseImageView.setImageResource(R.drawable.ic_incorrect)
           binding.exerciseImageView.bringToFront()
       }
        binding.exerciseImageView.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.exerciseImageView.visibility = View.GONE
        }, 3000) // 3 saniye beklet
    }

}

data class Question(val text: String, val options: List<String>, val correctAnswer: Int, val imageResId: Int? = null)
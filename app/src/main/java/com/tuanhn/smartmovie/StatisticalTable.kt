package com.tuanhn.smartmovie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.tuanhn.smartmovie.adapter.TopSellerAdapter
import com.tuanhn.smartmovie.data.model.entities.Bill
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.databinding.FragmentBookSeatsInformationBinding
import com.tuanhn.smartmovie.databinding.FragmentStatisticalTableBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class StatisticalTable : Fragment() {

    private var binding: FragmentStatisticalTableBinding? = null

    private var adapter: TopSellerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStatisticalTableBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTabLayout()

        setupAdapter()

        observeData()

    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("films").get().addOnSuccessListener { result ->

            val listFilm: MutableList<Film> = mutableListOf()

            for (document in result) {
                val film = document.toObject(Film::class.java)
                listFilm.add(film)
            }

            val listSorted = listFilm.sortedBy { it.film_id }

            observeDataBills(listSorted)

        }
    }

    private fun observeDataBills(list: List<Film>) {
        val db = FirebaseFirestore.getInstance()

        db.collection("bills").get().addOnSuccessListener { result ->

            val listBill: MutableList<Bill> = mutableListOf()

            for (document in result) {
                val bill = document.toObject(Bill::class.java)
                listBill.add(bill)
            }

            val listCount: MutableList<Int> = mutableListOf()

            for (film in list) {
                listCount.add(calculateTotalMoneyForFilm(listBill, film.film_id))
            }

            val top10List = getTop10FilmsWithMoney(list, listCount)

            val top10Films = top10List.map { it.first }
            val top10Count = top10List.map { it.second }

            adapter?.updateMovies(top10Films, top10Count)

        }
    }

    fun getTop10FilmsWithMoney(filmList: List<Film>, moneyList: List<Int>): List<Pair<Film, Int>> {

        val combinedList = filmList.zip(moneyList)

        val filteredList = combinedList.filter { it.second > 0 }

        val sortedList = filteredList.sortedByDescending { it.second }

        return sortedList.take(10)
    }

    private fun calculateTotalMoneyForFilm(bills: List<Bill>, filmId: Int): Int {
        return bills.filter { it.film_id == filmId }.sumOf { it.seats.split(",").size }
    }

    private fun setupAdapter() {
        adapter = TopSellerAdapter(listOf(), listOf())
        binding?.rcvTopSeller?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvTopSeller?.adapter = adapter
    }

    private fun getAllDayOfMonth(): Int {
        val currentDate = LocalDate.now()

        // Lấy thông tin về tháng hiện tại
        val currentYearMonth = YearMonth.of(currentDate.year, currentDate.month)

        // Đếm số ngày của tháng hiện tại
        val daysInMonth = currentYearMonth.lengthOfMonth()

        return daysInMonth
    }

    //chia thanh 10 cot 2-3 ngay 1
    private fun setDataCharInMonth() {

        listDate = mutableListOf("1-4", "5-9", "10-15", "16-21", "22-26", "27-")
        listTotalMoney = mutableListOf(0f, 0f, 0f, 0f, 0f, 0f)


        val db = FirebaseFirestore.getInstance()

        var list: MutableList<Pair<String, Float>> = mutableListOf()

        db.collection("bills").get().addOnSuccessListener { result ->

            listDate[5] = listDate[5] + getAllDayOfMonth()

            var totalMoney: Float = 0f

            for (document in result) {
                val bill = document.toObject(Bill::class.java)
                if (isCurrentMonth(bill.date)) {

                    val currentDay = "${bill.date[0]}${bill.date[1]}"

                    totalMoney += bill.totalMoney

                    addMoney(currentDay.toInt(), bill.totalMoney)

                }
            }

            val currentMonth = LocalDate.now().month

            binding?.tvTotalMoney?.text = "Total Income in ${
                currentMonth.getDisplayName(
                    TextStyle.FULL,
                    Locale.ENGLISH
                )
            }: ${formatNumber(totalMoney)}"

            for (i in 0..5) {
                val pair = Pair(listDate[i], listTotalMoney[i])
                list.add(pair)
            }
            list?.let {
                setDataChar(list)
            } ?: setDataChar(listOf())
        }
    }

    var listDate: MutableList<String> = mutableListOf()
    var listTotalMoney: MutableList<Float> = mutableListOf()

    private fun addMoney(value: Int, money: Float) {
        when (value) {
            in 1..4 -> {
                listTotalMoney[0] += money
            }

            in 5..9 -> {
                listTotalMoney[1] += money
            }

            in 10..15 -> {
                listTotalMoney[2] += money
            }

            in 16..21 -> {
                listTotalMoney[3] += money
            }

            in 22..26 -> {
                listTotalMoney[4] += money
            }

            in 27..31 -> {
                listTotalMoney[5] += money
            }
        }
    }

    private fun setDataCharInYear() {

        val listMonth: MutableList<String> = mutableListOf()

        var listTotalMoneyOfYear: MutableList<Float> =
            mutableListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        for (i in 1..12)
            listMonth.add(i.toString())

        val db = FirebaseFirestore.getInstance()

        db.collection("bills").get().addOnSuccessListener { result ->

            var totalMoney : Float = 0f

            for (document in result) {
                val bill = document.toObject(Bill::class.java)
                if (isCurrentYear(bill.date)) {

                    val month = "${bill.date[3]}${bill.date[4]}"

                    totalMoney += bill.totalMoney

                    listTotalMoneyOfYear[month.toInt() - 1] += bill.totalMoney
                }
            }

            binding?.tvTotalMoney?.text = "Total Income in this year: ${formatNumber(totalMoney)}"

            val listYear: MutableList<Pair<String, Float>> = mutableListOf()

            for (i in 0..11) {
                val pair = Pair(listMonth[i], listTotalMoneyOfYear[i])
                listYear.add(pair)
            }
            listYear?.let {
                setDataChar(it)
            } ?: setDataChar(listOf())
        }
    }
    fun formatNumber(value: Float): String {
        return when {
            value >= 1_000_000_000 -> String.format("%.1fB", value / 1_000_000_000) // Từ 1 tỷ trở lên
            value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000) // Từ 1 triệu trở lên
            value >= 1_000 -> String.format("%.1fk", value / 1_000) // Từ 1 nghìn trở lên
            else -> value.toString() // Dưới 1 nghìn
        }
    }
    private fun setDataCharOnDay() {
        val db = FirebaseFirestore.getInstance()
        var sum = 0f
        var dateNow: String? = null
        db.collection("bills").get().addOnSuccessListener { result ->
            for (document in result) {
                val bill = document.toObject(Bill::class.java)
                if (isToday(bill.date)) {
                    dateNow = bill.date
                    sum += bill.totalMoney
                }
            }

            binding?.tvTotalMoney?.text = "Total InCome today: ${formatNumber(sum)}"

            dateNow?.let {
                it
                val list = listOf(it to sum)
                setDataChar(list)
            } ?: setDataChar(listOf())
        }
    }

    fun isCurrentMonth(dateString: String): Boolean {
        // Định dạng ngày
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Chuyển đổi chuỗi thành LocalDate
        val date = LocalDate.parse(dateString, formatter)

        // Lấy tháng hiện tại
        val currentMonth = LocalDate.now().monthValue

        // So sánh tháng
        return date.monthValue == currentMonth
    }

    fun isCurrentYear(dateString: String): Boolean {
        // Định dạng ngày
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Chuyển đổi chuỗi thành LocalDate
        val date = LocalDate.parse(dateString, formatter)

        // Lấy tháng hiện tại
        val currentYear = LocalDate.now().year

        // So sánh tháng
        return date.year == currentYear
    }

    fun isToday(dateString: String): Boolean {
        // Định dạng ngày
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Chuyển đổi chuỗi thành LocalDate
        val date = LocalDate.parse(dateString, formatter)

        // Lấy ngày hiện tại
        val today = LocalDate.now()

        // So sánh
        return date.isEqual(today)
    }

    private fun setDataChar(chartData: List<Pair<String, Float>>) {
        binding?.customView?.data = chartData
    }

    private fun setTabLayout() {
        val tabLayout = binding?.tabLayout
        tabLayout?.addTab(tabLayout?.newTab()!!.setText("Today"))
        tabLayout?.addTab(tabLayout?.newTab()!!.setText("Month"))
        tabLayout?.addTab(tabLayout?.newTab()!!.setText("Year"))

        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Xử lý khi tab được chọn
                val position = tab.position
                when (position) {
                    0 -> {
                        setDataCharOnDay()
                    }

                    1 -> {
                        setDataCharInMonth()
                    }

                    2 -> {
                        setDataCharInYear()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Xử lý khi tab không còn được chọn
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Xử lý khi tab đã được chọn lại
            }
        })

    }
}
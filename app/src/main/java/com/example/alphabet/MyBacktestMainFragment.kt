package com.example.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.alphabet.databinding.FragmentMyBacktestMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MyBacktestMainFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyBacktestMainBinding.inflate(inflater, container, false)
        val viewPager = binding.viewPagerBacktest
        val tabLayout = binding.tabLayoutBacktest
        val titles = resources.getStringArray(R.array.backtest_tab_titles)

        viewPager.adapter = ViewPagerAdapter(
            childFragmentManager,
            lifecycle,
            listOf(MyStrategyBacktestFragment(), MyPortfolioFragment())
        )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        return binding.root
    }
}
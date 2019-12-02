package jp.co.soramitsu.irohaandroid.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabsViewpagerAdapter(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    private val pages = mutableListOf<Pair<String, Fragment>>()

    fun addPage(title: String, fragment: Fragment) {
        pages.add(Pair(title, fragment))
    }

    override fun getPageTitle(position: Int) = pages[position].first

    override fun getItem(position: Int) = pages[position].second

    override fun getCount() = pages.size
}
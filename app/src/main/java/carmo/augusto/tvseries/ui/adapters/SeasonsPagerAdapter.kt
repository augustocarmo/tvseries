package carmo.augusto.tvseries.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import carmo.augusto.core.entities.Season
import carmo.augusto.tvseries.ui.fragments.SeasonsEpisodesFragment

class SeasonsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val seasons = ArrayList<Season>()

    override fun getItemCount(): Int {
        return seasons.count()
    }

    override fun createFragment(position: Int): Fragment {
        return SeasonsEpisodesFragment.create(seasonId = seasons[position].id)
    }

    fun setSeasons(seasons: List<Season>) {
        this.seasons.clear()

        this.seasons.addAll(seasons)

        this.notifyDataSetChanged()
    }
}
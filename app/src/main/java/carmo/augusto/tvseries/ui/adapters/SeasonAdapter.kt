package carmo.augusto.tvseries.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import carmo.augusto.core.entities.Episode
import carmo.augusto.tvseries.databinding.VhEpisodeBinding

class SeasonAdapter : RecyclerView.Adapter<SeasonAdapter.SeasonEpisodeViewHolder>() {

    private val episodes = ArrayList<Episode>()

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonEpisodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = VhEpisodeBinding.inflate(inflater, parent, false)

        return SeasonEpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeasonEpisodeViewHolder, position: Int) {
        val seasonEpisode = episodes[position]

        holder.binding.number.text = seasonEpisode.number.toString()
        holder.binding.name.text = seasonEpisode.name

        holder.itemView.setOnClickListener {
            listener?.onSeasonEpisodeClicked(episodeId = seasonEpisode.id)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ViewType.SEASON_SECTION_HEADER.type
        } else {
            ViewType.EPISODE.type
        }
    }

    override fun getItemCount(): Int {
        var itemCount = 0

        // episodes count
        itemCount += episodes.count()

        return itemCount
    }

    @MainThread
    fun setEpisodes(episodes: List<Episode>) {
        this.episodes.clear()

        this.episodes.addAll(episodes)

        notifyDataSetChanged()
    }

    interface Listener {

        fun onSeasonEpisodeClicked(episodeId: Int)
    }

    open class SeasonEpisodeModel(
        val id: Int,
        val name: String
    )

    class SeasonEpisodeViewHolder(
        val binding: VhEpisodeBinding,
    ) : RecyclerView.ViewHolder(binding.root)
}

private enum class ViewType(val type: Int) {
    SEASON_SECTION_HEADER(type = 1),
    EPISODE(type = 2)
}
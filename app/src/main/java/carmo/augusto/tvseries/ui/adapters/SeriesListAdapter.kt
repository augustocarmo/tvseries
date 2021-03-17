package carmo.augusto.tvseries.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import carmo.augusto.core.entities.Series
import carmo.augusto.core.extensions.exhaustive
import carmo.augusto.tvseries.R
import carmo.augusto.tvseries.databinding.VhLoadMoreButtonBinding
import carmo.augusto.tvseries.databinding.VhSeriesBinding
import carmo.augusto.tvseries.extensions.toHtml
import com.bumptech.glide.RequestManager

class SeriesListAdapter(
    private val glide: RequestManager,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val seriesList = ArrayList<Series>()
    var loadingMoreButtonState = LoadMoreButtonState.HIDDEN
        set(value) {
            val oldValue = field
            if (value == oldValue) {
                return
            }

            field = value

            when {
                oldValue.visible && value.visible -> {
                    notifyItemChanged(seriesList.count())
                }
                oldValue.visible && !value.visible -> {
                    notifyItemRemoved(seriesList.count())
                }
                !oldValue.visible && value.visible -> {
                    notifyItemInserted(seriesList.count())
                }
                else -> {
                    // do nothing
                }
            }.exhaustive
        }

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_SERIES -> {
                val binding = VhSeriesBinding.inflate(layoutInflater, parent, false)
                SeriesViewHolder(binding)
            }
            VIEW_TYPE_LOAD_MORE_BUTTON -> {
                val binding = VhLoadMoreButtonBinding.inflate(layoutInflater, parent, false)
                LoadMoreButtonViewHolder(binding)
            }
            else -> {
                throw IllegalStateException("Invalid viewType: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)

        when (viewType) {
            VIEW_TYPE_SERIES -> {
                bind(holder = holder as SeriesViewHolder, position = position)
            }
            VIEW_TYPE_LOAD_MORE_BUTTON -> {
                bind(holder = holder as LoadMoreButtonViewHolder, position = position)
            }
            else -> {
                throw IllegalStateException("Invalid viewType: $viewType")
            }
        }.exhaustive
    }

    override fun getItemCount(): Int {
        var itemCount = 0

        itemCount += seriesList.count()
        if (loadingMoreButtonState.visible) {
            itemCount++
        }

        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        if (!loadingMoreButtonState.visible) {
            return VIEW_TYPE_SERIES
        }

        return if (position == seriesList.count()) {
            VIEW_TYPE_LOAD_MORE_BUTTON
        } else {
            VIEW_TYPE_SERIES
        }
    }

    private fun bind(holder: SeriesViewHolder, position: Int) {
        val context = holder.itemView.context
        val series = seriesList[position]

        glide.load(series.posterImage?.mediumUrl)
            .override(holder.binding.poster.width, holder.binding.poster.height)
            .placeholder(R.drawable.ic_placeholder_poster)
            .centerCrop()
            .into(holder.binding.poster)

        holder.binding.name.text = series.name.toHtml()
        holder.binding.summary.text = series.summary?.toHtml()
        holder.binding.genres.text = series.genres.joinToString(separator = " • ")

        holder.binding.poster.transitionName = context.getString(
            R.string.transition_poster,
            series.id
        )
        holder.binding.name.transitionName = context.getString(
            R.string.transition_name,
            series.id
        )
        holder.binding.summary.transitionName = context.getString(
            R.string.transition_summary,
            series.id
        )
        holder.binding.genres.transitionName = context.getString(
            R.string.transition_genres,
            series.id
        )

        holder.itemView.setOnClickListener {
            listener?.onSeriesCardCLicked(series = series)
        }
    }

    private fun bind(holder: LoadMoreButtonViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.binding.button.isVisible = false
        holder.binding.loading.isVisible = false

        when (loadingMoreButtonState) {
            LoadMoreButtonState.HIDDEN -> {
                // do nothing
            }
            LoadMoreButtonState.VISIBLE -> {
                holder.binding.button.text = context.getString(R.string.button_load_more)
                holder.binding.button.setOnClickListener {
                    listener?.onLoadMoreButtonClicked()
                }
                holder.binding.button.isVisible = true
            }
            LoadMoreButtonState.LOADING -> {
                holder.binding.loading.isVisible = true
            }
        }.exhaustive
    }

    @MainThread
    fun setData(seriesList: List<Series>) {
        this.seriesList.clear()

        this.seriesList.addAll(seriesList)

        this.notifyDataSetChanged()
    }

    fun findSeriesAdapterPosition(seriesId: Int): Int {
        return seriesList.indexOfFirst {
            it.id == seriesId
        }
    }

    class SeriesViewHolder(
        val binding: VhSeriesBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    class LoadMoreButtonViewHolder(
        val binding: VhLoadMoreButtonBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    interface Listener {

        fun onSeriesCardCLicked(series: Series)
        fun onLoadMoreButtonClicked()
    }

    enum class LoadMoreButtonState(val visible: Boolean) {
        HIDDEN(visible = false),
        VISIBLE(visible = true),
        LOADING(visible = true)
    }

    companion object {
        private const val VIEW_TYPE_SERIES = 1
        private const val VIEW_TYPE_LOAD_MORE_BUTTON = 2
    }
}
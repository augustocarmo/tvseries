package carmo.augusto.tvseries.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val marginLeft: Int = 0,
    private val marginTop: Int = 0,
    private val marginRight: Int = 0,
    private val marginBottom: Int = 0,
    private val customOffsetsApplication: ((position: Int, outRect: Rect) -> Boolean)? = null
) : RecyclerView.ItemDecoration() {

    constructor(
        margin: Int,
        customOffsetsApplication: ((position: Int, outRect: Rect) -> Boolean)? = null
    ) : this(
        marginLeft = margin,
        marginTop = margin,
        marginRight = margin,
        marginBottom = margin,
        customOffsetsApplication = customOffsetsApplication
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (customOffsetsApplication?.invoke(itemPosition, outRect) == true) {
            return
        }

        outRect.set(
            marginLeft,
            marginTop,
            marginRight,
            marginBottom
        )
    }
}
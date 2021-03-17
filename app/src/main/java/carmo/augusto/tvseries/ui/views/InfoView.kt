package carmo.augusto.tvseries.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import carmo.augusto.tvseries.databinding.ViewInfoBinding

class InfoView : FrameLayout {

    private var _binding: ViewInfoBinding? = null
    private val binding get() = _binding!!

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        _binding = ViewInfoBinding.inflate(LayoutInflater.from(context), this)
    }

    private fun syncVisibility() {
        this.isVisible = binding.loading.isVisible
                || binding.noInternet.isVisible
                || binding.serverError.isVisible
                || binding.genericError.isVisible
    }

    fun showGenericErrorInfoView(clickCallback: (() -> Unit)?) {
        binding.genericError.isVisible = true
        binding.genericError.setOnClickListener {
            clickCallback?.invoke()
        }

        syncVisibility()
    }

    fun showServerErrorInfoView(clickCallback: (() -> Unit)?) {
        binding.serverError.isVisible = true
        binding.serverError.setOnClickListener {
            clickCallback?.invoke()
        }

        syncVisibility()
    }

    fun showNoInternetInfoView(clickCallback: (() -> Unit)?) {
        binding.noInternet.isVisible = true
        binding.noInternet.setOnClickListener {
            clickCallback?.invoke()
        }

        syncVisibility()
    }

    fun showLoadingView() {
        binding.loading.isVisible = true

        syncVisibility()
    }

    fun hideGenericErrorInfoView() {
        binding.genericError.isVisible = false

        syncVisibility()
    }

    fun hideServerErrorInfoView() {
        binding.serverError.isVisible = false

        syncVisibility()
    }

    fun hideNoInternetInfoView() {
        binding.noInternet.isVisible = false

        syncVisibility()
    }

    fun hideLoadingView() {
        binding.loading.isVisible = false

        syncVisibility()
    }

    fun hideAll() {
        hideGenericErrorInfoView()
        hideServerErrorInfoView()
        hideNoInternetInfoView()
        hideLoadingView()

        syncVisibility()
    }
}
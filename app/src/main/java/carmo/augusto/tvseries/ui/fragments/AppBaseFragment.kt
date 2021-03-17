package carmo.augusto.tvseries.ui.fragments

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment


abstract class AppBaseFragment : Fragment() {

    fun showSoftKeyboard(view: View) {
        val activity = activity
        if (activity == null) {
            return
        }

        view.requestFocus()
        val imgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard() {
        val activity = activity
        if (activity == null) {
            return
        }

        val view = view
        if (view == null) {
            return
        }

        val imm: InputMethodManager = activity.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
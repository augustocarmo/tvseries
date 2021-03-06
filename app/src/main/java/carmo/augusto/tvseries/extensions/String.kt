package carmo.augusto.tvseries.extensions

import android.os.Build
import android.text.Html

fun String.toHtml() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
} else {
    Html.fromHtml(this)
}

fun String.strippingHtmlTags() = this.replace(regex = Regex("<.*?>"), replacement = "")
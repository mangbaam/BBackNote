package mangbaam.bbacknote.util

import android.content.Context
import android.util.DisplayMetrics

class RecyclerViewUtil {
    companion object Utility {
        fun calculateCountOfColumns(
            context: Context,
            columnWidthDp: Float = 180F,
            marginWidthDp: Float = 0F
        ): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density - marginWidthDp
            return (screenWidthDp / columnWidthDp + 0.5).toInt()
        }
    }
}
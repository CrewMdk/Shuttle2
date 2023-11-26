package au.com.simplecityapps.shuttle.imageloading

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.WorkerThread
import au.com.simplecityapps.shuttle.imageloading.palette.ColorSet

interface ArtworkImageLoader {
    fun loadArtwork(
        imageView: ImageView,
        data: Any,
        options: List<Options> = emptyList(),
        onCompletion: ((Result<Unit>) -> Unit)? = null,
        onColorSetGenerated: ((ColorSet) -> Unit)? = null
    )

    fun loadBitmap(
        data: Any,
        width: Int,
        height: Int,
        options: List<Options> = emptyList(),
        onCompletion: (Bitmap?) -> Unit
    )

    fun loadColorSet(
        data: Any,
        callback: (ColorSet?) -> Unit
    )

    @WorkerThread
    fun loadBitmap(data: Any): ByteArray?

    fun clear(imageView: ImageView)

    suspend fun clearCache(context: Context?)

    sealed class Options {
        /**
         * @param radius corner radius, in DP
         */
        class RoundedCorners(val radius: Int) : Options()

        class Crossfade(val duration: Int) : Options()

        class Placeholder(val placeholderRes: Drawable) : Options()

        class Error(val errorRes: Drawable) : Options()

        object CircleCrop : Options()

        object CenterCrop : Options()

        sealed class Priority : Options() {
            object Low : Options.Priority()

            object Default : Options.Priority()

            object High : Options.Priority()

            object Max : Options.Priority()
        }

        object LoadColorSet : Options()

        object CacheDecodedResource : Options()
    }
}

package ru.tanexc.gravityfallsmultiplatform.presentation.ui.util.imageTool

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Composable
fun ComposeImage(
    model: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    onLoading: @Composable BoxScope.(progress: Float) -> Unit,
    onError: @Composable BoxScope.() -> Unit,
) {
    val imageTool: ImageTool = remember { ImageToolFactory.get() }
    val imageState: MutableState<ImageState> = remember { mutableStateOf(ImageState.Loading(0f)) }

    Box {
        LaunchedEffect(true) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = imageTool.loadImage(model, collector = { received, total ->
                    imageState.value = ImageState.Loading(received.toFloat() / total)
                })
                imageState.value = ImageState.Success(data)

            }
        }

        when (imageState.value) {
            is ImageState.Loading -> onLoading((imageState.value as ImageState.Loading).progress)
            is ImageState.Failed -> onError()
            is ImageState.Success ->
                Image(
                    bitmap = (imageState.value as ImageState.Success).imageBitmap,
                    contentDescription,
                    modifier,
                    alignment,
                    contentScale,
                    alpha,
                    colorFilter,
                    filterQuality
                )
        }
    }
}
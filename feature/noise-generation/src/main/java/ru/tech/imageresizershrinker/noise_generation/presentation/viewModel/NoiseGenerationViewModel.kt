/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.noise_generation.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.noise_generation.domain.NoiseGenerator
import ru.tech.imageresizershrinker.noise_generation.domain.model.NoiseParams
import javax.inject.Inject

@HiltViewModel
class NoiseGenerationViewModel @Inject constructor(
    dispatchersHolder: DispatchersHolder,
    private val noiseGenerator: NoiseGenerator<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>
) : BaseViewModel(dispatchersHolder) {

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _noiseParams: MutableState<NoiseParams> = mutableStateOf(NoiseParams.Default)
    val noiseParams: NoiseParams by _noiseParams

    private val _noiseSize: MutableState<IntegerSize> = mutableStateOf(IntegerSize(1000, 1000))
    val noiseSize: IntegerSize by _noiseSize

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base(100))
    val quality: Quality by _quality

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveNoise(
        oneTimeSaveLocationUri: String?,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        savingJob = viewModelScope.launch(defaultDispatcher) {
            _isSaving.update { true }
            noiseGenerator.generateNoise(
                width = noiseSize.width,
                height = noiseSize.height,
                noiseParams = noiseParams,
                onFailure = {
                    onComplete(SaveResult.Error.Exception(it))
                }
            )?.let { bitmap ->
                val imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    quality = quality,
                    imageFormat = imageFormat
                )
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            metadata = null,
                            originalUri = "Noise",
                            sequenceNumber = null,
                            data = imageCompressor.compress(
                                image = bitmap,
                                imageFormat = imageFormat,
                                quality = quality
                            )
                        ),
                        keepOriginalMetadata = true,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            }
            _isSaving.update { false }
        }
    }

    fun cacheCurrentNoise(onComplete: (Uri) -> Unit) {
        savingJob = viewModelScope.launch {
            _isSaving.update { true }
            noiseGenerator.generateNoise(
                width = noiseSize.width,
                height = noiseSize.height,
                noiseParams = noiseParams
            )?.let { image ->
                val imageInfo = ImageInfo(
                    width = image.width,
                    height = image.height,
                    quality = quality,
                    imageFormat = imageFormat
                )
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.update { false }
        }
    }

    fun shareNoise(onComplete: () -> Unit) {
        cacheCurrentNoise { uri ->
            viewModelScope.launch {
                shareProvider.shareUri(
                    uri = uri.toString(),
                    onComplete = onComplete
                )
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
    }

    fun updateParams(params: NoiseParams) {
        _noiseParams.update { params }
        updatePreview()
    }

    fun setNoiseWidth(width: Int) {
        _noiseSize.update { it.copy(width = width.coerceAtMost(2048)) }
        updatePreview()
    }

    fun setNoiseHeight(height: Int) {
        _noiseSize.update { it.copy(height = height.coerceAtMost(2048)) }
        updatePreview()
    }

    private fun updatePreview() {
        viewModelScope.launch {
            _isImageLoading.update { true }
            _previewBitmap.update { null }
            noiseGenerator.generateNoise(
                width = noiseSize.width,
                height = noiseSize.height,
                noiseParams = noiseParams
            ).also { bitmap ->
                _previewBitmap.update { bitmap }
                _isImageLoading.update { false }
            }
        }
    }

    init {
        updatePreview()
    }

}
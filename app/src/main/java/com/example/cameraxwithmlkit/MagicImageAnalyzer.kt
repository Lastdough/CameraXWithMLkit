package com.example.cameraxwithmlkit

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class MagicImageAnalyzer(onTextFound: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val textRecognizer = MagicTextRecognizer(onTextFound)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        textRecognizer.recognizeImageText(image, imageProxy.imageInfo.rotationDegrees) {
            imageProxy.close()
        }
    }
}
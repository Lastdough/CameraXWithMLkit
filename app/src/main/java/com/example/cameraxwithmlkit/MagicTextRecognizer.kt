package com.example.cameraxwithmlkit

import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition



class MagicTextRecognizer(private val onTextFound: (String) -> Unit) {
    fun recognizeImageText(image: Image, rotationDegrees: Int, onResult: (Boolean) -> Unit) {
        val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
        TextRecognition.getClient()
            .process(inputImage)
            .addOnSuccessListener { recognizedText ->
                processTextFromImage(recognizedText)
                onResult(true)
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Failed to recognize image text")
                error.printStackTrace()
                onResult(false)
            }
    }

    private fun processTextFromImage(text: Text) {
//        text.textBlocks.joinToString {
////            it.lines[0].boundingBox.toString()
////            it.text.lines().count()
////            it.text.lines().joinToString(" ") + it.boundingBox.toString()
//            getTitleFromImage(it).text
//        }.let {
//            if (it.isNotBlank()) {
//                onTextFound(it)
//            }
//        }
        if (text.textBlocks.isNotEmpty()){
            onTextFound(getTitleFromImage(text).text)
        }
    }

    private fun getTitleFromImage(text: Text): Text.Line {
        var maxHeight = 0
        var maxLine = text.textBlocks[0].lines[0]

        for (block in text.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineHeight = line.boundingBox?.height()
                if (lineHeight != null) {
                    if (lineHeight > maxHeight){
                        maxLine = line
                        maxHeight = lineHeight
                    }
                }
            }
        }

        return maxLine
    }

    companion object {
        private val TAG = MagicTextRecognizer::class.java.name
    }

}




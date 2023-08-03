package com.psijuego.core.utils

class UtilText {
    companion object {
        private val context = CoreModule.getContext()!!

        fun formatTextMaxCharactersPerLine(text: String, maxLength: Int): String {
            val formattedText = if (text.length > maxLength) {
                var cutoffIndex = maxLength - 1
                while (cutoffIndex >= 0 && text[cutoffIndex] != ' ') {
                    cutoffIndex--
                }
                if (cutoffIndex >= 0) {
                    text.substring(0, cutoffIndex + 1) + "\n" + text.substring(cutoffIndex + 1)
                } else {
                    text.substring(0, maxLength)
                }
            } else {
                text
            }
            return formattedText
        }
    }

}
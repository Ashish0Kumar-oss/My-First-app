package com.example.models

enum class PageSize {
    AUTO, A4, LETTER, LEGAL, A3
}

enum class CompressionType(val quality: Int) {
    HIGH(100), MEDIUM(75), LOW(50)
}

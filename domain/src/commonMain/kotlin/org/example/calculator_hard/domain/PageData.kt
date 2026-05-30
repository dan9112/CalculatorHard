package org.example.calculator_hard.domain

data class PageData<T>(
    val items: Iterable<T>,
    val hasNext: Boolean
)

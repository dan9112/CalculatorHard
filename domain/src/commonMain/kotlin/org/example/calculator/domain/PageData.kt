package org.example.calculator.domain

data class PageData<T>(
    val items: Iterable<T>,
    val hasNext: Boolean,
)

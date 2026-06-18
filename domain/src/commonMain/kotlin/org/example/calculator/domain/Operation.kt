package org.example.calculator.domain

enum class Operation(val charSymbol: Char) {
    Plus(charSymbol = '+'),
    Minus(charSymbol = '-'),
    Mult(charSymbol = '*'),
    Div(charSymbol = '/')
}

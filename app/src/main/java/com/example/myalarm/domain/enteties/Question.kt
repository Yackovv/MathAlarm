package com.example.myalarm.domain.enteties

data class Question(
    val num1: Int,
    val num2: Int,
    val num3: Int,
    val answer: Int,
    val action1: String,
    val action2: String
){
    val example
        get() = "$num1 $action1 $num2 $action2 $num3 = ?"
}
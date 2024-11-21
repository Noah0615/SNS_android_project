package com.example.testtest
import java.util.*
import java.io.Console
import java.lang.NumberFormatException

fun main(){
    println("======배열======")
    var list1 : Array<Int> = arrayOf(1,2,3,4,5)
    var list2 : List<Int> = listOf(1,2,3,4,5)
    var list3 : MutableList<Int> = mutableListOf(1,2,3,4,5)

    list1 = list1.plus(10)
    list2 = list2.plus(10)
    list3.add(10)

    println("List3 : ${Arrays.toString(list1)}")
    println("List2 : $list2")
    println("List3 : $list3")

    val filteredList1 = list1.filter{it%3==0}
    val filteredList2 = list2.filter{it%3==0}
    val filterList3 = list3.filter{it%3==0}

}
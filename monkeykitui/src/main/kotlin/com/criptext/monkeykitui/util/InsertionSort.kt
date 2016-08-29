package com.criptext.monkeykitui.util

import java.util.*

/**
 * Created by gesuwall on 8/29/16.
 */

class InsertionSort<T>(val list: ArrayList<T>, val comparator: Comparator<T>) {
    var startPoint = 1

    constructor(list: ArrayList<T>, cmp: Comparator<T>, startPoint: Int): this(list, cmp){
        this.startPoint = startPoint
    }

    fun sort(){
        val upperLimit = list.size - 1
        for(i in startPoint..upperLimit){
            val temp = list[i]
            var j = i - 1
            while(j > -1 && comparator.compare(temp, list[j]) == -1){
                list[j + 1] = list[j]
                j--
            }
            list[j + 1] = temp
        }
    }

    fun sortBackwards(){
        val size = list.size
        for(i in startPoint downTo 0){
            val temp = list[i]
            var j = i + 1
            while(j < size && comparator.compare(temp, list[j]) == 1){
                list[j - 1] = list[j]
                j++
            }
            list[j - 1] = temp
        }
    }
}
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

   fun insertAtCorrectPosition(item: T, insertAtEnd: Boolean): Int{
        if(list.isEmpty()){
            list.add(item)
            return 0
        }

        if(insertAtEnd) {
            var actualPosition = list.size
            while (actualPosition > 0 && comparator.compare(list[actualPosition - 1], item) == 1)
                actualPosition--
            actualPosition = Math.max(0, actualPosition)
            list.add(actualPosition, item);
            return actualPosition
        } else { //insert at position 0

            if(comparator.compare(item, list.first()) == -1){
                list.add(0, item)
                return 0
            }

            var actualPosition = 0
            while (actualPosition < list.size && comparator.compare(list[actualPosition], item) == -1)
                actualPosition++
            actualPosition = Math.min(list.size, actualPosition)
            list.add(actualPosition, item);
            return actualPosition
        }
    }
}
package com.sunshine.freeform.ui.freeform

import android.content.ComponentName

/**
 * @author sunshine
 * @date 2021/3/18
 * 为了满足单一性栈，采用集合特性来实现栈
 */
class StackSet {

    private val elementData = ArrayList<FreeformViewAbs>()

    /**
     * 将元素放到栈顶，但同时保证单一性
     * 时间复杂度O(n)
     */
    fun push(element: FreeformViewAbs) {
        elementData.remove(element)
        elementData.add(element)
    }

    fun pop(): FreeformViewAbs {
        return elementData.removeAt(elementData.size - 1)
    }

    fun peek(): FreeformViewAbs {
        return elementData[elementData.size - 1]
    }

    fun remove(element: FreeformViewAbs) {
        elementData.remove(element)
    }

    fun clean() {
        elementData.forEach {
            it.destroy()
            remove(it)
        }
    }

    fun size(): Int {
        return elementData.size
    }

    fun get(index: Int): FreeformViewAbs {
        return elementData[index]
    }

    fun getByComponentName(componentName: ComponentName, userId: Int): FreeformViewAbs? {
        elementData.forEach {
            if (it.config.componentName!! == componentName && it.config.userId == userId) {
                return it
            }
        }
        return null
    }
}
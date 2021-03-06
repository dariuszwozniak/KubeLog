package com.payu.kube.log.controller

import com.payu.kube.log.util.ViewUtils.toggleClass
import javafx.beans.value.ObservableValue
import javafx.scene.control.ListCell
import javafx.scene.text.Text
import javafx.scene.text.TextFlow


class LogEntryCell(
    private var isWrappingProperty: ObservableValue<Boolean>,
    private var searchedText: ObservableValue<String>
) : ListCell<String?>() {

    private val logEntryClass = "log-entry"
    private val logEntrySearchedClass = "searched"
    private val textClass = "text"
    private val markedTextClass = "marked"

    private val textFlow = TextFlow()

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        text = null
        if (graphic == null) {
            graphic = textFlow
        }

        if (isWrappingProperty.value) {
            minWidth = 0.0
            maxWidthProperty().bind(listView.widthProperty().subtract(20))
            prefWidthProperty().bind(listView.widthProperty().subtract(20))
        } else {
            minWidth = 0.0
            maxWidthProperty().unbind()
            prefWidthProperty().unbind()
            prefWidth = USE_COMPUTED_SIZE
            maxWidth = Double.MAX_VALUE
        }

        toggleClass(logEntryClass, true)

        val realSearchText = searchedText.value
        if (realSearchText.isNotBlank() && item != null && realSearchText in item) {
            toggleClass(logEntrySearchedClass, true)
            textFlow.children.setAll(
                createTextWithMarks(item, realSearchText)
            )
        } else {
            toggleClass(logEntrySearchedClass, false)

            textFlow.children.setAll(
                Text(item ?: "").also { it.toggleClass(textClass, true) }
            )
        }
    }

    private fun createTextWithMarks(item: String, searchedText: String): List<Text> {
        val allIndexes = findAllIndexes(item, searchedText)
        val regions = allIndexes.map { it to it + searchedText.length}
        return createTextWithMarks(item, regions)
    }

    private fun findAllIndexes(textString: String, word: String): List<Int> {
        val indexes = mutableListOf<Int>()
        var wordLength = 0
        var index = 0
        while (index != -1) {
            index = textString.indexOf(word, index + wordLength)
            if (index != -1) {
                indexes.add(index)
            }
            wordLength = word.length
        }
        return indexes
    }

    private fun createTextWithMarks(item: String, regions: List<Pair<Int, Int>>): List<Text> {
        val list = mutableListOf<Text>()
        var index = 0
        for ((from, to) in regions) {
            if (index < from) {
                val subtext = item.substring(index, from)
                list.add(Text(subtext).also { it.toggleClass(textClass, true) })
            }
            val subtext = item.substring(from, to)
            list.add(Text(subtext).also {
                it.toggleClass(textClass, true)
                it.toggleClass(markedTextClass, true)
            })
            index = to
        }
        if (index < item.length) {
            val subtext = item.substring(index)
            list.add(Text(subtext).also { it.toggleClass(textClass, true) })
        }
        return list
    }
}
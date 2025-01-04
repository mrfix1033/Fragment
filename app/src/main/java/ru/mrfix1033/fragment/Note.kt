package ru.mrfix1033.fragment

/**
 * @param doneTime -1 if isn't done else milliseconds since 1970
 */
class Note(var id: Int, var text: String, var doneTime: Long) {
    fun isDone() = doneTime != -1L
}
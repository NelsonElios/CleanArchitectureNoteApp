package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util

sealed class NoteOrder(var orderType: OrderType) {

    class Title(orderType: OrderType) : NoteOrder(orderType)
    class Date(orderType: OrderType) : NoteOrder(orderType)
    class Color(orderType: OrderType) : NoteOrder(orderType)

    // NAL_QUESTIONS = Can I create a companion object there ? --> YES WE CAN
    fun copy(orderType: OrderType): NoteOrder = when (this) {
        is Title -> Title(orderType)
        is Date -> Date(orderType)
        is Color -> Color(orderType)
    }

    companion object {
        fun copyy(noteOrder: NoteOrder, orderType: OrderType): NoteOrder = when (noteOrder) {
            is Title -> Title(orderType)
            is Date -> Date(orderType)
            is Color -> Color(orderType)
        }
    }
}

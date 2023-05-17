package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NotesUseCases
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NotesUseCases
) : ViewModel() {


    // NAL_QUESTIONS = Can I put this alternatives ? ----> YES WE CAN.
    // StateFlow is better
    private val _stateFlow = MutableStateFlow(NotesState())
    val stateFlow = _stateFlow.asStateFlow()

    /*private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state*/

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (stateFlow.value::class == event.noteOrder::class && stateFlow.value.noteOrder.orderType == event.noteOrder.orderType) {
                    return
                }

                getNotes(event.noteOrder)
            }

            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }

            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }

            is NotesEvent.ToggleOrderSection -> {
                /*_state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )*/

                _stateFlow.update { it.copy(isOrderSectionVisible = !it.isOrderSectionVisible) }
            }


        }
    }

    // NAL_QUESTIONS : Can I replace with the traditional viewModel.launch {} ? ----> YES WE CAN
    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrder).onEach { notes ->
            // _state.value = state.value.copy(notes = notes, noteOrder = noteOrder)
            _stateFlow.update { it.copy(notes = notes, noteOrder = noteOrder) }
        }.launchIn(viewModelScope)

       /*getNotesJob = viewModelScope.launch {
            noteUseCases.getNotes(noteOrder).collect{notes ->
                _stateFlow.update { it.copy(notes = notes, noteOrder = noteOrder) }
            }
        }*/

    }
}
package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidateNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNoteUseCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NotesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NotesUseCases,
    saveStateHandle: SavedStateHandle
) : ViewModel() {

    sealed class UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }

    private var currenNoteId: Int? = null

    private val _titleState = mutableStateOf(NoteTextFieldState(hint = "Enter title..."))
    val titleState: State<NoteTextFieldState> = _titleState

    private val _contentState = mutableStateOf(NoteTextFieldState())
    val contentState: State<NoteTextFieldState> = _contentState

    private val _colorState = mutableStateOf<Int>(Note.notesColors.random().toArgb())
    val colorState: State<Int> = _colorState
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        saveStateHandle.get<Int>("noteId")?.let {
            if (it != -1) {
                viewModelScope.launch {
                    noteUseCases.getNoteById(it)?.also { note ->
                        currenNoteId = note.id
                        _titleState.value = titleState.value.copy(
                            text = note.content,
                            isHintVisible = false,
                        )
                        _colorState.value = note.color
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _titleState.value = titleState.value.copy(text = event.value)
            }

            is AddEditNoteEvent.ChangeTitleFocus -> {
                _titleState.value =
                    titleState.value.copy(
                        isHintVisible = !event.focusState.isFocused && titleState.value.text.isBlank()
                    )
            }

            is AddEditNoteEvent.EnteredContent -> {
                _contentState.value = contentState.value.copy(text = event.value)
            }

            is AddEditNoteEvent.ChangeContentFocus -> {
                _contentState.value =
                    contentState.value.copy(
                        isHintVisible = !event.focusState.isFocused && contentState.value.text.isBlank()
                    )
            }

            is AddEditNoteEvent.ChangeColor -> {
                _colorState.value = event.color
            }

            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = titleState.value.text,
                                content = contentState.value.text,
                                timeStamp = System.currentTimeMillis(),
                                color = colorState.value,
                                id = currenNoteId

                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: InvalidateNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
        }
    }


}


package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case


// There we put all our uses Cases so we can have and call them from the same location.
// This class will be injected in the viewModel
data class NotesUseCases(
    val getNotes: GetNoteUseCase,
    val deleteNote: DeleteNoteUserCase,
    val addNote: AddNote,
    val getNoteById: GetNote
)

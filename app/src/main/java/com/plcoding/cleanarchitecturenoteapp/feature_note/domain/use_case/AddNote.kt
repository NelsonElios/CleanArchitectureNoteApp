package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidateNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import kotlin.jvm.Throws

class AddNote(private val repository: NoteRepository) {

    @Throws(InvalidateNoteException::class)
    suspend operator fun invoke(note: Note) {
        if(note.title.isBlank()) {
            throw InvalidateNoteException("The title can be empty")
        }
        if(note.content.isBlank()) {
            throw InvalidateNoteException("The content can be empty")
        }
        repository.insertNote(note)
    }


}
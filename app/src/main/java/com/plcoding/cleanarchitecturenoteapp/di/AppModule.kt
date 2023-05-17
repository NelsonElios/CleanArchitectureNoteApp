package com.plcoding.cleanarchitecturenoteapp.di

import android.app.Application
import androidx.room.Room
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source.NoteDatabase
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.repository.NoteRepositoryImpl
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.AddNote
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.DeleteNoteUserCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNote
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNoteUseCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NotesUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class) // There watch out for the Installin . it can be SingletonComposant or ViewModelComponent
object AppModule {


    // We provide the database
    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    // We provide the repository to access to database
    @Provides
    @Singleton
    fun proviteNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }


    //We provide the uses cases
    @Provides
    @Singleton
    fun provideNoteUsesCases(repository: NoteRepository): NotesUseCases = NotesUseCases(
        getNotes = GetNoteUseCase(repository),
        deleteNote = DeleteNoteUserCase(repository),
        addNote = AddNote(repository),
        getNoteById = GetNote(repository)

    )
}
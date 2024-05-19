package com.example.HW18.notes;

import com.example.HW18.users.User;
import com.example.HW18.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final UserService userService;
    private final NoteRepository repository;

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 10000;


    public CreateNoteResponse create(String name, CreateNoteRequest request) {
        Optional<CreateNoteResponse.Error> validatorError = validateCreateFields(request);

        if(validatorError.isPresent()) {
            return CreateNoteResponse.failed(validatorError.get());
        }

        User user = userService.findByUsername(name);

        Note createdNote = repository.save(Note.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build());

        return CreateNoteResponse.success(createdNote.getId());
     }

     public GetUserNotesResponse getUserNotes(String name) {
         List<Note> notes = repository.getUserNotes(name);
         return GetUserNotesResponse.success(notes);
     }

     public UpdateNoteResponse update(String name, UpdateNoteRequest request) {
        Optional<Note> optionalNote = repository.findById(request.getId());

        if(optionalNote.isEmpty()) {
            return UpdateNoteResponse.failed(UpdateNoteResponse.Error.invalidNoteId);
        }

        Note note = optionalNote.get();
        boolean isNotUserNote = isNotUserNote(name, note);

        if(isNotUserNote) {
            return UpdateNoteResponse.failed(UpdateNoteResponse.Error.insufficientPrivileges);
        }

        Optional<UpdateNoteResponse.Error> validatorError = validateUpdateFields(request);

        if (validatorError.isPresent()) {
            return UpdateNoteResponse.failed(validatorError.get());
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        repository.save(note);

        return UpdateNoteResponse.success(note);
     }

     public DeleteNoteResponse delete(String name, Long id) {
        Optional<Note> optionalNote = repository.findById(id);

        if (optionalNote.isEmpty()) {
            return DeleteNoteResponse.failed(DeleteNoteResponse.Error.invalidNoteId);
        }

        Note note = optionalNote.get();
        boolean isNotUserNote = isNotUserNote(name, note);

        if (isNotUserNote) {
            return DeleteNoteResponse.failed(DeleteNoteResponse.Error.insufficientPrivileges);
        }

        repository.delete(note);
        return DeleteNoteResponse.success();
     }

     private Optional<CreateNoteResponse.Error> validateCreateFields(CreateNoteRequest request) {
        if (Objects.isNull(request.getTitle()) || request.getTitle().length() > MAX_TITLE_LENGTH) {
            return Optional.of(CreateNoteResponse.Error.invalidTitle);
        }
        if (Objects.isNull(request.getContent()) || request.getContent().length() > MAX_CONTENT_LENGTH) {
            return Optional.of(CreateNoteResponse.Error.invalidContent);
        }

        return Optional.empty();
     }

     private Optional<UpdateNoteResponse.Error> validateUpdateFields(UpdateNoteRequest request) {
        if (Objects.nonNull(request.getTitle()) && request.getTitle().length() > MAX_TITLE_LENGTH) {
            return Optional.of(UpdateNoteResponse.Error.invalidTitleLength);
        }
        if (Objects.nonNull(request.getContent()) && request.getContent().length() > MAX_CONTENT_LENGTH) {
            return Optional.of(UpdateNoteResponse.Error.invalidContentLength);
        }

        return Optional.empty();
     }

     private boolean isNotUserNote(String username, Note note) {
        return !note.getUser().getName().equals(username);
     }
}

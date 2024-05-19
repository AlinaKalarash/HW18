package com.example.HW18.notes;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GetUserNotesResponse {
    private Error error;

    private List<Note> notes;

    public enum Error {
        ok
    }

    public static GetUserNotesResponse success(List<Note> notes) {
        return builder().error(Error.ok).notes(notes).build();
    }

    public static GetUserNotesResponse failed(Error error) {
        return builder().error(error).notes(null).build();
    }
}

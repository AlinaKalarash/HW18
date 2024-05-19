package com.example.HW18.notes;

import lombok.Data;

@Data
public class CreateNoteRequest {
    private String title;
    private String content;
}

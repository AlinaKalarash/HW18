package com.example.HW18.notes;

import lombok.Data;

@Data
public class UpdateNoteRequest {
    private Long id;
    private String title;
    private String content;
}

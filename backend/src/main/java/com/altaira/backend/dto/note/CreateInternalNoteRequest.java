package com.altaira.backend.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for create internal note in the note API. */
public class CreateInternalNoteRequest {

    /** Text or file content carried by this item. The request must supply nonblank text. */
    @NotBlank(message = "Note content is required")
    @Size(min = 2, max = 2000, message = "Note content must be between 2 and 2000 characters")
    private String content;

    /** Author. */
    @Size(max = 80, message = "Author must be at most 80 characters")
    private String author;

    /** {@return text or file content carried by this item} */
    public String getContent() { return content; }
    /**
     * Sets the text or file content carried by this item on this create internal note.
     *
     * @param content text or file content carried by this item
     */
    public void setContent(String content) { this.content = content; }

    /** {@return author} */
    public String getAuthor() { return author; }
    /**
     * Sets the author on this create internal note.
     *
     * @param author author
     */
    public void setAuthor(String author) { this.author = author; }
}

package com.abhay.notesapp.controller;

import com.abhay.notesapp.entity.Note;
import com.abhay.notesapp.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public String listNotes(Model model) {
        logger.info("Fetching all notes");
        List<Note> notes = noteService.findAll();
        model.addAttribute("notes", notes);
        return "notes/list";
    }

    @GetMapping("/new")
    public String createNoteForm(Model model) {
        logger.info("Showing create note form");
        model.addAttribute("note", new Note());
        return "notes/create";
    }

    @PostMapping
    public String createNote(@Valid Note note, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors while creating note: {}", bindingResult.getAllErrors());
            return "notes/create";
        }
        logger.info("Creating new note with title: {}", note.getTitle());
        noteService.save(note);
        return "redirect:/notes";
    }

    @GetMapping("/{id}")
    public String viewNote(@PathVariable Long id, Model model) {
        logger.info("Viewing note with id: {}", id);
        Optional<Note> note = noteService.findById(id);
        if (note.isEmpty()) {
            logger.warn("Note not found with id: {}", id);
            return "errors/404";
        }
        model.addAttribute("note", note.get());
        return "notes/view";
    }

    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable Long id, Model model) {
        logger.info("Showing edit form for note with id: {}", id);
        Optional<Note> note = noteService.findById(id);
        if (note.isEmpty()) {
            logger.warn("Note not found with id: {}", id);
            return "errors/404";
        }
        model.addAttribute("note", note.get());
        return "notes/edit";
    }

    @PostMapping("/{id}")
    public String editNote(@PathVariable Long id, @Valid Note noteDetails, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors while editing note {}: {}", id, bindingResult.getAllErrors());
            model.addAttribute("note", noteDetails);
            return "notes/edit";
        }
        Optional<Note> existingNote = noteService.findById(id);
        if (existingNote.isEmpty()) {
            logger.warn("Note not found with id: {}", id);
            return "errors/404";
        }
        Note note = existingNote.get();
        note.setTitle(noteDetails.getTitle());
        note.setMainContent(noteDetails.getMainContent());
        noteService.save(note);
        logger.info("Updated note with id: {}", id);
        return "redirect:/notes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id) {
        logger.info("Deleting note with id: {}", id);
        noteService.deleteById(id);
        return "redirect:/notes";
    }
}
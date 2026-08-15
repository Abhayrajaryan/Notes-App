package com.abhay.notesapp.repository;

import com.abhay.notesapp.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(n.mainContent) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Note> searchNotes(String keyword);

    Optional<Note> findByTitleContaining(String keyword);
}
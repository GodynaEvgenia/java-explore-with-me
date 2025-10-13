package ru.practicum.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {
    List<CompilationEvent> findByCompilation_Id(Long compilationId);

    void deleteAllByCompilation_id(Long compilationId);
}

package ru.practicum.compilation;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    public CompilationController(CompilationService selectionService) {
        this.compilationService = selectionService;
    }

    @PostMapping("/admin/compilations")
    public ResponseEntity<CompilationResponseDto> create(@RequestBody @Valid CreateCompilationDto request) {
        CompilationResponseDto resp = compilationService.createCompilation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<Void> delete(@PathVariable Long compId) {
        compilationService.deleteCompilations(compId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/compilations/{compId}")
    public ResponseEntity<CompilationResponseDto> update(@PathVariable Long compId, @RequestBody @Valid UpdateCompilationDto dto) {
        CompilationResponseDto updatedSelection = compilationService.updateSelection(compId, dto);
        return ResponseEntity.ok(updatedSelection);
    }

    @GetMapping("/compilations")
    public List<CompilationResponseDto> getSelections(
            @RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("CONTROLLER SIZE=" + size);
        List<CompilationResponseDto> compilations = compilationService.getCompilations(pinned, from, size);
        return compilations;

    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationResponseDto> getSelection(@PathVariable Long compId) {
        CompilationResponseDto cm = compilationService.getCompilationById(compId);
        return ResponseEntity.ok(cm);
    }
}

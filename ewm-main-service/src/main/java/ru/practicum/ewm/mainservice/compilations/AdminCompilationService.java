package ru.practicum.ewm.mainservice.compilations;

import ru.practicum.ewm.mainservice.compilations.dto.CompilationDto;
import ru.practicum.ewm.mainservice.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.mainservice.compilations.dto.UpdateCompilationDto;

public interface AdminCompilationService {
    CompilationDto createCompilation(NewCompilationDto inputDto);

    void deleteCompilation(Long compilationId);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto inputDto);
}

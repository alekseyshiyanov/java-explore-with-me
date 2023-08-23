package ru.practicum.ewm.mainservice.compilations;

import ru.practicum.ewm.mainservice.compilations.dto.CompilationDto;
import ru.practicum.ewm.mainservice.model.Compilations;

import java.util.List;

public interface PublicCompilationsService {
    List<CompilationDto> getCompilationList(Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long compilationId);
}

package ru.practicum.ewm.mainservice.compilations.dto;

import ru.practicum.ewm.mainservice.events.dto.EventsMapper;
import ru.practicum.ewm.mainservice.model.Compilations;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationsMapper {
    public static Compilations fromDto(NewCompilationDto dto) {
        return Compilations.builder()
                .id(null)
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static Compilations fromDto(UpdateCompilationDto dto) {
        return Compilations.builder()
                .id(null)
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationDto toDto(Compilations compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getCompData().stream().map(EventsMapper::toShortDto).collect(Collectors.toList()))
                .build();
    }

    public static List<CompilationDto> toDto(List<Compilations> compilationsList) {
        if (compilationsList == null || compilationsList.isEmpty()) {
            return Collections.emptyList();
        }

        return compilationsList.stream()
                .map(CompilationsMapper::toDto)
                .collect(Collectors.toList());
    }
}

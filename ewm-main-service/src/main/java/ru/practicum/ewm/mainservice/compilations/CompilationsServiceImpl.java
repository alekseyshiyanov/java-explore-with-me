package ru.practicum.ewm.mainservice.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mainservice.compilations.dto.CompilationDto;
import ru.practicum.ewm.mainservice.compilations.dto.CompilationsMapper;
import ru.practicum.ewm.mainservice.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.mainservice.compilations.dto.UpdateCompilationDto;
import ru.practicum.ewm.mainservice.model.CompilationArray;
import ru.practicum.ewm.mainservice.model.Compilations;
import ru.practicum.ewm.mainservice.model.Events;
import ru.practicum.ewm.mainservice.repository.CompilationArrayRepository;
import ru.practicum.ewm.mainservice.repository.CompilationsRepository;
import ru.practicum.ewm.mainservice.repository.EventsRepository;
import ru.practicum.statsclient.exceptions.ApiErrorException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompilationsServiceImpl implements PublicCompilationsService, AdminCompilationService {

    private final CompilationsRepository compilationsRepository;
    private final CompilationArrayRepository compilationArrayRepository;
    private final EventsRepository eventsRepository;

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilation(Long compilationId) {
        return CompilationsMapper.toDto(getCompilationById(compilationId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilationList(Boolean pinned, int from, int size) {
        Query query;

        if (pinned == null) {
            query = entityManager.createQuery("SELECT c FROM Compilations c ORDER BY c.id ASC", Compilations.class);
        } else {
            query = entityManager.createQuery("SELECT c FROM Compilations c WHERE c.pinned = ?1 ORDER BY c.id ASC", Compilations.class)
                    .setParameter(1, pinned);
        }
        query.setFirstResult(from);
        query.setMaxResults(size);

        List<Compilations> cl = query.getResultList();

        return CompilationsMapper.toDto(cl);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto inputDto) {
        var eventIdsList = validateIdsList(inputDto.getEvents());
        var eventList = eventsRepository.getEventsByIdIn(eventIdsList);

        if (eventList.size() != eventIdsList.size()) {
            throw sendErrorMessage(HttpStatus.BAD_REQUEST, "События для подборки отсутствуют в базе данных");
        }

        var compilation = CompilationsMapper.fromDto(inputDto);
        List<CompilationArray> newCAList = new ArrayList<>();

        for (Events event : eventList) {
            newCAList.add(new CompilationArray(null, compilation, event));
        }

        compilation.setCompData(newCAList);
        var c = compilationsRepository.save(compilation);

        return CompilationsMapper.toDto(c);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        var c = getCompilationById(compilationId);
        compilationsRepository.delete(c);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto inputDto) {
        var compilation = getCompilationById(compilationId);

        var newCompilation = CompilationsMapper.fromDto(inputDto);
        var eventIdsList = validateIdsList(inputDto.getEvents());

        if (eventIdsList != null) {
            var eventList = eventsRepository.getEventsByIdIn(eventIdsList);

            if (eventList.size() != eventIdsList.size()) {
                throw sendErrorMessage(HttpStatus.BAD_REQUEST, "События для подборки отсутствуют в базе данных");
            }

            List<CompilationArray> newCAList = new ArrayList<>();

            for (Events event : eventList) {
                newCAList.add(new CompilationArray(null, compilation, event));
            }

            compilationArrayRepository.deleteAll(compilation.getCompData());

            var ret = compilationArrayRepository.saveAll(newCAList);

            compilation.setCompData(ret);
        }

        copyNonNullProperties(newCompilation, compilation);

        var c = compilationsRepository.save(compilation);

        return CompilationsMapper.toDto(c);
    }

    private List<Long> validateIdsList(List<Long> idsList) {
        if ((idsList != null) && (!idsList.isEmpty())) {
            if (idsList.stream().anyMatch(id -> id <= 0)) {
                throw sendErrorMessage(HttpStatus.BAD_REQUEST, "Идентификаторы событий должны быть положительным числом");
            }
        }
        return idsList;
    }

    private ApiErrorException sendErrorMessage(HttpStatus httpStatus, String errMsg) {
        log.error(errMsg);
        return new ApiErrorException(httpStatus, errMsg);
    }

    private Compilations getCompilationById(Long compilationId) {
        return compilationsRepository.findById(compilationId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND, "Подборка событий с id = " + compilationId
                        + " не найдена в базе данных"));
    }

    private void copyNonNullProperties(Compilations in, Compilations out) {
        final BeanWrapper src = new BeanWrapperImpl(in);
        final BeanWrapper trg = new BeanWrapperImpl(out);

        for (final Field property : out.getClass().getDeclaredFields()) {
            Object providedObject = src.getPropertyValue(property.getName());
            if (providedObject != null && !(providedObject instanceof Collection<?>)) {
                trg.setPropertyValue(property.getName(), providedObject);
            }
        }
    }
}

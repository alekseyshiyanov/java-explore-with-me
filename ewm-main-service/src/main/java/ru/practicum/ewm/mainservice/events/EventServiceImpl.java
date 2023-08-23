package ru.practicum.ewm.mainservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mainservice.model.Categories;
import ru.practicum.ewm.mainservice.repository.CategoryRepository;
import ru.practicum.ewm.mainservice.events.dto.*;
import ru.practicum.ewm.mainservice.model.Events;
import ru.practicum.ewm.mainservice.model.Requests;
import ru.practicum.ewm.mainservice.model.Users;
import ru.practicum.ewm.mainservice.repository.EventsRepository;
import ru.practicum.ewm.mainservice.repository.RequestsRepository;
import ru.practicum.ewm.mainservice.repository.UsersRepository;
import ru.practicum.ewm.mainservice.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.mainservice.requests.dto.RequestMapper;
import ru.practicum.ewm.mainservice.requests.RequestStatus;
import ru.practicum.ewm.mainservice.statistics.StatisticsService;
import ru.practicum.statsclient.exceptions.ApiErrorException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements AdminEventService, PrivateEventsService, PublicEventService {

    private final UsersRepository usersRepository;
    private final CategoryRepository categoryRepository;
    private final EventsRepository eventsRepository;
    private final RequestsRepository requestsRepository;

    private final EntityManager entityManager;
    private final StatisticsService statisticsService;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        return EventsMapper.toFullDto(eventsRepository.save(prepareEventData(userId, eventDto)));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        return EventsMapper.toFullDto(getEventByIdAndUserId(userId, eventId));
    }

    @Override
    public List<EventShortDto> getEventsListByUser(Long userId, int from, int size) {
        checkUserExists(userId, HttpStatus.BAD_REQUEST);

        Query query = entityManager.createQuery("SELECT e FROM Events e WHERE e.user.id = ?1 ORDER BY e.id");
        query.setParameter(1, userId);
        query.setFirstResult(from);
        query.setMaxResults(size);

        return EventsMapper.toShortDto(query.getResultList());
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto) {
        if (eventDto.getEventDate() != null) {
            validateEventDate(eventDto.getEventDate(), 2L, HttpStatus.BAD_REQUEST);
        }

        var event = getEventByIdAndUserId(userId, eventId);
        var eventState = event.getState();
        var stateAction = prepareUpdateStateAction(eventDto.getStateAction());
        var dataForUpdate = EventsMapper.fromDto(eventDto);

        if(eventState == EventState.PUBLISHED) {
            throw sendErrorMessage(HttpStatus.CONFLICT, "Отменить можно только отмененные события или события в состоянии " +
                    "ожидания модерации. Текущее состояние: " + eventState.name());
        }

        switch (stateAction) {
            case CANCEL_REVIEW:
                if(eventState == EventState.PENDING) {
                    dataForUpdate.setState(EventState.CANCELED);
                } else {
                    throw sendErrorMessage(HttpStatus.CONFLICT, "Отменить можно события только в состоянии " +
                            "ожидания модерации");
                }
                break;
            case SEND_TO_REVIEW:
                if (eventState == EventState.CANCELED) {
                    dataForUpdate.setState(EventState.PENDING);
                } else {
                    throw sendErrorMessage(HttpStatus.CONFLICT, "Отправить в состоянии ожидания модерации " +
                            "можно только отмененные события.");
                }
                break;
        }

        if (eventDto.getCategory() != null) {
            dataForUpdate.setCategory(getCategoryById(eventDto.getCategory()));
        }

        copyNonNullProperties(dataForUpdate, event);

        return EventsMapper.toFullDto(eventsRepository.save(event));
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Events event = getEventById(eventId);

        if(event.getUser().getId().equals(userId)) {
            throw sendErrorMessage(HttpStatus.CONFLICT, "Инициатор события не может добавить запрос " +
                    "на участие в своём событии");
        }

        if(event.getState() != EventState.PUBLISHED) {
            throw sendErrorMessage(HttpStatus.CONFLICT, "Событие с id = " + eventId
                    + " неопубликовано. State = " + event.getState().name());
        }

        var participants = event.getConfirmedRequests();
        var participantLimit = event.getParticipantLimit();

        if((participants >= participantLimit) && (participantLimit != 0)) {
            throw sendErrorMessage(HttpStatus.CONFLICT, "Достигнут лимит запросов на участие. " +
                    "Текущий лимит = " + event.getParticipantLimit());
        }

        Users user = getUserById(userId, HttpStatus.BAD_REQUEST);

        Requests requests = Requests.builder()
                .user(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || (participantLimit == 0)) {
            requests.setStatus(RequestStatus.CONFIRMED);
        }

        var r = requestsRepository.save(requests);

        if(r.getStatus() == RequestStatus.CONFIRMED) {
            eventsRepository.increaseConfirmedRequests(eventId, 1L);
        }

        return RequestMapper.toDto(r);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        checkUserExists(userId, HttpStatus.NOT_FOUND);
        return RequestMapper.toDto(requestsRepository.findAllByUserId(userId));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        var req = requestsRepository.findByIdAndUserId(requestId, userId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND, "Запрос на участие с параметрами requestId = " + requestId +
                " и userId = " + userId + " не найден в базе данных"));

        var oldStatus = req.getStatus();

        req.setStatus(RequestStatus.CANCELED);

        var r = requestsRepository.save(req);

        if (oldStatus == RequestStatus.CONFIRMED) {
            eventsRepository.decreaseConfirmedRequests(req.getEvent().getId(), 1L);
        }

        return RequestMapper.toDto(r);
    }

    @Override
    public List<ParticipationRequestDto> getEventByUserRequest(Long userId, Long eventId) {
        var ret = requestsRepository.getRequestsByUserIdAndEventId(userId, eventId);
        return RequestMapper.toDto(ret);
    }

    @Override
    public EventRequestStatusUpdateResultDto updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto dto) {
        List<Requests> confirmedRequestsList = null;
        List<Requests> rejectedRequestsList = null;

        var requestStatus = prepareRequestStatus(dto.getStatus());
        var requestsCount = requestsRepository.checkRequestsStatus(RequestStatus.PENDING, dto.getRequestIds());
        var idsList = dto.getRequestIds();
        var requestsIdsCount = idsList.size();

        if(requestsCount != requestsIdsCount) {
            throw sendErrorMessage(HttpStatus.CONFLICT, "Не все заявки из списка имеют статус PENDING");
        }

        var event = getEventById(eventId);
        var confirmedRequests = event.getConfirmedRequests();
        var participantLimit = event.getParticipantLimit();

        if (confirmedRequests >= participantLimit) {
            throw sendErrorMessage(HttpStatus.CONFLICT, "Достигнут лимит запросов на участие. " +
                    "Текущий лимит = " + participantLimit);
        }

        Long participantsLeft = participantLimit - confirmedRequests;

        switch(requestStatus) {
            case CONFIRMED:
                    if(participantsLeft >= requestsIdsCount) {
                        confirmedRequestsList = changeRequestStatus(idsList, RequestStatus.CONFIRMED);
                        rejectedRequestsList = Collections.emptyList();

                        eventsRepository.increaseConfirmedRequests(event.getId(), (long) requestsIdsCount);
                    } else if(participantsLeft > 0) {
                        var cl = idsList.subList(0, participantsLeft.intValue());
                        var rl = idsList.subList(participantsLeft.intValue(), idsList.size());

                        confirmedRequestsList = changeRequestStatus(cl, RequestStatus.CONFIRMED);
                        rejectedRequestsList = changeRequestStatus(rl, RequestStatus.REJECTED);

                        eventsRepository.increaseConfirmedRequests(event.getId(), (long) cl.size());
                    } else {
                        rejectedRequestsList = changeRequestStatus(idsList, RequestStatus.REJECTED);
                        confirmedRequestsList = Collections.emptyList();
                    }
                break;
            case REJECTED:
                Long adder = (event.getConfirmedRequests() >= requestsIdsCount) ? Long.valueOf(requestsIdsCount) : event.getConfirmedRequests();

                rejectedRequestsList = changeRequestStatus(idsList, RequestStatus.REJECTED);
                confirmedRequestsList = Collections.emptyList();

                eventsRepository.decreaseConfirmedRequests(event.getId(), adder);
                break;
        }

        return RequestMapper.toDto(confirmedRequestsList, rejectedRequestsList);
    }

    private List<Requests> changeRequestStatus(List<Long> idsList, RequestStatus newRequestStatus) {
        requestsRepository.updateRequestsStatus(newRequestStatus, idsList);
        return requestsRepository.findAllByIdIsIn(idsList);
    }

    @Override
    public List<Events> getFilteredEventList(List<Long> usersList, List<String> statesList,
                                                   List<Long> categoriesList, String rangeStart, String rangeEnd,
                                                   int from, int size) {
        int paramIndex = 1;
        boolean whereFlag = true;

        StringBuilder queryString = new StringBuilder("SELECT e FROM Events e");

        var userIdsList = prepareIdsList(usersList);
        var esList = prepareEventStateList(statesList);
        var categoryIdsList = prepareIdsList(categoriesList);
        var startTime = prepareTime(rangeStart);
        var endTime = prepareTime(rangeEnd);

        if (userIdsList != null) {
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" e.user.id IN ?");
            queryString.append(paramIndex++);
            whereFlag = false;
        }

        if (esList != null) {
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" e.state IN ?");
            queryString.append(paramIndex++);
            whereFlag = false;
        }

        if (categoryIdsList != null) {
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" e.category.id IN ?");
            queryString.append(paramIndex++);
            whereFlag = false;
        }

        if((startTime != null) && (endTime != null)) {
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" e.eventDate BETWEEN ?");
            queryString.append(paramIndex++);
            queryString.append(" AND ?");
            queryString.append(paramIndex);
        }

        queryString.append(" order by id");

        Query query = entityManager.createQuery(queryString.toString());

        paramIndex = 1;

        if (userIdsList != null) {
            validateIdsList(userIdsList);
            query.setParameter(paramIndex++, userIdsList);
        }

        if (esList != null) {
            query.setParameter(paramIndex++, esList);
        }

        if (categoryIdsList != null) {
            validateIdsList(categoryIdsList);
            query.setParameter(paramIndex++, categoriesList);
        }

        if ((startTime != null) && (endTime != null)) {
            query.setParameter(paramIndex++, startTime);
            query.setParameter(paramIndex, endTime);
        }

        query.setFirstResult(from);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public Events updateEventDataAndStatus(Long eventId, UpdateEventDto eventDto) {
        var event = getEventById(eventId);
        var stateAction = prepareStateAction(eventDto.getStateAction());
        var newEventData = EventsMapper.fromDto(eventDto);
        var eventDate = event.getEventDate();
        var publishedDate = event.getPublishedOn();

        copyNonNullProperties(newEventData, event);

        switch (stateAction) {
            case NONE_EVENT:
                validateEventDate(event.getEventDate(), 1L, HttpStatus.BAD_REQUEST);
                if ((event.getState() == EventState.PUBLISHED) && eventDate.isBefore(publishedDate.plusHours(1))) {
                    throw sendErrorMessage(HttpStatus.CONFLICT, "От времени публикации до начала события менее 1 часа. ");
                }
                break;
            case PUBLISH_EVENT:
                if (event.getState() == EventState.PENDING) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    validateEventDate(event.getEventDate(), 1L, HttpStatus.CONFLICT);
                } else {
                    throw sendErrorMessage(HttpStatus.CONFLICT, "Событие с id = " + eventId
                            + " находится не в состоянии PENDING. " +
                            "Текущее состояние: " + event.getState().name());
                }
                break;
            case REJECT_EVENT:
                if (event.getState() != EventState.PUBLISHED) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw sendErrorMessage(HttpStatus.CONFLICT, "Событие с id = " + eventId
                            + " находится в состоянии PUBLISHED.");
                }
                break;
        }

        return eventsRepository.save(event);
    }

    @Override
    public List<Events> getFilteredEventList(String searchString, List<Long> categoriesList,
                                             Boolean paid, String rangeStart, String rangeEnd,
                                             Boolean onlyAvailable, String sort, int from, int size) {
        int paramIndex = 1;
        boolean whereFlag = true;

        StringBuilder queryString = new StringBuilder("SELECT * FROM Events e");

        var categoryIdsList = prepareIdsList(categoriesList);
        var startTime = prepareTime(rangeStart);
        var endTime = prepareTime(rangeEnd);
        var sortType = prepareSortType(sort);

        if(onlyAvailable == null) {
            onlyAvailable = false;
        }

        if((searchString != null) && (!searchString.isBlank())) {
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" (setweight(to_tsvector(e.annotation),'A')" +
                    " || setweight(to_tsvector(e.description), 'B')) @@ plainto_tsquery('");
            queryString.append(searchString);
            queryString.append("')");
            whereFlag = false;
        }

        if (categoryIdsList != null) {
            validateIdsList(categoryIdsList);
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" e.category_id IN ?");
            queryString.append(paramIndex++);
            whereFlag = false;
        }

        queryString.append(buildWhereString(whereFlag));
        whereFlag = false;
        if((startTime != null) && (endTime != null)) {
            queryString.append(" e.event_date BETWEEN ?");
            queryString.append(paramIndex++);
            queryString.append(" AND ?");
            queryString.append(paramIndex++);
        } else {
            queryString.append(" e.event_date > ?");
            queryString.append(paramIndex++);
        }

        if(paid != null) {
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" e.paid = ?");
            queryString.append(paramIndex);
        }

        if(onlyAvailable) {
            queryString.append(buildWhereString(whereFlag));
            queryString.append(" e.participant_limit > e.confirmed_requests");
        }

        switch(sortType) {
            case ID:
                queryString.append(" order by e.id");
                break;
            case EVENT_DATE:
                queryString.append(" order by e.event_date");
                break;
            case VIEWS:
                queryString.append(" order by e.views");
                break;
        }

        Query query = entityManager.createNativeQuery(queryString.toString(), Events.class);

        paramIndex = 1;

        if (categoryIdsList != null) {
            query.setParameter(paramIndex++, categoriesList);
        }

        if ((startTime != null) && (endTime != null)) {
            query.setParameter(paramIndex++, startTime);
            query.setParameter(paramIndex++, endTime);
        } else {
            query.setParameter(paramIndex++, LocalDateTime.now());
        }

        if(paid != null) {
            query.setParameter(paramIndex, paid);
        }

        query.setFirstResult(from);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public Events getPublihedEventsById(Long eventId) {
        var ret = eventsRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND, "Информация об опубликованном событии с id = " + eventId
                        + " не найдена в базе данных"));

        var views = statisticsService.getEventViewFromUniqueIpAddress(eventId);
        if (views != null) {
            ret.setViews(views);
            return eventsRepository.save(ret);
        }
        return ret;
    }

    private void validateIdsList(List<Long> idsList) {
        for(Long id : idsList) {
            if(id <= 0) {
                throw sendErrorMessage(HttpStatus.BAD_REQUEST, "Идентификатор должен быть больше 0. Value: " + id);
            }
        }
    }

    private String buildWhereString(boolean flag) {
        return (flag) ? " WHERE" : " AND";
    }

    private LocalDateTime prepareTime(String time) {
        if((time == null) || (time.trim().isBlank())) {
            return null;
        }

        try {
            return LocalDateTime.parse(time.trim(), dtf);
        } catch (DateTimeParseException e) {
            throw sendErrorMessage(HttpStatus.BAD_REQUEST, "Неверный формат строки времени. Текущая строка: " + time);
        }
    }

    private List<Long> prepareIdsList(List<Long> idsList) {
        if ((idsList == null) || (idsList.isEmpty())) {
            return null;
        }
        return idsList;
    }

    private List<EventState> prepareEventStateList(List<String> statesList) {
        if ((statesList == null) || (statesList.isEmpty())) {
            return null;
        }

        List<EventState> esl = new ArrayList<>();

        for(String s : statesList) {
            var es = EventState.from(s).orElseThrow(() ->
                    sendErrorMessage(HttpStatus.BAD_REQUEST, "Неверный код статуса события. EventState: " + s));
            esl.add(es);
        }

        return esl;
    }

    private EventSortTypes prepareSortType(String sortType) {
        if ((sortType == null) || (sortType.isEmpty())) {
            return EventSortTypes.ID;
        }

        return EventSortTypes.from(sortType).orElseThrow(() ->
                sendErrorMessage(HttpStatus.BAD_REQUEST, "Неверный код типа сортировки. " +
                        "SortType: " + sortType));
    }

    private RequestStatus prepareRequestStatus(String requestStatus) {
        return RequestStatus.from(requestStatus).orElseThrow(() ->
                sendErrorMessage(HttpStatus.BAD_REQUEST, "Неизвестный статус запроса на участие. " +
                        "Status = " + requestStatus));
    }

    private EventStateAction prepareStateAction(String stateAction) {
        if ((stateAction == null) || (stateAction.isEmpty())) {
            return EventStateAction.NONE_EVENT;
        }

        return EventStateAction.from(stateAction).orElseThrow(() ->
                sendErrorMessage(HttpStatus.BAD_REQUEST, "Неизвестный StateAction. " +
                        "Текущий StateAction: " + stateAction));
    }

    private UpdateEventStateAction prepareUpdateStateAction(String stateAction) {
        if ((stateAction == null) || (stateAction.isEmpty())) {
            return UpdateEventStateAction.NONE_EVENT;
        }

        return UpdateEventStateAction.from(stateAction).orElseThrow(() ->
                sendErrorMessage(HttpStatus.BAD_REQUEST, "Неизвестный UpdateEventStateAction. " +
                        "Текущий UpdateEventStateAction: " + stateAction));
    }

    private Events prepareEventData(Long userId, NewEventDto eventDto) {
        validateEventDate(eventDto.getEventDate(), 2L, HttpStatus.BAD_REQUEST);

        var user = getUserById(userId, HttpStatus.BAD_REQUEST);
        var category = getCategoryById(eventDto.getCategory());
        var event = EventsMapper.fromDto(eventDto);

        event.setUser(user);
        event.setCategory(category);

        return event;
    }

    private void checkUserExists(Long userId, HttpStatus httpStatus) {
        if(!usersRepository.existsById(userId)) {
            throw sendErrorMessage(httpStatus, "Пользователь с id = " + userId + " не найден в базе данных");
        }
    }

    private Events getEventByIdAndUserId(Long userId, Long eventId) {
        return eventsRepository.findByIdAndUserId(eventId, userId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.BAD_REQUEST, "Информация о событии с параметрами userId = " + userId
                        + " и eventId = " + eventId + " не найдена в базе данных"));
    }

    private Events getEventById(Long eventId) {
        return eventsRepository.findById(eventId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND, "Информация о событии с Id = " + eventId
                        + " не найдена в базе данных"));
    }

    private void validateEventDate(LocalDateTime eventDate, long hours, HttpStatus httpStatus) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
            throw sendErrorMessage(httpStatus, "Поле 'eventDate' должно содержать дату, которая еще не наступила. " +
                    "Текущее значение: " + eventDate);
        }
    }

    private Users getUserById(Long userId, HttpStatus httpStatus) {
        return usersRepository.findById(userId).orElseThrow(() ->
                sendErrorMessage(httpStatus, "Пользователь с id = " + userId + " не найден в базе данных"));
    }

    private Categories getCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.BAD_REQUEST, "Категория события с id = " + catId
                        + " не найдена в базе данных"));
    }

    private ApiErrorException sendErrorMessage(HttpStatus httpStatus, String errMsg) {
        log.error(errMsg);
        return new ApiErrorException(httpStatus, errMsg);
    }

    private void copyNonNullProperties(Events in, Events out) {
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

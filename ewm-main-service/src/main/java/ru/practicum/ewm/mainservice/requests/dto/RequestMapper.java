package ru.practicum.ewm.mainservice.requests.dto;

import ru.practicum.ewm.mainservice.events.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.mainservice.model.Requests;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static List<ParticipationRequestDto> toDto(List<Requests> requestsList) {
        if (requestsList == null) {
            return Collections.emptyList();
        }

        return requestsList.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public static ParticipationRequestDto toDto(Requests requests) {
        if (requests == null) {
            return null;
        }

        return ParticipationRequestDto.builder()
                .id(requests.getId())
                .created(requests.getCreated())
                .event(requests.getEvent().getId())
                .requester(requests.getUser().getId())
                .status(requests.getStatus().name())
                .build();
    }

    public static EventRequestStatusUpdateResultDto toDto(List<Requests> confirmedRequestsList, List<Requests> rejectedRequestsList) {
        return EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(confirmedRequestsList == null ? Collections.emptyList() : RequestMapper.toDto(confirmedRequestsList))
                .rejectedRequests(rejectedRequestsList == null ? Collections.emptyList() : RequestMapper.toDto(rejectedRequestsList))
                .build();
    }
}

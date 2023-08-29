package ru.practicum.ewm.mainservice.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ranking {
    private Events event;
    private Double ranking;
    private Long likes;
    private Long positive;
}

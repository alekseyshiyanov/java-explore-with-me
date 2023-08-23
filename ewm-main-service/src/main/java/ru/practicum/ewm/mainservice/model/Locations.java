package ru.practicum.ewm.mainservice.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Locations {
    private Double lat;
    private Double lon;
}

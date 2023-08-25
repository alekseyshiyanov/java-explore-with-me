package ru.practicum.ewm.mainservice.model;

import lombok.*;
import ru.practicum.ewm.mainservice.events.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "published", nullable = false)
    private LocalDateTime publishedOn;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "requestModeration", nullable = false)
    private Boolean requestModeration;

    @Enumerated
    @Column(name = "state", nullable = false)
    private EventState state;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "views")
    private Long views;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}

package ru.practicum.ewm.mainservice.model;

import lombok.*;
import ru.practicum.ewm.mainservice.requests.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Requests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Events event;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Enumerated
    @Column(name = "status", nullable = false)
    private RequestStatus status;
}

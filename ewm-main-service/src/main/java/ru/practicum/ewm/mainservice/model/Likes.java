package ru.practicum.ewm.mainservice.model;

import lombok.*;
import ru.practicum.ewm.mainservice.ranking.LikeState;

import javax.persistence.*;

@Entity
@Table(name = "likes", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Events event;

    @Enumerated
    @Column(name = "grade", nullable = false)
    private LikeState grade;
}

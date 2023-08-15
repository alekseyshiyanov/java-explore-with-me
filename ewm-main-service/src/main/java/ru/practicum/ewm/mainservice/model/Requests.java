package ru.practicum.ewm.mainservice.model;

import lombok.*;

import javax.persistence.*;

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
}

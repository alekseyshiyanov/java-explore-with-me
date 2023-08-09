package ru.practicum.ewm.stats.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "app")
    private Apps app;

    @Column(name = "uri")
    private String uri;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created")
    private LocalDateTime created;
}

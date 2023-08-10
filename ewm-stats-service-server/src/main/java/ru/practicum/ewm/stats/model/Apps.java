package ru.practicum.ewm.stats.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "apps", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Apps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}

package ru.practicum.ewm.mainservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compilations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "pinned", nullable = false)
    Boolean pinned;

    @Column(name = "title", nullable = false)
    String title;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "compilation")
    List<CompilationArray> compData;
}

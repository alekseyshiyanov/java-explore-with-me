package ru.practicum.ewm.mainservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "compilation_array", schema = "public")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationArray {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "compilation_id")
    Compilations compilation;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Events events;
}

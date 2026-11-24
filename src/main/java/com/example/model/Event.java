package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "events")
@SoftDelete
public class Event {

    // uuid v7 hibernate 7 style
//    @Id
//    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
//    private UUID id;

    // uuid v4 jpa style
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(name = "eventDate")
    private LocalDateTime date;

    @Version
    private Long version;

    public Event(String title, LocalDateTime date) {
        // for application use, to create new events
        this.title = title;
        this.date = date;
    }
}

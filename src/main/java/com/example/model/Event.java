package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Events")
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(name = "eventDate")
    private LocalDateTime date;

    public Event(String title, LocalDateTime date) {
        // for application use, to create new events
        this.title = title;
        this.date = date;
    }
}

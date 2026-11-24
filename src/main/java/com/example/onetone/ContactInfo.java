package com.example.onetone;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    private Long id;
    private String phoneNumber;
    private String address;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id") // comment it to show warning message by setting details to null
    @OnDelete(action = OnDeleteAction.CASCADE) // mirrors the DB's ON DELETE CASCADE
    private UserProfile userProfile;

}
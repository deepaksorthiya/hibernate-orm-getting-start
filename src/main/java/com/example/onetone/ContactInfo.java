package com.example.onetone;

import jakarta.persistence.*;
import lombok.*;

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
    private UserProfile userProfile;

}
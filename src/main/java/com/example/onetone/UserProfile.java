package com.example.onetone;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class UserProfile {

    @Id
    @GeneratedValue // comment this if you want to set ID manually
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @ToString.Exclude
    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private ContactInfo contactInfo;

    public void setContactDetails(ContactInfo details) {
        if (details == null) {
            if (this.contactInfo != null) {
                this.contactInfo.setUserProfile(null);
            }
        } else {
            details.setUserProfile(this);
        }
        this.contactInfo = details;
    }

}

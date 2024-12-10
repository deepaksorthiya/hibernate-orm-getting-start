package com.example.manytomany;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "Person")
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    private String registrationNumber;

    @ToString.Exclude
    @OneToMany(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PersonAddress> addresses = new ArrayList<>();

    public Person(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    //Getters and setters are omitted for brevity

    public void addAddress(Address address) {
        PersonAddress personAddress = new PersonAddress(this, address);
        addresses.add(personAddress);
        address.getOwners().add(personAddress);
    }

    public void removeAddress(Address address) {
        PersonAddress personAddress = new PersonAddress(this, address);
        address.getOwners().remove(personAddress);
        addresses.remove(personAddress);
        personAddress.setPerson(null);
        personAddress.setAddress(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return Objects.equals(registrationNumber, person.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationNumber);
    }
}

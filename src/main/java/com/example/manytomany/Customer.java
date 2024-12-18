package com.example.manytomany;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "customer")
@SoftDelete
public class Customer {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ToString.Exclude
    @ManyToMany
    //@ManyToMany(mappedBy = "customers", cascade = CascadeType.ALL)
    private List<Phone> phones = new ArrayList<>();

    public Customer(String name) {
        this.name = name;
    }

    public void addPhone(Phone phone) {
        this.phones.add(phone);
        phone.getCustomers().add(this);
    }

    public void removePhone(Phone phone) {
        this.phones.remove(phone);
        phone.getCustomers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer person = (Customer) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

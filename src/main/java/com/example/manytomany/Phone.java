package com.example.manytomany;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "phone")
@SoftDelete
public class Phone implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String number;
    private String type;

    @ToString.Exclude
    @ManyToMany(mappedBy = "phones", cascade = CascadeType.ALL)
    //@ManyToMany
    private List<Customer> customers = new ArrayList<>();

    public Phone(String number, String type) {
        this.number = number;
        this.type = type;
    }

    public void addCustomer(Customer customer) {
        this.customers.add(customer);
        customer.getPhones().add(this);
    }

    public void removeCustomer(Customer customer) {
        this.customers.remove(customer);
        customer.getPhones().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Phone address = (Phone) o;
        return Objects.equals(id, address.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
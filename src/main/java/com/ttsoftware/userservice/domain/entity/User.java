package com.ttsoftware.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "USERS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "SURNAME", length = 100)
    private String surname;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "USERNAME", length = 100)
    private String username;

    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "PHONE", length = 15)
    private String phone;

    @Column(name = "PHONE_NUMBER", length = 15)
    private String phoneNumber;

    @Column(name = "COUNTRY", length = 50)
    private String country;

    @Column(name = "CITY", length = 50)
    private String city;

    @Column(name = "ADDRESS", length = 250)
    private String address;

    @Column(name = "DEALER_NAME", length = 100)
    private String dealerName;

    @Column(name = "TAX_NUMBER", length = 100)
    private String taxNumber;

    @Column(name = "TAX_OFFICES", length = 100)
    private String taxOffice;

    @Column(name = "DEALER", length = 100)
    private boolean isDealer;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "IS_DELETED")
    private Integer isDeleted;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    @Generated
    private void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.isDeleted = 0;
    }
}
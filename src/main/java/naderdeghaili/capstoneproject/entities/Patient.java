package naderdeghaili.capstoneproject.entities;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class Patient {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String fiscalCode;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = true)
    private String address;

    @Column(nullable = false)
    private Boolean emailConsent;

    private String photoUrl;
    private LocalDateTime createdAt;

    public Patient() {
    }

    public Patient(String firstName, String lastName, LocalDate birthDate, String fiscalCode, String email, String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.fiscalCode = fiscalCode;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.emailConsent = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean isEmailConsent() {
        return emailConsent;
    }

    public void setEmailConsent(Boolean emailConsent) {
        this.emailConsent = emailConsent;
    }

    @Override
    public String toString() {
        return "Patient: " +
                "id: " + id +
                " | firstName: " + firstName +
                " | lastName: " + lastName +
                " | birthDate: " + birthDate +
                " | fiscalCode: " + fiscalCode +
                " | email: " + email +
                " | phone: " + phone +
                " | address: " + address +
                " | photoUrl: " + photoUrl +
                " | emailConsent: " + emailConsent
                ;
    }
}

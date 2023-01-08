package com.cry.DeliveryChain.Entity;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity(name = "Supplier")
@Table(name = "Supplier")
public class Supplier {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "Name")
    public String Name;

    @Column(name = "Username")
    public String Username;

    @Column(name = "Password")
    public String Password;

    @Column(name = "CreationDate")
    public LocalDate CreationDate;

    @Column(name = "ProfilePhoto")
    public String ProfilePhoto;

    @Column(name = "IsActive")
    public Boolean IsActive;

    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public Supplier() {}

    public Supplier(String Name, String Username, String Password, LocalDate CreationDate,
        String ProfilePhoto, Boolean IsActive, UUID UniqueId) {
        this.Name = Name;
        this.Username = Username;
        this.Password = Password;
        this.CreationDate = CreationDate;
        this.ProfilePhoto = ProfilePhoto;
        this.IsActive = IsActive;
        this.UniqueId = UniqueId;
    }
}

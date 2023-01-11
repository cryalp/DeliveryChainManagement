package com.cry.DeliveryChain.Entity;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "UserAccount")
@Table(name = "UserAccount")
public class UserAccount {
    @JsonIgnore
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "Email")
    public String Email;

    @Column(name = "Username")
    public String Username;

    @JsonIgnore
    @Column(name = "Password")
    public String Password;

    @JsonIgnore
    @Column(name = "AccountType")
    public String AccountType;

    @JsonIgnore
    @Column(name = "CreationDate")
    public LocalDateTime CreationDate;

    @JsonIgnore
    @Column(name = "Photo")
    public String Photo;

    @JsonIgnore
    @Column(name = "IsActive")
    public Boolean IsActive;

    @JsonIgnore
    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public UserAccount() {}

    public UserAccount(String Email, String Username, String Password, String AccountType, LocalDateTime CreationDate, String Photo, Boolean IsActive, UUID UniqueId) {
        this.Email = Email;
        this.Username = Username;
        this.Password = Password;
        this.AccountType = AccountType;
        this.CreationDate = CreationDate;
        this.Photo = Photo;
        this.IsActive = IsActive;
        this.UniqueId = UniqueId;
    }
}

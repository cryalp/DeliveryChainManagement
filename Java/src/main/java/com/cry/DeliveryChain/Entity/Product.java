package com.cry.DeliveryChain.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "Product")
@Table(name = "Product")
public class Product {
    @JsonIgnore
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @OneToOne
    @JoinColumn(name = "UserAccountId", referencedColumnName = "Id")
    public UserAccount UserAccount;

    @Column(name = "Header")
    public String Header;

    @Column(name = "Description")
    public String Description;

    @Column(name = "Price")
    public BigDecimal Price;

    @Column(name = "Quantity")
    public Integer Quantity;

    @Column(name = "AdditionDate")
    public LocalDateTime AdditionDate;

    @Column(name = "Photo")
    public String Photo;

    @Column(name = "IsActive")
    public Boolean IsActive;

    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public Product() {}

    public Product(UserAccount UserAccount, String Header, String Description, BigDecimal Price, Integer Quantity, LocalDateTime AdditionDate, String Photo,
            Boolean IsActive, UUID UniqueId) {
        this.UserAccount = UserAccount;
        this.Header = Header;
        this.Description = Description;
        this.Price = Price;
        this.Quantity = Quantity;
        this.AdditionDate = AdditionDate;
        this.Photo = Photo;
        this.IsActive = IsActive;
        this.UniqueId = UniqueId;
    }
}

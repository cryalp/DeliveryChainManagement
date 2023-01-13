package com.cry.DeliveryChain.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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

    @Column(name = "Discount")
    public Float Discount;

    @Column(name = "Quantity")
    public Integer Quantity;

    @Column(name = "AdditionDate")
    public LocalDateTime AdditionDate;

    @Transient
    public List<ProductPhoto> PhotoList;

    @Column(name = "IsActive")
    public Boolean IsActive;

    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public Product() {}

    public Product(UserAccount UserAccount, String Header, String Description, BigDecimal Price, Float Discount, Integer Quantity, LocalDateTime AdditionDate,
            Boolean IsActive, UUID UniqueId) {
        this.UserAccount = UserAccount;
        this.Header = Header;
        this.Description = Description;
        this.Price = Price;
        this.Discount = Discount;
        this.Quantity = Quantity;
        this.AdditionDate = AdditionDate;
        this.IsActive = IsActive;
        this.UniqueId = UniqueId;
    }

    public void setPhotoList(List<ProductPhoto> PhotoList) {
        this.PhotoList = PhotoList;
    }
}

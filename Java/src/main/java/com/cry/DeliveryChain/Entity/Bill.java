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

@Entity(name = "Bill")
@Table(name = "Bill")
public class Bill {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @OneToOne
    @JoinColumn(name = "SupplierId", referencedColumnName = "Id")
    public UserAccount Supplier;

    @OneToOne
    @JoinColumn(name = "BuyerId", referencedColumnName = "Id")
    public UserAccount Buyer;

    @OneToOne
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    public Product Product;

    @Column(name = "Quantity")
    public Integer Quantity;

    @Column(name = "CurrentPrice")
    public BigDecimal CurrentPrice;

    @Column(name = "CreationDate")
    public LocalDateTime CreationDate;

    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public Bill() {}

    public Bill(UserAccount Supplier, UserAccount Buyer, Product Product, Integer Quantity, BigDecimal CurrentPrice, LocalDateTime CreationDate, UUID UniqueId) {
        this.Supplier = Supplier;
        this.Buyer = Buyer;
        this.Product = Product;
        this.Quantity = Quantity;
        this.CurrentPrice = CurrentPrice;
        this.CreationDate = CreationDate;
        this.UniqueId = UniqueId;
    }
}

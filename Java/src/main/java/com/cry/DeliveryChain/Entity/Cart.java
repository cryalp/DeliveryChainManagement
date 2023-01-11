package com.cry.DeliveryChain.Entity;

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

@Entity(name = "Cart")
@Table(name = "Cart")
public class Cart {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @OneToOne
    @JoinColumn(name = "BuyerId", referencedColumnName = "Id")
    public UserAccount Buyer;

    @OneToOne
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    public Product Product;

    @Column(name = "Quantity")
    public Integer Quantity;

    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public Cart() {}

    public Cart(UserAccount Buyer, Product Product, Integer Quantity, UUID UniqueId) {
        this.Buyer = Buyer;
        this.Product = Product;
        this.Quantity = Quantity;
        this.UniqueId = UniqueId;
    }
}

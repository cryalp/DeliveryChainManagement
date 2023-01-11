package com.cry.DeliveryChain.Entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "BillProduct")
@Table(name = "BillProduct")
public class BillProduct {
    @JsonIgnore
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @OneToOne
    @JoinColumn(name = "SupplierId", referencedColumnName = "Id")
    public UserAccount Supplier;

    @OneToOne
    @JoinColumn(name = "BillId", referencedColumnName = "Id")
    public Bill Bill;

    @OneToOne
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    public Product Product;

    @Column(name = "Quantity")
    public Integer Quantity;

    @Column(name = "CurrentPrice")
    public BigDecimal CurrentPrice;

    public BillProduct() {}

    public BillProduct(UserAccount Supplier, Bill Bill, Product Product, Integer Quantity, BigDecimal CurrentPrice) {
        this.Supplier = Supplier;
        this.Bill = Bill;
        this.Product = Product;
        this.Quantity = Quantity;
        this.CurrentPrice = CurrentPrice;
    }
}

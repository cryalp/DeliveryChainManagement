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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "ProductPhoto")
@Table(name = "ProductPhoto")
public class ProductPhoto {
    @JsonIgnore
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @OneToOne
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    public Product Product;

    @Column(name = "Photo")
    public String Photo;

    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public ProductPhoto() {}

    public ProductPhoto(Product Product, String Photo, UUID UniqueId) {
        this.Product = Product;
        this.Photo = Photo;
        this.UniqueId = UniqueId;
    }
}

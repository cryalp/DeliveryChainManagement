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

@Entity(name = "Bill")
@Table(name = "Bill")
public class Bill {
    @JsonIgnore
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @OneToOne
    @JoinColumn(name = "BuyerId", referencedColumnName = "Id")
    public UserAccount Buyer;

    @Column(name = "TotalPrice")
    public BigDecimal TotalPrice;

    @Column(name = "CreationDate")
    public LocalDateTime CreationDate;

    @Column(name = "IsApproved")
    public Boolean IsApproved;

    @Column(name = "UniqueId")
    @Type(type = "uuid-char")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public UUID UniqueId;

    public Bill() {}

    public Bill(UserAccount Buyer, BigDecimal TotalPrice, LocalDateTime CreationDate, Boolean IsApproved, UUID UniqueId) {
        this.Buyer = Buyer;
        this.TotalPrice = TotalPrice;
        this.CreationDate = CreationDate;
        this.IsApproved = IsApproved;
        this.UniqueId = UniqueId;
    }
}

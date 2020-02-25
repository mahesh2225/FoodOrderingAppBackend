package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address", schema = "public")
public class AddressEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid", unique = true)
    @Size(max = 200)
    private String uuid;

    @Column(name = "flat_buil_number")
    @Size(max = 255)
    private String flatBuildNo;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 39)
    private String city;

    @Column(name = "pincode", unique = true)
    @Size(max = 30)
    private String pincode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "State_id")
    private StateEntity state;

    @Column(name = "active")
    private Integer active = 1;

    @OneToOne(mappedBy = "address", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private RestaurantEntity restaurant;

    @OneToMany(mappedBy = "address", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<CustomerAddressEntity> customerAddress = new ArrayList<>();

    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private List<OrderEntity> order = new ArrayList<>();

    public AddressEntity(){}

    public AddressEntity(@Size(max = 200) String uuid, @Size(max = 255) String flatBuildNo, @Size(max = 255) String locality, @Size(max = 39) String city, @Size(max = 30) String pincode, StateEntity state) {
        this.id = 0;
        this.uuid = uuid;
        this.flatBuildNo = flatBuildNo;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.active = 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFlatBuilNo() {
        return flatBuildNo;
    }

    public void setFlatBuilNo(String flatBuildNo) {
        this.flatBuildNo = flatBuildNo;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coupon", schema = "public")
@NamedQueries({
        @NamedQuery(name = "getCouponByCouponUuid", query = "select c from CouponEntity c where c.uuid =:uuid"),
        @NamedQuery(name = "getCouponByCouponName", query = "select c from CouponEntity c where c.couponName =:couponName")
})
public class CouponEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid", unique = true)
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "coupon_name")
    @NotNull
    @Size(max = 255)
    private String couponName;

    @Column(name = "percent")
    @NotNull
    private Integer percent;

    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    private List<OrderEntity> order = new ArrayList<>();

    public CouponEntity(@Size(max = 200) @NotNull String uuid, @NotNull @Size(max = 255) String couponName, @NotNull Integer percent) {
        this.id = 0;
        this.uuid = uuid;
        this.couponName = couponName;
        this.percent = percent;
    }

    public CouponEntity() {
        this.id = 0;
        this.uuid = "";
        this.couponName = "";
        this.percent = 0;
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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
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

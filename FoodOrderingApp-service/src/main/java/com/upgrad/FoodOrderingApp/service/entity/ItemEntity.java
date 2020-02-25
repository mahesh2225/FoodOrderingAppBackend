package com.upgrad.FoodOrderingApp.service.entity;

import com.upgrad.FoodOrderingApp.service.common.ItemType;
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
@Table(name = "item", schema = "public")
@NamedQueries({
        @NamedQuery(name = "getItemsByUuid", query = "select i from ItemEntity i where i.uuid =:uuid"),
        @NamedQuery(name = "getItemsByCategoryAndRestaurant", query = "Select i from ItemEntity i, RestaurantItemEntity ri, CategoryItemEntity ci " +
                "where ri.item.uuid = i.uuid and ci.item.uuid = i.uuid and ci.category.uuid =:categoryId and ri.restaurant.uuid =:restaurantId"),
        @NamedQuery(name = "getItemsByPopularity", query = "Select ie from ItemEntity ie where ie.id in ( Select oi.item.id from ItemEntity i, RestaurantItemEntity ri, OrderItemEntity oi " +
                " where ri.item.id = i.id and oi.item.id = i.id and ri.restaurant.id = :restaurantId group by oi.item.id order by sum(oi.quantity) )"),
})
public class ItemEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid", unique = true)
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "item_name")
    @NotNull
    @Size(max = 30)
    private String itemName;

    @Column(name = "price")
    @NotNull
    private Integer price;

    @Column(name = "type")
    @NotNull
    @Size(max = 10)
    @Enumerated(EnumType.ORDINAL)
    private ItemType type;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<CategoryItemEntity> categoryItem = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RestaurantItemEntity> restaurantItem = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItem = new ArrayList<>();

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
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

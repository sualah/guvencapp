package com.tr.guvencmakina.guvencapp.Products.data;

import com.google.common.base.Objects;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ProductCategory {
    public String uid;
    String name;
    String image;

    public ProductCategory() {
    }

    public ProductCategory(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", image=" + image +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategory)) return false;
        ProductCategory that = (ProductCategory) o;
        return Objects.equal(getUid(), that.getUid()) &&
                Objects.equal(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUid(), getName());
    }
}

package com.seray.sjc.entity.device;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.seray.sjc.api.result.ProductDZCDTO;

@Entity(tableName = ProductADB.TABLE_NAME)
public class ProductADB {

    public static final String TABLE_NAME = "Product";

    @ColumnInfo(name = "product_id")
    @PrimaryKey
    @NonNull
    private Integer product_id;

    @ColumnInfo(name = "sequences")
    private Integer sequences;

    @ColumnInfo(name = "product_name")
    private String pro_name;

    @ColumnInfo(name = "price")
    private Double price;

    @ColumnInfo(name = "max_price")
    private Double max_price;

    @ColumnInfo(name = "min_price")
    private Double min_price;

    @ColumnInfo(name = "img_url")
    private String img_url;

    public ProductADB() {
        super();
    }

    public ProductADB(ProductDZCDTO productDZCDTO) {
        this.product_id=productDZCDTO.getProduct_id();
        this.sequences=productDZCDTO.getSequences();
        this.pro_name = productDZCDTO.getPro_name();
        this.price=productDZCDTO.getPrice();
        this.max_price=productDZCDTO.getMax_price();
        this.min_price=productDZCDTO.getMin_price();
        this.img_url = productDZCDTO.getImg_url();
    }


    @NonNull
    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(@NonNull Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getSequences() {
        return sequences;
    }

    public void setSequences(Integer sequences) {
        this.sequences = sequences;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMax_price() {
        return max_price;
    }

    public void setMax_price(Double max_price) {
        this.max_price = max_price;
    }

    public Double getMin_price() {
        return min_price;
    }

    public void setMin_price(Double min_price) {
        this.min_price = min_price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return "ProductADB{" +
                "product_id=" + product_id +
                ", sequences=" + sequences +
                ", pro_name='" + pro_name + '\'' +
                ", price=" + price +
                ", max_price=" + max_price +
                ", min_price=" + min_price +
                ", img_url='" + img_url + '\'' +
                '}';
    }
}

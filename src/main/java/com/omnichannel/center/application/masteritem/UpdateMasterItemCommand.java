package com.omnichannel.center.application.masteritem;

import com.omnichannel.center.domain.masteritem.ChannelListing;

import java.util.List;
import java.util.Map;

public class UpdateMasterItemCommand {
    private String sku;
    private String title;
    private String description;
    private String brand;
    private String category;
    private Map<String, String> attributes;
    private List<ChannelListing> listings;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<ChannelListing> getListings() {
        return listings;
    }

    public void setListings(List<ChannelListing> listings) {
        this.listings = listings;
    }
}

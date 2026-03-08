package com.omnichannel.center.masteritem;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateMasterItemRequest {
    @NotBlank
    private String tenantId;

    @NotBlank
    private String sku;

    @NotBlank
    private String title;

    private String description;
    private String brand;
    private String category;
    private Map<String, String> attributes = new HashMap<>();
    private List<ChannelListing> listings = new ArrayList<>();

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

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

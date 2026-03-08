package com.omnichannel.center.masteritem;

import com.omnichannel.center.config.ChannelCode;

public class ChannelListing {
    private ChannelCode channel;
    private String listingId;
    private String sku;
    private String status;
    private double price;

    public ChannelCode getChannel() {
        return channel;
    }

    public void setChannel(ChannelCode channel) {
        this.channel = channel;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

package com.example.myapplication;

public class ItemDetails {
    public String itemName, itemQuantity, address, fullName, phone;

    public ItemDetails(){

    }

    public ItemDetails(String itemName, String itemQuantity, String address, String fullName, String phone) {
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.address = address;
        this.fullName = fullName;
        this.phone = phone;
    }

}

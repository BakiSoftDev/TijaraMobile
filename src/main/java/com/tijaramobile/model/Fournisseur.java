package com.tijaramobile.model;

import java.time.LocalDateTime;

public class Fournisseur {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private boolean tvaExempt;
    private LocalDateTime createdAt;

    public Fournisseur() {}

    public Fournisseur(String name, String phone, String email, String address, boolean tvaExempt) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tvaExempt = tvaExempt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isTvaExempt() { return tvaExempt; }
    public void setTvaExempt(boolean tvaExempt) { this.tvaExempt = tvaExempt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return name != null ? name : ""; }
}

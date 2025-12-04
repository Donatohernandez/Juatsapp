package com.juatsapp.app;

import java.util.Date;

/**
 * Entidad que representa a un usuario de Juatsapp.
 * Se persiste en la colección "users" de MongoDB.
 */
public class User {

    private String id;           // _id de MongoDB como String
    private String phone;        // Teléfono utilizado para el login
    private String passwordHash; // Contraseña hasheada con BCrypt
    private Date birthDate;      // Fecha de nacimiento
    private String address;      // Dirección completa
    private String sex;          // masculino, femenino, robot, ninja, otro
    private Date createdAt;
    private Date updatedAt;

    public User() {
    }

    

    public User(String phone, String passwordHash, Date birthDate,
                String address, String sex) {
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.birthDate = birthDate;
        this.address = address;
        this.sex = sex;
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package technologyaa.devit.domain.member.oatuh.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String picture;
    private String provider;

    public User() {}

    public User(String name, String email, String picture, String provider) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPicture() { return picture; }
    public String getProvider() { return provider; }

    public void setName(String name) { this.name = name; }
    public void setPicture(String picture) { this.picture = picture; }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }
}
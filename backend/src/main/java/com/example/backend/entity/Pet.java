package com.example.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private PetType type;

    @Enumerated(EnumType.STRING)
    private PetStatus status = PetStatus.AVAILABLE;

    private String breed;
    private Integer age;

    // ✅ 性别改为普通字符串（去掉 @Enumerated）
    private String gender;

    private String color;
    private String description;
    private String healthStatus;

    // ✅ 疫苗接种信息（字符串）
    private String vaccination;

    // ✅ 图片字段名统一为 image（前端字段）
    @Column(name = "image")
    private String image = "/images/pets/default.jpg";

    // 已接种疫苗标志（布尔）
    private Boolean vaccinated = false;

    // 已驱虫标志（布尔）
    private Boolean dewormed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum PetType {
        DOG, CAT, RABBIT, BIRD, OTHER
    }

    public enum PetStatus {
        AVAILABLE, ADOPTED, FOSTERED, PENDING, PENDING_ADOPTION
    }
}
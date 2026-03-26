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

    @Enumerated(EnumType.STRING) // 建议改为枚举
    private Gender gender;

    private String color;
    private String description;
    private String healthStatus;

    // 修改：与前端字段保持一致
    @Column(name = "image") // 数据库字段也改为 image
    private String image = "/images/pets/default.jpg";

    // 修改：boolean 类型，与前端匹配
    @Column(name = "vaccinated")
    private Boolean vaccinated = false;

    // 新增：驱虫字段
    @Column(name = "dewormed")
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
        AVAILABLE, // 可领养
        ADOPTED, // 已领养
        FOSTERED, // 寄养中
        PENDING, // 待处理
        PENDING_ADOPTION // 待领养
    }

    // 新增：性别枚举
    public enum Gender {
        MALE, FEMALE
    }
}
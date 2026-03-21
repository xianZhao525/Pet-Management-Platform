package com.example.backend.dto;

import lombok.Data;
// ✅ 正确的（Jakarta EE，用于Spring Boot 3.x）
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class AdminDTO {

    // 宠物管理相关
    private Long petId;

    @NotBlank(message = "宠物名称不能为空")
    private String petName;

    private String petType;

    @NotBlank(message = "宠物状态不能为空")
    private String petStatus;

    private String petBreed;

    @NotNull(message = "宠物年龄不能为空")
    private Integer petAge;

    private String petGender;

    private String petColor;

    private String petDescription;

    private String healthStatus;

    private String vaccination;

    private String imageUrl;

    private Long ownerId;

    // 领养申请审核相关
    private Long adoptionId;

    @NotBlank(message = "审核意见不能为空")
    private String reviewNotes;

    private String action; // APPROVE/REJECT/COMPLETE

    // 分页相关
    private Integer page = 0;

    private Integer size = 10;
}
package com.example.backend.controller.spring;

import com.example.backend.entity.Pet;
import com.example.backend.service.PetService;
import com.example.backend.controller.spring.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    // ==================== 原有页面渲染接口（保留给JSP用）====================

    @GetMapping("/list")
    public String listPets(@RequestParam(required = false) String keyword, Model model) {
        List<Pet> pets = StringUtils.hasText(keyword)
                ? petService.searchPets(keyword)
                : petService.getAvailablePets();

        model.addAttribute("pets", pets);
        model.addAttribute("keyword", keyword);

        Map<String, Long> stats = new HashMap<>();
        stats.put("availableCount", petService.countByStatus(Pet.PetStatus.AVAILABLE));
        stats.put("adoptedCount", petService.countByStatus(Pet.PetStatus.ADOPTED));
        stats.put("fosteredCount", petService.countByStatus(Pet.PetStatus.FOSTERED));
        model.addAttribute("stats", stats);

        return "pet/list";
    }

    @GetMapping("/detail")
    public String petDetail(@RequestParam Long id, Model model) {
        Pet pet = petService.getPetById(id).orElse(null);
        model.addAttribute("pet", pet);
        return "pet/detail";
    }

    // ==================== 新增：REST API 接口（给Vue前端用）====================

    /**
     * 获取宠物列表（JSON）
     * GET /pet/api/list?keyword=xxx&type=xxx
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ApiResponse<Map<String, Object>> getPetListApi(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {

        List<Pet> pets = StringUtils.hasText(keyword)
                ? petService.searchPets(keyword)
                : petService.getAvailablePets();

        // 如果前端传了type参数，再过滤一次
        if (StringUtils.hasText(type) && !"ALL".equals(type)) {
            pets = pets.stream()
                    .filter(p -> p.getType().name().equals(type))
                    .toList();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", pets);

        Map<String, Long> stats = new HashMap<>();
        stats.put("availableCount", petService.countByStatus(Pet.PetStatus.AVAILABLE));
        stats.put("adoptedCount", petService.countByStatus(Pet.PetStatus.ADOPTED));
        stats.put("fosteredCount", petService.countByStatus(Pet.PetStatus.FOSTERED));
        data.put("stats", stats);

        return ApiResponse.success(data);
    }

    /**
     * 获取宠物详情（JSON）
     * GET /pet/api/detail?id=2
     */
    @GetMapping("/api/detail")
    @ResponseBody
    public ApiResponse<Pet> getPetDetailApi(@RequestParam Long id) {
        Pet pet = petService.getPetById(id).orElse(null);
        if (pet == null) {
            return ApiResponse.error(404, "宠物不存在");
        }
        return ApiResponse.success(pet);
    }

    /**
     * 获取可领养宠物列表（JSON）
     * GET /pet/api/available
     */
    @GetMapping("/api/available")
    @ResponseBody
    public ApiResponse<List<Pet>> getAvailablePetsApi() {
        List<Pet> pets = petService.getAvailablePets();
        return ApiResponse.success(pets);
    }

    /**
     * 搜索宠物（JSON）
     * GET /pet/api/search?keyword=xxx
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ApiResponse<List<Pet>> searchPetsApi(@RequestParam String keyword) {
        List<Pet> pets = petService.searchPets(keyword);
        return ApiResponse.success(pets);
    }
}
package com.example.backend.controller.spring;

import com.example.backend.dto.AdminDTO;
import com.example.backend.entity.*;
import com.example.backend.service.AdoptionService;
import com.example.backend.service.PetService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PetService petService;

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private UserService userService;

    // 检查管理员权限
    private boolean checkAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole() == User.UserRole.ADMIN;
    }

    // 管理员主页
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        User admin = (User) session.getAttribute("user");
        model.addAttribute("admin", admin);

        long pendingAdoptions = adoptionService.getPendingAdoptions().size();
        long availablePets = petService.countByStatus(Pet.PetStatus.AVAILABLE);
        long totalAdoptions = adoptionService.getAdoptionCount();
        long totalUsers = userService.getUserCount();

        model.addAttribute("pendingAdoptions", pendingAdoptions);
        model.addAttribute("availablePets", availablePets);
        model.addAttribute("totalAdoptions", totalAdoptions);
        model.addAttribute("totalUsers", totalUsers);

        List<Adoption> recentAdoptions = adoptionService.getPendingAdoptions();
        if (recentAdoptions.size() > 5) {
            recentAdoptions = recentAdoptions.subList(0, 5);
        }
        model.addAttribute("recentAdoptions", recentAdoptions);

        return "admin/dashboard";
    }

    // ==================== 宠物管理 ====================

    @GetMapping("/pets")
    public String managePets(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        List<Pet> pets;
        if (keyword != null && !keyword.trim().isEmpty()) {
            pets = petService.searchPets(keyword);
        } else {
            pets = petService.getAllPets();
        }

        model.addAttribute("pets", pets);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "admin/pet/list";
    }

    @GetMapping("/pets/add")
    public String showAddPetForm(HttpSession session, Model model) {
        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        model.addAttribute("adminDTO", new AdminDTO());
        model.addAttribute("petTypes", Pet.PetType.values());
        model.addAttribute("petStatuses", Pet.PetStatus.values());

        return "admin/pet/add";
    }

    @PostMapping("/pets/add")
    public String addPet(@Valid @ModelAttribute AdminDTO adminDTO,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("petTypes", Pet.PetType.values());
            model.addAttribute("petStatuses", Pet.PetStatus.values());
            return "admin/pet/add";
        }

        try {
            Pet pet = new Pet();
            pet.setName(adminDTO.getPetName());
            pet.setType(Pet.PetType.valueOf(adminDTO.getPetType()));
            pet.setStatus(Pet.PetStatus.valueOf(adminDTO.getPetStatus()));
            pet.setBreed(adminDTO.getPetBreed());
            pet.setAge(adminDTO.getPetAge());
            // ✅ 修复：gender 直接是 String
            pet.setGender(adminDTO.getPetGender());
            pet.setColor(adminDTO.getPetColor());
            pet.setDescription(adminDTO.getPetDescription());
            pet.setHealthStatus(adminDTO.getHealthStatus());
            // ✅ 修复：vaccinated 是 Boolean
            pet.setVaccinated("已接种".equals(adminDTO.getVaccination()) ||
                    "true".equalsIgnoreCase(adminDTO.getVaccination()));
            pet.setDewormed(false); // 默认
            // ✅ 修复：image 代替 imageUrl
            pet.setImage(adminDTO.getImageUrl());

            if (adminDTO.getOwnerId() != null) {
                userService.getUserById(adminDTO.getOwnerId()).ifPresent(pet::setOwner);
            }

            petService.savePet(pet);

            redirectAttributes.addFlashAttribute("success", "宠物添加成功！");
            return "redirect:/admin/pets";

        } catch (Exception e) {
            model.addAttribute("error", "添加失败: " + e.getMessage());
            model.addAttribute("petTypes", Pet.PetType.values());
            model.addAttribute("petStatuses", Pet.PetStatus.values());
            return "admin/pet/add";
        }
    }

    @GetMapping("/pets/edit/{id}")
    public String showEditPetForm(@PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        Pet pet = petService.getPetById(id)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setPetId(pet.getId());
        adminDTO.setPetName(pet.getName());
        adminDTO.setPetType(pet.getType().name());
        adminDTO.setPetStatus(pet.getStatus().name());
        adminDTO.setPetBreed(pet.getBreed());
        adminDTO.setPetAge(pet.getAge());
        // ✅ 修复：gender 是 String
        adminDTO.setPetGender(pet.getGender());
        adminDTO.setPetColor(pet.getColor());
        adminDTO.setPetDescription(pet.getDescription());
        adminDTO.setHealthStatus(pet.getHealthStatus());
        // ✅ 修复：vaccinated 转 String
        adminDTO.setVaccination(Boolean.TRUE.equals(pet.getVaccinated()) ? "已接种" : "未接种");
        // ✅ 修复：image 代替 imageUrl
        adminDTO.setImageUrl(pet.getImage());

        if (pet.getOwner() != null) {
            adminDTO.setOwnerId(pet.getOwner().getId());
        }

        model.addAttribute("adminDTO", adminDTO);
        model.addAttribute("petTypes", Pet.PetType.values());
        model.addAttribute("petStatuses", Pet.PetStatus.values());

        return "admin/pet/edit";
    }

    @PostMapping("/pets/edit")
    public String editPet(@Valid @ModelAttribute AdminDTO adminDTO,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("petTypes", Pet.PetType.values());
            model.addAttribute("petStatuses", Pet.PetStatus.values());
            return "admin/pet/edit";
        }

        try {
            Pet pet = petService.getPetById(adminDTO.getPetId())
                    .orElseThrow(() -> new RuntimeException("宠物不存在"));

            pet.setName(adminDTO.getPetName());
            pet.setType(Pet.PetType.valueOf(adminDTO.getPetType()));
            pet.setStatus(Pet.PetStatus.valueOf(adminDTO.getPetStatus()));
            pet.setBreed(adminDTO.getPetBreed());
            pet.setAge(adminDTO.getPetAge());
            // ✅ 修复：gender 是 String
            pet.setGender(adminDTO.getPetGender());
            pet.setColor(adminDTO.getPetColor());
            pet.setDescription(adminDTO.getPetDescription());
            pet.setHealthStatus(adminDTO.getHealthStatus());
            // ✅ 修复：vaccinated 是 Boolean
            pet.setVaccinated("已接种".equals(adminDTO.getVaccination()) ||
                    "true".equalsIgnoreCase(adminDTO.getVaccination()));
            // ✅ 修复：image 代替 imageUrl
            pet.setImage(adminDTO.getImageUrl());

            if (adminDTO.getOwnerId() != null) {
                userService.getUserById(adminDTO.getOwnerId()).ifPresent(pet::setOwner);
            } else {
                pet.setOwner(null);
            }

            petService.savePet(pet);

            redirectAttributes.addFlashAttribute("success", "宠物信息更新成功！");
            return "redirect:/admin/pets";

        } catch (Exception e) {
            model.addAttribute("error", "更新失败: " + e.getMessage());
            model.addAttribute("petTypes", Pet.PetType.values());
            model.addAttribute("petStatuses", Pet.PetStatus.values());
            return "admin/pet/edit";
        }
    }

    @PostMapping("/pets/delete/{id}")
    public String deletePet(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            petService.deletePet(id);
            redirectAttributes.addFlashAttribute("success", "宠物删除成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }

        return "redirect:/admin/pets";
    }

    // ==================== 领养申请管理 ====================

    @GetMapping("/adoptions")
    public String manageAdoptions(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        List<Adoption> adoptions = adoptionService.getPendingAdoptions();

        model.addAttribute("adoptions", adoptions);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("status", status);

        return "admin/adoption/list";
    }

    @GetMapping("/adoptions/detail/{id}")
    public String adoptionDetail(@PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            Adoption adoption = adoptionService.getAdoptionById(id);
            model.addAttribute("adoption", adoption);

            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setAdoptionId(id);
            model.addAttribute("adminDTO", adminDTO);

            return "admin/adoption/detail";
        } catch (Exception e) {
            model.addAttribute("error", "获取申请详情失败: " + e.getMessage());
            return "redirect:/admin/adoptions";
        }
    }

    @PostMapping("/adoptions/approve")
    public String approveAdoption(@ModelAttribute AdminDTO adminDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            adoptionService.approveAdoption(adminDTO.getAdoptionId(), adminDTO.getReviewNotes());
            redirectAttributes.addFlashAttribute("success", "申请已批准！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "批准失败: " + e.getMessage());
        }

        return "redirect:/admin/adoptions";
    }

    @PostMapping("/adoptions/reject")
    public String rejectAdoption(@ModelAttribute AdminDTO adminDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            adoptionService.rejectAdoption(adminDTO.getAdoptionId(), adminDTO.getReviewNotes());
            redirectAttributes.addFlashAttribute("success", "申请已拒绝！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "拒绝失败: " + e.getMessage());
        }

        return "redirect:/admin/adoptions";
    }

    @PostMapping("/adoptions/complete/{id}")
    public String completeAdoption(@PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            adoptionService.completeAdoption(id);
            redirectAttributes.addFlashAttribute("success", "领养已完成！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "操作失败: " + e.getMessage());
        }

        return "redirect:/admin/adoptions";
    }

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    public String manageUsers(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/user/login";
        }

        List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userService.searchUsers(keyword);
        } else {
            users = userService.searchUsers("");
        }

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);

        return "admin/user/list";
    }
}
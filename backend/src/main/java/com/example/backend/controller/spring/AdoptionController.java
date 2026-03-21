package com.example.backend.controller.spring;

import com.example.backend.dto.AdoptionDTO;
import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.Adoption;
import com.example.backend.entity.Pet;
import com.example.backend.entity.User;
import com.example.backend.service.AdoptionService;
import com.example.backend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private PetService petService; // 新增：用于获取宠物信息

    // 显示领养申请页面
    @GetMapping("/apply/{petId}")
    public String showApplyForm(@PathVariable Long petId,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login?redirect=/adoption/apply/" + petId;
        }

        // 检查是否已经申请过
        if (adoptionService.hasAppliedForPet(user.getId(), petId)) {
            model.addAttribute("error", "您已经申请过领养此宠物");
            return "redirect:/pet/detail?id=" + petId;
        }

        // 获取宠物信息
        Optional<Pet> petOptional = petService.getPetById(petId);
        if (!petOptional.isPresent()) {
            model.addAttribute("error", "宠物不存在");
            return "redirect:/pet/list";
        }

        Pet pet = petOptional.get();

        // 检查宠物是否可领养
        if (pet.getStatus() != Pet.PetStatus.AVAILABLE) {
            model.addAttribute("error", "该宠物暂时不可领养");
            return "redirect:/pet/detail?id=" + petId;
        }

        AdoptionDTO adoptionDTO = new AdoptionDTO();
        adoptionDTO.setUserId(user.getId());
        adoptionDTO.setPetId(petId);

        // 设置用户默认信息
        adoptionDTO.setContactPhone(user.getPhone() != null ? user.getPhone() : "");
        adoptionDTO.setContactAddress(user.getAddress() != null ? user.getAddress() : "");

        model.addAttribute("adoptionDTO", adoptionDTO);
        model.addAttribute("pet", pet); // 将宠物信息传给页面
        model.addAttribute("user", user); // 将用户信息传给页面
        return "adoption/apply";
    }

    // 处理领养申请
    @PostMapping("/apply")
    public String applyAdoption(@Valid @ModelAttribute AdoptionDTO adoptionDTO,
            BindingResult result,
            HttpSession session,
            Model model) {

        System.out.println("========== FORM SUBMIT DEBUG ==========");
        System.out.println("User in session: " + session.getAttribute("user"));
        System.out.println("AdoptionDTO: " + adoptionDTO);
        System.out.println("BindingResult has errors: " + result.hasErrors());
        if (result.hasErrors()) {
            result.getAllErrors()
                    .forEach(error -> System.out.println("Validation Error: " + error.getDefaultMessage()));
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        // 重新获取宠物信息用于页面显示
        Optional<Pet> petOptional = petService.getPetById(adoptionDTO.getPetId());
        if (!petOptional.isPresent()) {
            model.addAttribute("error", "宠物不存在");
            return "adoption/apply";
        }

        model.addAttribute("pet", petOptional.get());
        model.addAttribute("user", user);

        if (result.hasErrors()) {
            return "adoption/apply";
        }

        try {
            // 确保设置当前用户的ID
            adoptionDTO.setUserId(user.getId());
            Adoption adoption = adoptionService.applyAdoption(adoptionDTO);
            model.addAttribute("success", "领养申请提交成功，请等待审核");
            model.addAttribute("pet", petOptional.get());
            return "adoption/apply";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "adoption/apply";
        }
    }

    // 显示我的领养申请
    @GetMapping("/my")
    public String myAdoptions(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        try {
            List<Adoption> adoptions = adoptionService.getAdoptionsByUserId(user.getId());
            model.addAttribute("adoptions", adoptions);
            model.addAttribute("applications", adoptions); // 与myApplications.jsp中的变量名匹配
            return "adoption/myApplications"; // 返回正确的JSP文件名
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    // 其他方法保持不变...
    // 管理员：显示所有领养申请
    @GetMapping("/admin/list")
    public String adminListAdoptions(HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/user/login";
        }

        // 这里可以使用分页，暂时简化
        List<Adoption> pendingAdoptions = adoptionService.getPendingAdoptions();
        model.addAttribute("adoptions", pendingAdoptions);

        return "adoption/admin/list";
    }

    // 管理员：批准申请
    @PostMapping("/admin/approve/{id}")
    public String approveAdoption(@PathVariable Long id,
            @RequestParam String adminNotes,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            adoptionService.approveAdoption(id, adminNotes);
            model.addAttribute("success", "申请已批准");
            return "redirect:/adoption/admin/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/adoption/admin/list";
        }
    }

    // 管理员：拒绝申请
    @PostMapping("/admin/reject/{id}")
    public String rejectAdoption(@PathVariable Long id,
            @RequestParam String adminNotes,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            adoptionService.rejectAdoption(id, adminNotes);
            model.addAttribute("success", "申请已拒绝");
            return "redirect:/adoption/admin/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/adoption/admin/list";
        }
    }

    // 管理员：完成领养
    @PostMapping("/admin/complete/{id}")
    public String completeAdoption(@PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            adoptionService.completeAdoption(id);
            model.addAttribute("success", "领养已完成");
            return "redirect:/adoption/admin/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/adoption/admin/list";
        }
    }

    // REST API: 申请领养
    @PostMapping("/api/apply")
    @ResponseBody
    public ApiResponse<Adoption> applyAdoptionApi(@Valid @RequestBody AdoptionDTO adoptionDTO,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error("请先登录", 401);
        }

        try {
            // 设置当前用户的ID
            adoptionDTO.setUserId(user.getId());
            Adoption adoption = adoptionService.applyAdoption(adoptionDTO);
            return ApiResponse.success("申请提交成功", adoption);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 管理员：获取所有领养申请（分页）
    @GetMapping("/admin/all")
    public String getAllAdoptions(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/user/login";
        }

        try {
            // 这里需要实现分页查询
            // Page<Adoption> adoptions = adoptionService.getAllAdoptions(page, size);
            // model.addAttribute("adoptions", adoptions.getContent());
            // model.addAttribute("pageInfo", createPageInfo(adoptions));

            // 暂时返回所有
            List<Adoption> allAdoptions = adoptionService.getPendingAdoptions();
            model.addAttribute("adoptions", allAdoptions);
            return "adoption/admin/all";
        } catch (Exception e) {
            model.addAttribute("error", "获取领养申请失败: " + e.getMessage());
            return "adoption/admin/all";
        }
    }

    // 查看领养申请详情
    @GetMapping("/detail/{id}")
    public String adoptionDetail(@PathVariable Long id,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

        try {
            Adoption adoption = adoptionService.getAdoptionById(id);

            // 检查权限：只有申请人或管理员可以查看
            if (!user.getId().equals(adoption.getUser().getId()) &&
                    !isAdmin(session)) {
                model.addAttribute("error", "无权查看此申请");
                return "redirect:/adoption/my";
            }

            model.addAttribute("adoption", adoption);
            return "adoption/detail";
        } catch (Exception e) {
            model.addAttribute("error", "获取领养申请详情失败: " + e.getMessage());
            return "redirect:/adoption/my";
        }
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole() == User.UserRole.ADMIN;
    }

    /**
     * 创建分页信息
     */
    private java.util.Map<String, Object> createPageInfo(org.springframework.data.domain.Page<?> page) {
        java.util.Map<String, Object> pageInfo = new java.util.HashMap<>();

        pageInfo.put("currentPage", page.getNumber() + 1); // 当前页（从1开始）
        pageInfo.put("totalPages", page.getTotalPages()); // 总页数
        pageInfo.put("totalElements", page.getTotalElements()); // 总记录数
        pageInfo.put("pageSize", page.getSize()); // 每页大小
        pageInfo.put("isFirst", page.isFirst()); // 是否第一页
        pageInfo.put("isLast", page.isLast()); // 是否最后一页
        pageInfo.put("hasNext", page.hasNext()); // 是否有下一页
        pageInfo.put("hasPrevious", page.hasPrevious()); // 是否有上一页

        return pageInfo;
    }
}
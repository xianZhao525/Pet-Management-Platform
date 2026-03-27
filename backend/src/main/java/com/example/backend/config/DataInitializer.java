package com.example.backend.config;

import com.example.backend.dao.PetRepository; // 改回 dao
import com.example.backend.dao.UserRepository; // 确认 UserRepository 也在 dao
import com.example.backend.entity.Pet;
import com.example.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PetRepository petRepository; // 现在使用 repository 包的

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                // 1. 初始化管理员账户
                if (userRepository.findByUsername("admin").isEmpty()) {
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setEmail("admin@petplatform.com");
                        admin.setPhone("13800138000");
                        admin.setRole(User.UserRole.ADMIN);
                        userRepository.save(admin);
                        System.out.println("✅ 初始化管理员账户: admin / admin123");
                }

                // 2. 初始化普通用户
                if (userRepository.findByUsername("user1").isEmpty()) {
                        User user = new User();
                        user.setUsername("user1");
                        user.setPassword(passwordEncoder.encode("user123"));
                        user.setEmail("user1@example.com");
                        user.setPhone("13900139000");
                        user.setRole(User.UserRole.USER);
                        userRepository.save(user);
                        System.out.println("✅ 初始化普通用户: user1 / user123");
                }

                // 3. 初始化20只宠物数据（与前端静态数据完全匹配）
                if (petRepository.count() == 0) {
                        System.out.println("📦 开始初始化20只宠物数据...");

                        // 狗狗 1-5
                        createPet(1L, "旺财", Pet.PetType.DOG, "金毛寻回犬", 2, "MALE",
                                        "性格温和友善，特别喜欢小孩，已经学会了基本指令如\"坐下\"、\"握手\"，喜欢玩接球游戏。",
                                        "/images/pets/dog1.jpg", "健康", true, true);

                        createPet(2L, "小黑", Pet.PetType.DOG, "边境牧羊犬", 3, "FEMALE",
                                        "非常聪明活泼，精力充沛，喜欢追逐和接飞盘，适合有院子或有时间带它户外活动的家庭。",
                                        "/images/pets/dog2.jpg", "健康", true, true);

                        createPet(3L, "豆豆", Pet.PetType.DOG, "柯基犬", 1, "MALE",
                                        "短腿柯基，性格活泼可爱，特别喜欢撒娇，会跟着主人走来走去，是个小跟屁虫。",
                                        "/images/pets/dog3.jpg", "健康", true, true);

                        createPet(4L, "乐乐", Pet.PetType.DOG, "哈士奇", 2, "FEMALE",
                                        "蓝眼睛的美丽哈士奇，性格独立但有颗温柔的心，需要定期梳毛，不适合公寓饲养。",
                                        "/images/pets/dog4.jpg", "健康", true, true);

                        createPet(5L, "多多", Pet.PetType.DOG, "柴犬", 4, "MALE",
                                        "表情包常客柴犬，性格忠诚机警，对主人非常忠心，适合有养狗经验的家庭。",
                                        "/images/pets/dog5.jpg", "健康", true, true);

                        // 猫咪 6-10
                        createPet(6L, "咪咪", Pet.PetType.CAT, "英国短毛猫", 3, "FEMALE",
                                        "蓝灰色英短，性格温和安静，喜欢趴在窗边看风景，对陌生人有些害羞。",
                                        "/images/pets/cat1.jpg", "健康", true, true);

                        createPet(7L, "小橘", Pet.PetType.CAT, "橘猫", 2, "MALE",
                                        "名副其实的小吃货，性格亲人，特别喜欢被抚摸下巴，会发出响亮的呼噜声。",
                                        "/images/pets/cat2.jpg", "健康", true, true);

                        createPet(8L, "小白", Pet.PetType.CAT, "波斯猫", 4, "FEMALE",
                                        "纯白长毛波斯猫，性格优雅高傲，需要每天梳毛，喜欢安静的环境。",
                                        "/images/pets/cat3.jpg", "健康", true, true);

                        createPet(9L, "奶牛", Pet.PetType.CAT, "奶牛猫", 1, "MALE",
                                        "黑白花纹奶牛猫，性格活泼好动，喜欢探索家里的每个角落，好奇心强。",
                                        "/images/pets/cat4.jpg", "健康", true, true);

                        createPet(10L, "小黑猫", Pet.PetType.CAT, "黑猫", 2, "FEMALE",
                                        "乌黑发亮的毛发，性格独立但亲人，晚上眼睛像两颗宝石，非常漂亮。",
                                        "/images/pets/cat5.jpg", "健康", true, true);

                        // 兔子 11-15
                        createPet(11L, "雪球", Pet.PetType.RABBIT, "垂耳兔", 1, "FEMALE",
                                        "纯白垂耳兔，性格温顺胆小，喜欢吃提摩西草和胡萝卜，需要定期修剪指甲。",
                                        "/images/pets/rabbit1.jpg", "健康", true, true);

                        createPet(12L, "咖啡", Pet.PetType.RABBIT, "侏儒兔", 2, "MALE",
                                        "咖啡色侏儒兔，体型小巧可爱，性格活泼好动，喜欢在笼子里跳来跳去。",
                                        "/images/pets/rabbit2.jpg", "健康", true, true);

                        createPet(13L, "花花", Pet.PetType.RABBIT, "道奇兔", 1, "FEMALE",
                                        "黑白花纹道奇兔，性格亲人，喜欢被轻轻抚摸头部，食量很大但很健康。",
                                        "/images/pets/rabbit3.jpg", "健康", true, true);

                        createPet(14L, "灰灰", Pet.PetType.RABBIT, "安哥拉兔", 3, "MALE",
                                        "灰色长毛安哥拉兔，毛发需要每天梳理，性格温顺安静，适合细心有耐心的主人。",
                                        "/images/pets/rabbit4.jpg", "健康", true, true);

                        createPet(15L, "巧克力", Pet.PetType.RABBIT, "荷兰垂耳兔", 2, "FEMALE",
                                        "深棕色垂耳兔，特别喜欢吃甜菜根和苹果，会用便盆，非常聪明。",
                                        "/images/pets/rabbit5.jpg", "健康", true, true);

                        // 其他 16-20
                        createPet(16L, "小胖", Pet.PetType.OTHER, "仓鼠", 0, "MALE",
                                        "黄金仓鼠，圆滚滚的很可爱，晚上比较活跃，喜欢在滚轮上跑步。",
                                        "/images/pets/other1.jpg", "健康", true, true);

                        createPet(17L, "小绿", Pet.PetType.OTHER, "鹦鹉", 3, "FEMALE",
                                        "虎皮鹦鹉，羽毛颜色鲜艳漂亮，会模仿简单的声音，喜欢和人互动。",
                                        "/images/pets/other2.jpg", "健康", true, true);

                        createPet(18L, "慢慢", Pet.PetType.OTHER, "乌龟", 8, "MALE",
                                        "巴西龟，性格安静，寿命长，容易照顾，需要定期晒背和换水。",
                                        "/images/pets/other3.jpg", "健康", true, true);

                        createPet(19L, "嘟嘟", Pet.PetType.OTHER, "荷兰猪", 2, "FEMALE",
                                        "三色荷兰猪，喜欢发出\"咕咕\"的声音，需要足够的活动空间和新鲜蔬菜。",
                                        "/images/pets/other4.jpg", "健康", true, true);

                        createPet(20L, "毛毛", Pet.PetType.OTHER, "龙猫", 4, "MALE",
                                        "灰色龙猫，毛茸茸的很可爱，需要定期梳毛和沙浴，性格温和亲人。",
                                        "/images/pets/other5.jpg", "健康", true, true);

                        System.out.println("✅ 20只宠物初始化完成！");
                } else {
                        System.out.println("📦 宠物数据已存在，跳过初始化");
                }
        }

        private void createPet(Long id, String name, Pet.PetType type, String breed,
                        int age, String gender, String description,
                        String image, String healthStatus,
                        boolean vaccinated, boolean dewormed) {
                Pet pet = new Pet();
                pet.setId(id); // 设置固定ID，与前端匹配
                pet.setName(name);
                pet.setType(type);
                pet.setBreed(breed);
                pet.setAge(age);
                pet.setGender(gender);
                pet.setDescription(description);
                pet.setImage(image);
                pet.setHealthStatus(healthStatus);
                pet.setVaccinated(vaccinated);
                pet.setDewormed(dewormed);
                pet.setStatus(Pet.PetStatus.AVAILABLE);
                pet.setColor("未知"); // 默认颜色
                petRepository.save(pet);
        }
}
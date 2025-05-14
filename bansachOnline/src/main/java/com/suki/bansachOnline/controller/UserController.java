package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.Order;
import com.suki.bansachOnline.model.User;
import com.suki.bansachOnline.respository.OrdersRepository;
import com.suki.bansachOnline.securityConfig.JwtUtil;
import com.suki.bansachOnline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;import java.time.LocalDate;
import java.util.HashMap; // Thêm import này
import java.util.Map;
import java.util.Optional;
import java.util.UUID;@Controller
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OrdersRepository ordersRepository;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, OrdersRepository ordersRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.ordersRepository = ordersRepository;
    }

//    @ModelAttribute
//    public void addUserToModel(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
//        if (oAuth2User != null) {
//            String providerId = oAuth2User.getAttribute("sub") != null ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
//            String provider = oAuth2User.getAttribute("sub") != null ? "google" : "facebook";
//            Optional<User> user = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
//            if (user.isPresent()) {
//                model.addAttribute("loggedInUser", user.get());
//            }
//        }
//    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "/";
    }

//    @GetMapping("/register")
//    public String showRegisterPage(@RequestParam String providerId, @RequestParam String provider, Model model) {
//        Optional<User> user = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
//        if (user.isPresent()) {
//            model.addAttribute("providerId", providerId);
//            model.addAttribute("name", user.get().getName());
//            model.addAttribute("provider", provider);
//            return "dangky";
//        }
//        return "redirect:/";
//    }
@PostMapping("/register")
@ResponseBody
public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> registerRequest) {
    String fullName = registerRequest.get("fullName");
    String phoneNumber = registerRequest.get("phone");
    String address = registerRequest.get("address");
    String email = registerRequest.get("email");
    String birthDate = registerRequest.get("birthDate");
    String password = registerRequest.get("password");
    String confirmPassword = registerRequest.get("confirmPassword");

    Map<String, String> response = new HashMap<>();

    // Kiểm tra các trường bắt buộc
    if (fullName == null || fullName.trim().isEmpty()) {
        response.put("error", "Họ tên không được để trống");
        return ResponseEntity.status(400).body(response);
    }
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
        response.put("error", "Số điện thoại không được để trống");
        return ResponseEntity.status(400).body(response);
    }
    if (email == null || email.trim().isEmpty()) {
        response.put("error", "Email không được để trống");
        return ResponseEntity.status(400).body(response);
    }
    if (password == null || password.trim().isEmpty()) {
        response.put("error", "Mật khẩu không được để trống");
        return ResponseEntity.status(400).body(response);
    }
    if (birthDate == null || birthDate.trim().isEmpty()) {
        response.put("error", "Ngày sinh không được để trống");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra định dạng số điện thoại (ví dụ: 10 số, bắt đầu bằng 0)
    if (!phoneNumber.matches("0[0-9]{9}")) {
        response.put("error", "Số điện thoại không hợp lệ");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra định dạng email
    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        response.put("error", "Email không hợp lệ");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra mật khẩu khớp
    if (!password.equals(confirmPassword)) {
        response.put("error", "Mật khẩu xác nhận không khớp");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra số điện thoại đã tồn tại
    if (userService.findByPhoneNumber(phoneNumber).isPresent()) {
        response.put("error", "Số điện thoại đã được sử dụng");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra email đã tồn tại
    if (userService.findByEmail(email).isPresent()) {
        response.put("error", "Email đã được sử dụng");
        return ResponseEntity.status(400).body(response);
    }

    // Tạo user mới
    User user = new User();
    user.setName(fullName);
    user.setPhoneNumber(phoneNumber);
    user.setAddress(address);
    user.setEmail(email);
    try {
        user.setDateOfBirth(LocalDate.parse(birthDate));
    } catch (Exception e) {
        response.put("error", "Ngày sinh không hợp lệ");
        return ResponseEntity.status(400).body(response);
    }
    user.setPassword(passwordEncoder.encode(password));
    user.setRole("USER");
    userService.saveUser(user);

    // Tạo token
    String token = jwtUtil.generateToken(user);
    response.put("token", token);
    response.put("phoneNumber", user.getPhoneNumber());
    response.put("role", user.getRole());
    response.put("name", user.getName());
    response.put("email", user.getEmail());

    return ResponseEntity.ok(response);
}
    @PostMapping("/save-profile")
    public String saveProfile(@RequestParam String providerId,
                              @RequestParam String provider,
                              @RequestParam String name,
                              @RequestParam LocalDate dateOfBirth,
                              @RequestParam String address,
                              @RequestParam String phoneNumber,
                              @RequestParam String password) {
        Optional<User> userOptional = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(name);
            user.setDateOfBirth(dateOfBirth);
            user.setAddress(address);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(password);
            userService.saveUser(user);
            return "redirect:/";
        }
        return "redirect:/";
    }

    @GetMapping("/home")
    public String showHomePage(Model model, @RequestParam String providerId, @RequestParam String provider) {
        Optional<User> user = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
        if (user.isPresent()) {
            model.addAttribute("name", user.get().getName());
            model.addAttribute("email", user.get().getEmail());
            model.addAttribute("phoneNumber", user.get().getPhoneNumber());
            model.addAttribute("picture", user.get().getPicture());
            return "thongtincanhan";
        }
        return "redirect:/";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        String phoneNumber = loginRequest.get("phoneNumber");
        String password = loginRequest.get("password");
        Map<String, String> response = new HashMap<>();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            response.put("error", "Số điện thoại không được để trống");
            return ResponseEntity.status(400).body(response);
        }
        if (password == null || password.trim().isEmpty()) {
            response.put("error", "Mật khẩu không được để trống");
            return ResponseEntity.status(400).body(response);
        }
        Optional<User> userOpt = userService.findByPhoneNumber(phoneNumber);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Login User Role: " + user.getRole()); // Ghi log
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtil.generateToken(user);
                response.put("token", token);
                response.put("role", user.getRole());
                response.put("email", user.getEmail());
                response.put("phoneNumber", user.getPhoneNumber());
                response.put("name", user.getName());
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Mật khẩu không đúng");
                return ResponseEntity.status(401).body(response);
            }
        } else {
            response.put("error", "Số điện thoại không tồn tại");
            return ResponseEntity.status(404).body(response);
        }
    }

    @ModelAttribute
    public void addUserToModel(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = null;
            if (oAuth2User != null) {
                String providerId = oAuth2User.getAttribute("sub") != null ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
                String provider = oAuth2User.getAttribute("sub") != null ? "google" : "facebook";
                Optional<User> userOpt = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
                user = userOpt.orElse(null);
            } else if (authentication.getPrincipal() instanceof User) {
                user = (User) authentication.getPrincipal();
            }
            if (user != null) {
                model.addAttribute("loggedInUser", user);
                model.addAttribute("name", user.getName());
                model.addAttribute("phoneNumber", user.getPhoneNumber());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("dateOfBirth", user.getDateOfBirth());
                // Lấy danh sách đơn hàng
                Pageable pageable = PageRequest.of(0, 10); // Mặc định trang đầu, 10 đơn hàng/trang
                Page<Order> orders = ordersRepository.findByUserId(user.getId(), pageable);
                model.addAttribute("orders", orders.getContent());
            }
        }
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        return "thongtincanhan";
    }

}


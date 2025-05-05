package com.suki.bansachOnline.controller;

import com.suki.bansachOnline.model.User;
import com.suki.bansachOnline.securityConfig.JwtUtil;
import com.suki.bansachOnline.service.UserService;
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

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
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

    @ModelAttribute
    public void addUserToModel(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (oAuth2User != null) {
                String providerId = oAuth2User.getAttribute("sub") != null ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id");
                String provider = oAuth2User.getAttribute("sub") != null ? "google" : "facebook";
                Optional<User> user = "google".equals(provider) ? userService.findByGoogleId(providerId) : userService.findByFacebookId(providerId);
                if (user.isPresent()) {
                    model.addAttribute("loggedInUser", user.get());
                }
            } else if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                model.addAttribute("loggedInUser", user);
            }
        }
    }

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
    String phoneNumber = registerRequest.get("phone");
    String password = registerRequest.get("password");
    String confirmPassword = registerRequest.get("confirmPassword");

    Map<String, String> response = new HashMap<>();

    // Kiểm tra số điện thoại
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
        response.put("error", "Số điện thoại không được để trống");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra mật khẩu
    if (password == null || password.trim().isEmpty()) {
        response.put("error", "Mật khẩu không được để trống");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra xác nhận mật khẩu
    if (!password.equals(confirmPassword)) {
        response.put("error", "Mật khẩu xác nhận không khớp");
        return ResponseEntity.status(400).body(response);
    }

    // Kiểm tra số điện thoại đã tồn tại
    if (userService.findByPhoneNumber(phoneNumber).isPresent()) {
        response.put("error", "Số điện thoại đã được sử dụng");
        return ResponseEntity.status(400).body(response);
    }

    // Tạo user mới
    User user = new User();
    user.setPhoneNumber(phoneNumber);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole("USER");
    userService.saveUser(user);

    // Tạo token
    String token = jwtUtil.generateToken(user);
    response.put("token", token);
    response.put("phoneNumber", user.getPhoneNumber());
    response.put("role", user.getRole());

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

}


package kr.ac.hansung.cse.hellospringdatajpa.controller;

import kr.ac.hansung.cse.hellospringdatajpa.entity.MyUser;
import kr.ac.hansung.cse.hellospringdatajpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")  // 클래스 레벨에서 ADMIN 권한 체크
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home")
    public String adminHome() {
        return "adminhome";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<MyUser> allUsers = userRepository.findAll();
        model.addAttribute("users", allUsers);
        return "admin/users";
    }
}
package com.example.WEB_TOKEN;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;



    @GetMapping("/")
    public String log() {
        return "home";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(User user, RedirectAttributes redirectAttributes, HttpSession session) {
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null && user.getPassword().equals(existingUser.getPassword())) {
            // Generate the JWT token
            String token = jwtUtil.generateToken(existingUser.getEmail());

            // Store login details in the session
            session.setAttribute("jwtToken", token);
            session.setAttribute("loggedInUser", existingUser);

            // Redirect based on the user type
            if (existingUser.getType().equals("user")) {
                return "redirect:/user-detail";
            } else if (existingUser.getType().equals("admin")) {
                return "redirect:/admin-detail";
            }
        }

        // Redirect to the login page with an error if login fails
        return "redirect:/login?error";
    }

    @GetMapping("/user-detail")
    public String userDetail(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("jwtToken");

        // Validate the token stored in the session
        if (token != null && jwtUtil.validateToken(token)) {

            User loggedInUser = (User) session.getAttribute("loggedInUser");
            model.addAttribute("userName", loggedInUser.getName());


            // Additional logic for user details can go here
            return "user-detail";
        } else {
            // Redirect to login if token is invalid or not found
            return "redirect:/login";
        }
    }

    @GetMapping("/admin-detail")
    public String adminDetail(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("jwtToken");

        // Validate the token stored in the session
        if (token != null && jwtUtil.validateToken(token)) {


            User loggedInUser = (User) session.getAttribute("loggedInUser");
            model.addAttribute("userName", loggedInUser.getName());


            // Additional logic for admin details can go here
            return "admin-detail";
        } else {
            // Redirect to login if token is invalid or not found
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the session to log the user out
        session.invalidate();

        // Redirect to the login page after logout
        return "redirect:/login";
    }
}



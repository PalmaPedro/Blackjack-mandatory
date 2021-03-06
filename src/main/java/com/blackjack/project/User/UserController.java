package com.blackjack.project.User;


import com.blackjack.project.User.Security.SecurityService;
import com.blackjack.project.User.Security.UserService;
import com.blackjack.project.User.Security.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;


    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        } else{
            userService.save(userForm);

            securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

            return "redirect:/";
        }
    }


    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        if (principal == null){
            return "redirect:/login";
        }else {
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user);
            return "profile";
        }
    }

    @GetMapping("/ranks")
    public String ranks(Model model) {

        model.addAttribute("rankedList", userRepository.findAll(Sort.by("coinAmount").descending()));
        return "ranking";
    }



}


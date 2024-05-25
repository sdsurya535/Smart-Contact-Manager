package com.boot.smc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.boot.smc.dao.UserRepo;
import com.boot.smc.entities.User;
import com.boot.smc.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Autowired
	private UserRepo userRepo;

	@GetMapping(value = { "/home", "/" })
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {

		model.addAttribute("title", "About - Smart Contact Manager");

		return "about";
	}

	@GetMapping("/signup")
	public String signUp(Model model) {

		model.addAttribute("title", "SignUp - Smart Contact Manager");
		model.addAttribute("user", new User());

		return "signup";

	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {

				System.out.println("Not Checked");
				throw new Exception(" You Haven't Checked The Terms and Conditions");
			}
			
			if(result.hasErrors()) {
				
				System.out.println("ERROR"+result);
				
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement: " + agreement);
			System.out.println("USER: " + user);

			User save = userRepo.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered", "alert-success"));

			return "signup";

		} catch (Exception e) {
			e.printStackTrace();

			model.addAttribute("user", user);

			session.setAttribute("message", new Message("Something Went Wrong: " + e.getMessage(), "alert-danger"));

			return "signup";
		}

	}
	
	@GetMapping("/signin")
	public String login(Model model) {
		
		model.addAttribute("title","Signin : Smart Contact Manager");
		
		
		return "login";
	}
	
	
	

}

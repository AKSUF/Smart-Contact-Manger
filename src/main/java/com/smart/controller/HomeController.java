package com.smart.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repo.UserRepository;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String startpage() {
		System.out.println("");
		System.out.println("Thi is start page .Here all all pageslink ");
		return "start";

	}

	@GetMapping("/home")
	public String home(Model model) {
		System.out.println("This is for home page");
		model.addAttribute("title", "Smart contact manager");
		return "home";

	}

	@GetMapping("/base")
	public String base() {
		System.out.println("This is for base page ,here bootsap  willl be inherited in other page");
		return "base";

	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-smart contact manger");
		System.out.println("This is for about page ");

		return "about";

	}

	@GetMapping("/signup")
	public String signup(Model model) {
		System.out.println("This is for get datav from user");

		model.addAttribute("user", new User());

		return "signup";

	}

	@PostMapping("/do_register")
	public String registeruser(@ModelAttribute("user") User user, Model model,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("You have not agreed the terms & Condition");
				throw new Exception("You have not agreed the terms & Condition");
			}
			user.setRole("ROLE_USER");
			
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println(agreement);
			System.out.println("User" + user);
			model.addAttribute("user", user);

			User result = this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully!!", "alert-success"));
			System.out.println("result" + result);

		} catch (Exception e) {

			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Somthing went wrong!!" + e.getMessage(), "alert-danger"));

		}
		return "signup";

	}
	
	@GetMapping("/signin")
	public String login(Model model) {
		System.out.println("login page for to login user in the portal");
		model.addAttribute("title","Login page");
		return "login";
		
	}
	@GetMapping("/login_fail")
	public String login_fail() {
		return "login_fail";
		
	}


}

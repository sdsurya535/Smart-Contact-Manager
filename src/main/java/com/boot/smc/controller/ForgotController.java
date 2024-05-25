package com.boot.smc.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.boot.smc.dao.UserRepo;
import com.boot.smc.entities.User;
import com.boot.smc.helper.Message;
import com.boot.smc.services.GmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	@Autowired
	private GmailService gmailService;
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	Random random = new Random(1000);

	@GetMapping("/forgot-form")
	public String openForgotPassword(Model model) {

		model.addAttribute("title", "Forgot Password");

		return "forgot-form";

	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("mail") String mail, HttpSession session,Model model) {

		model.addAttribute("title","Verify OTP");
		
		//checking the mail
		User userMail = userRepo.getUserByUserName(mail);
		
		
		if(userMail != null) {
			
			// generating otp

			int otp = random.nextInt(99999);
			

			// send otp
			String from = "codingsurya335@gmail.com";
			String to = mail;
			String subject = "OTP from Smart Contact Manager";
			String text = "<h1> Your OTP is " + otp + "</h1>";

			boolean sendEmail = gmailService.sendEmail(to, from, subject, text);

			if (sendEmail) {
				session.setAttribute("otp", otp);
				session.setAttribute("mail", mail);

				return "verify-otp";

			} else {

				session.setAttribute("message", new Message("Error sending message", "alert-danger"));
				return "forgot-form";

			}
			
		}else {
			session.setAttribute("message", new Message("This mail id does not exists","alert-danger"));
			return "forgot-form";
		}

		

	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") Integer otp, HttpSession session) {

		Integer myOtp = (Integer) session.getAttribute("otp");
		
		if(myOtp.equals(otp) && otp != null) {
			
			return "passchange-form";
		}else {
			session.setAttribute("message", new Message("You have entered a wrong OTP","alert-danger"));
			return "verify-otp";
		}

	}
	
	@PostMapping("/change-pass")
	public String changePassword(@RequestParam("password") String password,HttpSession session) {
		
		String mail = (String) session.getAttribute("mail");
		
		User userMail = userRepo.getUserByUserName(mail);
		
		userMail.setPassword(bCryptPasswordEncoder.encode(password));
		userRepo.save(userMail);
		
		session.setAttribute("message", new Message("","alert-success"));
		return "redirect:/signin?change=Your password has been changed successfully";
		
		
		
	}

}

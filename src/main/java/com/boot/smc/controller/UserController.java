package com.boot.smc.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.boot.smc.dao.ContactRepo;
import com.boot.smc.dao.MyOrderRepo;
import com.boot.smc.dao.UserRepo;
import com.boot.smc.entities.Contact;
import com.boot.smc.entities.MyOrder;
import com.boot.smc.entities.User;
import com.boot.smc.helper.Message;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private MyOrderRepo myOrderRepo;

	// Adding all the common data to all the handlers
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();

//		System.out.println("UserName: "+ userName);

		User userByUserName = userRepo.getUserByUserName(userName);

//		System.out.println(userByUserName);

		model.addAttribute("user", userByUserName);
	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");

		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String contactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add-contact-form";
	}

	@PostMapping("/process-contact")
	public String addContact(@Valid @ModelAttribute Contact contact, BindingResult result,
			@RequestParam("image") MultipartFile file, Model model, Principal principal, HttpSession session) {
		try {
			String user = principal.getName();

			User userName = userRepo.getUserByUserName(user);

			if (result.hasErrors()) {

				System.out.println(result);

				model.addAttribute("contact", contact);

				return "normal/add-contact-form";
			}

			if (file.isEmpty()) {

				// Error message file not found
				System.out.println("File Not Found");

				contact.setImageCon("contact.png");

//			throw new Exception("File Not Found");

			} else {

				contact.setImageCon(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			contact.setUser(userName);

			userName.getContacts().add(contact);

			userRepo.save(userName);

			session.setAttribute("message", new Message("Successfully Added", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error: " + e.getMessage());
			session.setAttribute("message", new Message("Something Went Wrong: " + e.getMessage(), "alert-danger"));
		}

		System.out.println(contact);

		return "normal/add-contact-form";
	}

	@GetMapping("/show-contacts/{page}")
	public String viewContacts(@PathVariable("page") int page, Model model, Principal principal) {

		String userName = principal.getName();

		User user = userRepo.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 3);

		Page<Contact> contactByUser = contactRepo.getContactByUser(user.getId(), pageable);

		model.addAttribute("title", "Show Contacts");
		model.addAttribute("contact", contactByUser);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contactByUser.getTotalPages());

		return "normal/show-contacts";

	}

	@GetMapping("/contact/{cId}")
	public String showContactDetails(@PathVariable("cId") Integer cid, Model model, Principal principal) {

//		System.out.println("CID"+cid);

		Optional<Contact> byId = contactRepo.findById(cid);
		Contact contact = byId.get();

		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);

		}

		return "normal/contact-details";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal,
			HttpSession session) {

		Optional<Contact> byId = contactRepo.findById(cid);
		Contact user = byId.get();

		String name = principal.getName();
		User userName = userRepo.getUserByUserName(name);

		if (userName.getId() == user.getUser().getId()) {

			contactRepo.delete(user);

			session.setAttribute("message", new Message("Contact Deleted Sucessfully", "alert-success"));

		} else {

			session.setAttribute("message", new Message("You Don't Have Access to This Contact", "alert-danger"));
		}

		return "redirect:/user/show-contacts/0";

	}

	@GetMapping("/update-form/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model, Principal principal, HttpSession session) {

		model.addAttribute("title", "Update Contact");

		Contact contact = contactRepo.findById(cid).get();

		String name = principal.getName();
		User userName = userRepo.getUserByUserName(name);

		if (userName.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);

		} else {

			session.setAttribute("message", new Message("You Don't Have Access to This Contact", "alert-danger"));
			return "redirect:/user/show-contacts/0";
		}

		return "normal/update";
	}

	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("image") MultipartFile file, Model model,
			HttpSession session, Principal principal) {

		Contact contact2 = contactRepo.findById(contact.getCid()).get();

		try {

			if (!file.isEmpty()) {

				// delete File

				File deleteFile = new ClassPathResource("static/images").getFile();
				File file4 = new File(deleteFile, contact2.getImageCon());
				file4.delete();

				// update File
				File file2 = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImageCon(file.getOriginalFilename());

			} else {

				contact.setImageCon(contact2.getImageCon());
			}

			User user = userRepo.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactRepo.save(contact);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "redirect:/user/contact/" + contact.getCid();

	}

	@GetMapping("/profile")
	public String userDashboard(Model model) {

		model.addAttribute("title", "Your Profile");

		return "normal/profile";

	}

	@GetMapping("/settings")
	public String settings(Model model) {

		model.addAttribute("title", "settings");

		return "normal/settings";

	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		String name = principal.getName();
		User currUser = userRepo.getUserByUserName(name);

		System.out.println(currUser.getPassword() + " " + oldPassword);

		if (bCryptPasswordEncoder.matches(oldPassword,currUser.getPassword())) {

			currUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepo.save(currUser);
			session.setAttribute("message", new Message("Your password changed successfully", "alert-success"));
		} else {
				
			session.setAttribute("message", new Message("Please enter your old password", "alert-danger"));
		}

		return "normal/settings";

	}
	
	@PostMapping("/create-order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data , Principal principal) throws Exception {
		
//		System.out.println(data);
		
		int amnt = Integer.parseInt(data.get("amount").toString());
		
		var client = new RazorpayClient("rzp_test_RPMjbVWpGIUtWd","EJPnleVn8LYg6Oc1BpbIBKXz");
		
		JSONObject ob = new JSONObject();
		ob.put("amount", amnt*100);
		ob.put("currency","INR");
		ob.put("receipt", "txn_235425");
		
		Order orders= client.orders.create(ob);
//		System.out.println(orders);
		
		
		//save order in database
		MyOrder myOrder = new MyOrder();
		myOrder.setId(orders.get("id"));
		myOrder.setAmount(orders.get("amount").toString());
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setReceipt(orders.get("receipt"));
		myOrder.setUser(userRepo.getUserByUserName(principal.getName()));
		
		myOrderRepo.save(myOrder);
		
		
		return orders.toString();
	}
	
	@PostMapping("/update-order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
		
		MyOrder myOrder = myOrderRepo.findById(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		myOrderRepo.save(myOrder);
		
		return ResponseEntity.ok(Map.of("msg","updated"));
		
	}

}

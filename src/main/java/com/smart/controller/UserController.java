package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repo.ContactRepository;
import com.smart.repo.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/user_dashboard")
	public String dashboard(@ModelAttribute("user") User user, Model model, Principal principal) {
		System.out.println("user_dashboard page only for user");

		String username = principal.getName();
		System.out.println("Username" + username);

		User userdetails = userRepository.getUserByUserName(username);
		System.out.println("User" + userdetails);

		model.addAttribute("user", userdetails);
		System.out.println("To show data in dashboard");
		return "normal/user_dashboard.html";

	}

	@GetMapping("/login_fail")
	public String login_fail() {
		return "login_fail";

	}

	@GetMapping("/addcontact")
	public String addcontact(@ModelAttribute("user") User user, Model model, Principal principal) {

		System.out.println("user_dashboard page only for user");

		String username = principal.getName();
		System.out.println("Username" + username);

		User userdetails = userRepository.getUserByUserName(username);
		System.out.println("User" + userdetails);

		model.addAttribute("user", userdetails);
		System.out.println("To show data in dashboard");

		return "normal/addcontact.html";

	}

	@PostMapping("/process-contact")
	public String procescontact(@ModelAttribute("contact") Contact contact, @ModelAttribute("user") User user,
			Model model, Principal principal, ModelMap map, @RequestParam("profileImage") MultipartFile file,
			HttpSession session) {

		System.out.println("user_dashboard page only for user");

		try {
			String name = principal.getName();
			User userbyname = this.userRepository.getUserByUserName(name);

			if (file.isEmpty()) {
				// if the is empty then try our message
				System.out.println("File is upload");
				contact.setImage("contact.png");
			} else {

				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}

			contact.setUser(userbyname);

			userbyname.getContact().add(contact);

			this.userRepository.save(userbyname);
			System.out.println(userbyname);

			String username = principal.getName();
			System.out.println("Username" + username);
			User userdetails = userRepository.getUserByUserName(username);
			System.out.println("User" + userdetails);
			model.addAttribute("user", userdetails);
			System.out.println("To show data in dashboard");
			System.out.println("DATA" + contact);

			// message sucess

			session.setAttribute("message", new Message("Your contact is added", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR" + e.getMessage());

			/* error message */
			session.setAttribute("message", new Message("Something went wrong,try again", "danger"));

		}

		return "normal/addcontact";

	}

//show contacts
	// pagination
	@GetMapping("/showcontacts/{page}")
	public String home(@PathVariable("page") Integer page, Model model, @ModelAttribute("user") User user,
			Principal principal) {

		System.out.println("This is show contact");
		model.addAttribute("title", "Show user contacts");
		String userName = principal.getName();

		User users = this.userRepository.getUserByUserName(userName);
//contact per page
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactByUser(users.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		/* partofnavbar */

		System.out.println("user_dashboard page only for user");
		String username = principal.getName();
		System.out.println("Username" + username);
		User userdetails = userRepository.getUserByUserName(username);
		System.out.println("User" + userdetails);

		model.addAttribute("user", userdetails);
		System.out.println("To show data in dashboard");

		/* partofnavbar */
		return "normal/show_contacts";

	}
	@GetMapping("/{id}/contact")
	public String showcontactdetai(@PathVariable("id")Integer id, Model model, @ModelAttribute("user") User user,
			Principal principal) {
		System.out.println("CID"+id);
Optional<Contact>contactOptional=this.contactRepository.findById(id);
		Contact contact=contactOptional.get();
		model.addAttribute("contact",contact);
		
		
		
		/* partofnavbar */

		System.out.println("user_dashboard page only for user");
		String username = principal.getName();
		System.out.println("Username" + username);
		if(user.getId()==contact.getUser().getId()) {
			
			model.addAttribute("contact",contact);
		
		}
		User userdetails = userRepository.getUserByUserName(username);
		System.out.println("User" + userdetails);

		model.addAttribute("user", userdetails);
		System.out.println("To show data in dashboard");

		/* partofnavbar */
		return "normal/contact_detail";
		
	}
	
	
	//delete controller handeler
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id")Integer id,Model model,HttpSession session) {
	
	
		Contact contact=this.contactRepository.findById(id).get();
		System.out.println(contact.getId());
		contact.setUser(null);
		this.contactRepository.delete(contact);
		System.out.println("Deleted"+contact.getId());
		session.setAttribute("message",new Message("Contact deleted succesfully","success"));
		return "redirect:/user/showcontacts/0";
		
	}
	//open updateform handelere
	
	@PostMapping("/update-contact/{id}")
	public String update(@PathVariable("id")Integer id,Model model, @ModelAttribute("user") User user,
			Principal principal) {
		Optional<Contact>contactOptional=this.contactRepository.findById(id);
		Contact contact=contactOptional.get();
		model.addAttribute("contact",contact);
		
		
		/* partofnavbar */

		System.out.println("user_dashboard page only for user");
		String username = principal.getName();
		System.out.println("Username" + username);
		User userdetails = userRepository.getUserByUserName(username);
		System.out.println("User" + userdetails);
		model.addAttribute("title", "User update form");
		model.addAttribute("user", userdetails);
		System.out.println("To show data in dashboard");

		/* partofnavbar */
		
		return "normal/update_form";
		
	}
	//update contact handeler
	
	@PostMapping("/process-update")
	public String updatehandeler(Model model,@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,HttpSession session,Principal principal) {
		Contact oldcontactDetail=this.contactRepository.findById(contact.getId()).get();
		try {
			
	//Contact oldcontactDetail=this.contactRepository.findById(contact.getId()).get();
			
		//image
		if(!file.isEmpty()) {
			//rewrite
			//delete old photo
			File deleteFile = new ClassPathResource("static/img").getFile();
			//Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
	File file1=new File(deleteFile,oldcontactDetail.getImage());
	file1.delete();
			
			//update 
			File savefile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is updated");
			contact.setImage(file.getOriginalFilename());
			
			
		}else {
			contact.setImage(oldcontactDetail.getImage());
		}
		
		User user=this.userRepository.getUserByUserName(principal.getName());
		contact.setUser(user);
		this.contactRepository.save(contact);
		
		session.setAttribute("message",new Message("Your contct is updated","success"));
		
		session.setAttribute("message",new  Message("Your contact is updated","success"));
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("CONTACT NAME"+contact.getName());
		System.out.println("CONTACT ID"+contact.getId());
		
		
		
		return "redirect:/user/user_dashboard";
		
	}
@GetMapping("/profile")
	public String yourprofile(Model model, @ModelAttribute("user") User user,
			Principal principal) {
		

	
	
	
	/* partofnavbar */

	System.out.println("user_dashboard page only for user");
	String username = principal.getName();
	System.out.println("Username" + username);
	User userdetails = userRepository.getUserByUserName(username);
	System.out.println("User" + userdetails);
	model.addAttribute("title", "Profile Page");
	model.addAttribute("user", userdetails);
	System.out.println("To show data in dashboard");

	/* partofnavbar */
		return "normal/profile";
		
	}
}
	

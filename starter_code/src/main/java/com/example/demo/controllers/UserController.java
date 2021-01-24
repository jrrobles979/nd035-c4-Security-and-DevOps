package com.example.demo.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	public static final Logger log = LogManager.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	//@Autowired
	//private AuthenticationManager authenticationManager;

	//@Autowired
	//private MyUserDetailsService myUserDetailsService;


	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		Cart cartSaved = cartRepository.save(cart);
		user.setCart(cart);

		if (  createUserRequest.getPassword().length()<7 || !createUserRequest.getPassword().equalsIgnoreCase(createUserRequest.getConfirmPassword()) ){
			log.error("Invalid password for user:  " + createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(  bCryptPasswordEncoder.encode( createUserRequest.getPassword()));

		/*User userSaved = null ;
		try {
			 userSaved = userRepository.save(user);
		}catch (Exception e){
			e.printStackTrace();
		}

		if(userSaved == null){
			log.error("Error creating user: '{}'" , createUserRequest.getUsername());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}*/

		User userSaved = userRepository.save(user);
		log.info("User created:" + user.getUsername());
		return ResponseEntity.ok(user);
	}


	/*@PostMapping("/login")
	public ResponseEntity login(@RequestBody LoginUserRequest loginUserRequest) {

		try {
		 Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginUserRequest.getUsername(),
							loginUserRequest.getPassword()
					)
			);

		} catch (BadCredentialsException e) {
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body(SecurityUtil.INVALID_CREDENTIALS);
		}

		//final UserDetails userDetails = myUserDetailsService.loadUserByUsername(loginUserRequest.getUsername());
		//User user = userRepository.findByUsername(userDetails.getUsername());
		return ResponseEntity.ok("");
	}*/





}

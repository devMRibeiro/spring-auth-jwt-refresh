package com.mrsystems.spring.auth.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mrsystems.spring.auth.enums.RoleEnum;
import com.mrsystems.spring.auth.model.Role;
import com.mrsystems.spring.auth.model.User;
import com.mrsystems.spring.auth.payload.request.LoginRequest;
import com.mrsystems.spring.auth.payload.request.SignUpRequest;
import com.mrsystems.spring.auth.payload.response.MessageResponse;
import com.mrsystems.spring.auth.payload.response.UserInfoResponse;
import com.mrsystems.spring.auth.repository.RoleRepository;
import com.mrsystems.spring.auth.repository.UserRepository;
import com.mrsystems.spring.auth.security.jwt.JwtUtils;
import com.mrsystems.spring.auth.security.service.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder encoder;
	private JwtUtils jwtUtils;

	public AuthController(
			AuthenticationManager authenticationManager,
			UserRepository userRepository,
			RoleRepository roleRepository,
			PasswordEncoder encoder,
			JwtUtils jwtUtils) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.encoder = encoder;
		this.jwtUtils = jwtUtils;
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticaUser(@Valid @RequestBody LoginRequest loginRequest) {

		 Authentication auth = authenticationManager
	        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(auth);

		UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.body(new UserInfoResponse(
						userDetails.getId(),
						userDetails.getUsername(),
						userDetails.getEmail(),
						roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername()))
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));

		if (userRepository.existsByEmail(signUpRequest.getEmail()))
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));

		User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getEmail());
		
		Set<String> hsStrRoles = signUpRequest.getHsRole();
		Set<Role> hsRoles = new HashSet<Role>();

		if (hsStrRoles == null) {
			Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			hsRoles.add(userRole);

		} else {
			
			for (int i = 0; i < hsStrRoles.size(); i++) {
				String role = hsStrRoles.iterator().next();

				if (role.equals("admin")) {
					Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					hsRoles.add(adminRole);
				} else if (role.equals("mod")) {
					Role modRole = roleRepository.findByName(RoleEnum.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					hsRoles.add(modRole);
				} else {
					Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					hsRoles.add(userRole);

					throw new IllegalArgumentException("Unexpected value: ");
				}
			}
		}

		user.setRoles(hsRoles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You've been signed out!"));
	}
}
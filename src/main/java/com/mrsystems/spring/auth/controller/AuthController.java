package com.mrsystems.spring.auth.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;
	@Autowired private PasswordEncoder encoder;
	@Autowired private JwtUtils jwtUtils;

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

		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));
		
		Set<String> hsStrRoles = signUpRequest.getHsRole();
		HashSet<Role> hsRoles = new HashSet<Role>();

		if (hsStrRoles == null) {
			Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			hsRoles.add(userRole);

		} else {

			hsStrRoles.forEach(role -> {

				switch (role) {

				case "admin" :
					Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					hsRoles.add(adminRole);
					break;

				case "mod" :

					Role modRole = roleRepository.findByName(RoleEnum.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					hsRoles.add(modRole);
					break;

				default:

					Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					hsRoles.add(userRole);

					throw new IllegalArgumentException("Unexpected value: ");
				}
				
			});
		}

		user.setRoles(hsRoles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
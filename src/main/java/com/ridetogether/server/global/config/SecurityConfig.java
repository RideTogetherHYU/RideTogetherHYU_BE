package com.ridetogether.server.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridetogether.server.domain.member.dao.MemberRepository;
import com.ridetogether.server.global.security.application.CustomUserDetailsService;
import com.ridetogether.server.global.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.ridetogether.server.global.security.filter.JwtAuthenticationFilter;
import com.ridetogether.server.global.security.handler.LoginFailureHandler;
import com.ridetogether.server.global.security.handler.LoginSuccessJWTProvideHandler;
import com.ridetogether.server.global.security.application.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Lazy
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;
	private final ObjectMapper objectMapper;
	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final CorsProperties corsProperties;

	// 스프링 시큐리티 기능 비활성화
	@Bean
	public WebSecurityCustomizer configure() {
		return (web -> web.ignoring()
//				.requestMatchers(toH2Console())
				.requestMatchers("/fcm", "/static/**", "/h2-console/**",
						"/favicon.ico", "/error", "/swagger-ui/**",
						"/swagger-resources/**", "/v3/api-docs/**")
		);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http	.csrf(AbstractHttpConfigurer::disable)
				.headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // For H2 DB
				.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
						.configurationSource(corsConfigurationSource()))
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers( "/api/member/signup", "/api/member/login", "/api/member/isDuplicated", "/api/email/send").permitAll()
						.requestMatchers("/api/member/all").hasRole("ADMIN")
						.anyRequest().authenticated())
				.sessionManagement(session -> session
								.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				);
//		http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
		http
				.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
				.addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}


	@Bean
	public AuthenticationManager authenticationManager() throws Exception {//2 - AuthenticationManager 등록
		DaoAuthenticationProvider provider = daoAuthenticationProvider();//DaoAuthenticationProvider 사용
		return new ProviderManager(provider);
	}

	@Bean
	public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
		return new LoginSuccessJWTProvideHandler(jwtService, memberRepository);
	}

	@Bean
	public LoginFailureHandler loginFailureHandler(){
		return new LoginFailureHandler();
	}

	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
		JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
		jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
		jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
		return jsonUsernamePasswordLoginFilter;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationProcessingFilter(){
		return new JwtAuthenticationFilter(jwtService, memberRepository);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		corsConfiguration.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
		corsConfiguration.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
		corsConfiguration.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
		corsConfiguration.setExposedHeaders(Arrays.asList("Authorization", "Authorization-refresh"));
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setMaxAge(corsConfiguration.getMaxAge());

		UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();
		corsConfigSource.registerCorsConfiguration("/**", corsConfiguration);
		return corsConfigSource;
	}
}

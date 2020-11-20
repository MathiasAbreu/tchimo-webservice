package br.com.ufcg.back;

import br.com.ufcg.back.controllers.filters.TokenFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"br.com.ufcg.back",
		"br.com.ufcg.back.controllers",
		"br.com.ufcg.back.daos",
		"br.com.ufcg.back.entities",
		"br.com.ufcg.back.services"})
public class Tchimo {

	public static void main(String[] args) {

		SpringApplication.run(Tchimo.class, args);
	}

	@Bean
	public FilterRegistrationBean<TokenFilter> filterJwt() {

		FilterRegistrationBean<TokenFilter> filterRB = new FilterRegistrationBean<>();
		filterRB.setFilter(new TokenFilter());
		filterRB.addUrlPatterns("/api/usuarios","/api/turma");

		return filterRB;
	}

	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
		config.addAllowedHeader("*");
		config.addAllowedMethod(HttpMethod.DELETE);
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}

}

package com.example.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.example.demo.model.MonitoringArea;
import com.example.demo.model.Pattern;
import com.example.demo.model.PatternInstance;
import com.example.demo.model.PatternVariable;
import com.example.demo.repository.MonitoringAreaRepository;
import com.example.demo.repository.PatternInstanceRepository;
import com.example.demo.repository.PatternRepository;
import com.example.demo.repository.PatternVariableRepository;
import com.example.demo.service.PatternConstraintService;
import com.example.demo.service.RabbitMqService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static org.apache.log4j.helpers.LogLog.warn;

@SpringBootApplication
@ImportResource({"classpath*:application-context.xml"})
public class DemoApplication {
	@Autowired
	RabbitMqService rabbitMqService;
	@Autowired
	PatternRepository patternRepository;
	@Autowired
	PatternInstanceRepository patternInstanceRepository;
	@Autowired
	PatternVariableRepository patternVariableRepository;
	@Autowired
	PatternConstraintService patternConstraintService;
	@Autowired
    MonitoringAreaRepository monitoringAreaRepository;

	private static final String BASE_PATH = "templates/";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	ApplicationRunner init(){
		return args -> {
			this.initPatternRepo();
			MonitoringArea monitoringArea = new Gson().fromJson(fetchStatementFromFile("json/monitoring-area.json"), MonitoringArea.class);
			monitoringArea.getPatternInstances().forEach(patternInstance -> {
				String filePath = patternInstance.getName() + ".epl";
				try {
					patternInstance.setConstraintStatement(fetchStatementFromFile(filePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			this.monitoringAreaRepository.save(monitoringArea);
		};
	}


	private void initPatternRepo() throws IOException {
		Stream.of("Watchdog", "CircuitBreaker", "StaticWorkload").forEach(name -> {
			Pattern pattern = new Pattern();
			pattern.setName(name);
			String filePath = name + ".epl";
			try {
				pattern.setpConstraint(fetchStatementFromFile(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			patternRepository.save(pattern);
		});
	}

	private String fetchStatementFromFile(String filename) throws FileNotFoundException, IOException {
		String data = "";
		ClassPathResource cpr = new ClassPathResource(filename);
		try {
			byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
			data = new String(bdata, StandardCharsets.UTF_8);
		} catch (IOException e) {
			warn("IOException", e);
		}
		return data;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:4200")
						.allowedMethods("*");
			}
		};
	}


}

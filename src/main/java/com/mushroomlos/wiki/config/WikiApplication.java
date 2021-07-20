package com.mushroomlos.wiki.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

// ComponentScan支持扫描多个包
// @ComponentScan({"com.mushroomlos", "com.text"})
@ComponentScan("com.mushroomlos")
@SpringBootApplication
public class WikiApplication {

	private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WikiApplication.class);
		Environment env = app.run(args).getEnvironment();
		LOG.info("Launch successfully!");
		LOG.info("Host:\thttp://127.0.0.1:{}", env.getProperty("server.port"));
	}

}

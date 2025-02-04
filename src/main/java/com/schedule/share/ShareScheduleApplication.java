package com.schedule.share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/env.yml")
public class ShareScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShareScheduleApplication.class, args);
	}
}

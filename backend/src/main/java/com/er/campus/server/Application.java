package com.er.campus.server;

import com.er.campus.server.task.ActivityStatusScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.er.campus.server.repository") // 扫描Repository包
@EntityScan("com.er.campus.server.entity") // 扫描实体类
@CrossOrigin
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
    }

}

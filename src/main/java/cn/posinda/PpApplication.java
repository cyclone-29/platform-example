package cn.posinda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement
public class PpApplication {

    public static void main(String[] args) {
        SpringApplication.run(PpApplication.class, args);
    }

}


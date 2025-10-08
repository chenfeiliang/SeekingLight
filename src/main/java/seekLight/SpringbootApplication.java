package seekLight;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@MapperScan("seekLight.mapper")
@Slf4j
@EnableMPP
public class SpringbootApplication {

	public static void main(String[] args) {

	    SpringApplication.run(SpringbootApplication.class, args);
	}
}

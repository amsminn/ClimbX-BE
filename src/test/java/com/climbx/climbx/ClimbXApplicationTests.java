package com.climbx.climbx;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
	DataSourceAutoConfiguration.class
}) // 현재 유닛 테스트에서는 데이터베이스 연결이 필요하지 않으므로 DataSourceAutoConfiguration을 제외함.
class ClimbXApplicationTests {

	@Test
	void contextLoads() {
	}

}

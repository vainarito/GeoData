package com.example.geodata;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GeoDataApplicationTests {


	@Test
	void contextLoads() {
        String asd = "sonarqube";
        assertNotNull(asd);
	}

}

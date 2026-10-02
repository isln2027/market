package org.isln.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.json.GsonBuilderUtils;

@SpringBootApplication
public class MarketApplication {
	public static void main(String[] args)
    {
		SpringApplication.run(MarketApplication.class, args);
	}
}

package com.ecommerce.productservice;

import com.ecommerce.productservice.Dto.ProductRequest;
import com.ecommerce.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer=new MongoDBContainer("4.4.2");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongo.url", mongoDBContainer::getReplicaSetUrl);
	}
	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest=getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
        Assertions.assertEquals(1, productRepository.findAll().size());
	}
	@Test
	void shouldGetProduct() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Iphone12")
				.description("Iphone 12 128GB")
				.price(BigDecimal.valueOf(40000))
				.build();
	}

}

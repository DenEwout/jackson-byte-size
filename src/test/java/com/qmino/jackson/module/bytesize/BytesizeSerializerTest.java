package com.qmino.jackson.module.bytesize;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BytesizeSerializerTest {

	private static final int LENGTH = 10;
	private static final String SIMPLE_INPUT = "x".repeat(LENGTH * 2);
	private static final String SPECIAL_INPUT = "é".repeat(LENGTH * 2);

	private ObjectMapper objectMapper;

	static class TruncateNoSuffix {
		@ByteSize(length = LENGTH)
		@JsonProperty("test")
		private String test;

		public TruncateNoSuffix(String test) {
			this.test = test;
		}
	}

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ByteSizeModule());
	}

	@Test
	void shouldTruncateWithoutSuffix() throws Exception {
		TruncateNoSuffix dto = new TruncateNoSuffix(SIMPLE_INPUT);
		assertThat(objectMapper.writeValueAsString(dto)).isEqualTo("{\"test\":\"xxxxxxxxxx\"}");
	}

	static class TruncateWithSuffix {
		@ByteSize(length = 10, suffix = "...")
		@JsonProperty("test")
		private String test;

		public TruncateWithSuffix(String test) {
			this.test = test;
		}
	}

	@Test
	void shouldTruncateWithSuffix() throws Exception {
		TruncateWithSuffix dto = new TruncateWithSuffix(SIMPLE_INPUT);
		assertThat(objectMapper.writeValueAsString(dto)).isEqualTo("{\"test\":\"xxxxxxx...\"}");
	}

	static class TruncateWithUTF8 {
		@SuppressWarnings("DefaultAnnotationParam")
		@ByteSize(length = 10, suffix = "...", charset = "UTF-8")
		@JsonProperty("test")
		private String test;

		public TruncateWithUTF8(String test) {
			this.test = test;
		}
	}

	@Test
	void shouldTruncateWithUTF8() throws Exception {
		TruncateWithSuffix dto = new TruncateWithSuffix(SIMPLE_INPUT);
		assertThat(objectMapper.writeValueAsString(dto)).isEqualTo("{\"test\":\"xxxxxxx...\"}");
	}

	static class TruncateWithASCI {
		@ByteSize(length = 10, suffix = "...", charset = "US-ASCII")
		@JsonProperty("test")
		private String test;

		public TruncateWithASCI(String test) {
			this.test = test;
		}
	}

	@Test
	void shouldTruncateWithASCI() throws Exception {
		TruncateWithSuffix dto = new TruncateWithSuffix(SIMPLE_INPUT);
		assertThat(objectMapper.writeValueAsString(dto)).isEqualTo("{\"test\":\"xxxxxxx...\"}");
	}

	@Test
	void shouldTruncateSpecialWithASCI() throws Exception {
		TruncateWithSuffix dto = new TruncateWithSuffix(SPECIAL_INPUT);
		assertThat(objectMapper.writeValueAsString(dto)).isEqualTo("{\"test\":\"ééé...\"}");
	}
}

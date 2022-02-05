package com.qmino.jackson.module.bytesize;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class BytesizeSerializer extends StdScalarSerializer<String> implements ContextualSerializer {

	private final BeanProperty property;

	protected BytesizeSerializer() {
		super(String.class);
		this.property = null;
	}

	public BytesizeSerializer(BeanProperty property) {
		super(String.class);
		this.property = property;
	}

	/**
	 * Based on:
	 * https://stackoverflow.com/questions/119328/how-do-i-truncate-a-java-string-to-fit-in-a-given-number-of-bytes-once-utf-8-en
	 */
	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (property == null) {
			gen.writeString(value);
		} else {
			ByteSize byteSize = property.getAnnotation(ByteSize.class);

			Charset charset = Charset.forName(byteSize.charset());
			byte[] sba = value.getBytes(charset);
			int length = byteSize.length();
			if (!byteSize.suffix().isBlank() && byteSize.length() > byteSize.suffix().getBytes(charset).length) {
				length = byteSize.length() - byteSize.suffix().getBytes(charset).length;
			}
			if (sba.length > length) {
				CharsetDecoder decoder = charset.newDecoder();
				ByteBuffer bb = ByteBuffer.wrap(sba, 0, length);
				CharBuffer cb = CharBuffer.allocate(length);
				decoder.onMalformedInput(CodingErrorAction.IGNORE);
				decoder.decode(bb, cb, true);
				decoder.flush(cb);
				gen.writeString(new String(cb.array(), 0, cb.position()) + byteSize.suffix());
			} else {
				gen.writeString(value);
			}
		}
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
		return new BytesizeSerializer(property);
	}
}

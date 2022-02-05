package com.qmino.jackson.module.bytesize;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import java.util.Collections;

public class ByteSizeModule extends Module {

	@Override
	public String getModuleName() {
		return "bytesize";
	}

	@Override
	public Version version() {
		return new Version(0, 0, 1, "SNAPSHOT",
				"com.qmino", "jackson-byte-size-module");
	}

	@Override
	public void setupModule(SetupContext setupContext) {
		setupContext.addSerializers(new SimpleSerializers(Collections.singletonList(new BytesizeSerializer())));
	}
}

package com.siyuyong.http;

import java.nio.ByteBuffer;

public class FileDescriptor {
	public long size;
	public String contentType;
	public long lastModified;
	public ByteBuffer data;
}

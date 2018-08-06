package com.siyuyong.http;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.Date;

public class HttpResponse {
	private SelectionKey myKey;
	private SocketChannel channel;
	private long contentLength = -1;
	private long lastModified = -1;
	private String contentType;
	private String contentCharset = "; charset=utf-8";
	private CharBuffer headerBuffer = CharBuffer.allocate(1024);
	private static CharsetEncoder encoder =
		Charset.forName("UTF-8").newEncoder();
	private static String NEWLINE = "\r\n";
	private static String OK = "HTTP/1.1 200 OK" + NEWLINE;

	public HttpResponse(SelectionKey key) {
		myKey = key;
		channel = (SocketChannel) myKey.channel();
	}

	public void sendFile(ByteBuffer data) throws IOException {
		commitResponseHeader();
		while (data.hasRemaining())
			channel.write(data);
		data.rewind();
	}

	public void close() throws IOException {
		myKey.channel().close();
		myKey.selector().wakeup();
	}

	public void setContentLength(long i) {
		contentLength = i;
	}

	public void setContentType(String string) {
		contentType = string;
	}

	public void setLastModified(long l) {
		lastModified = l;
	}

	public void commitResponseHeader() throws IOException {
		headerBuffer.clear();
		headerBuffer.put(OK);
		appendHeaderValue("Last-Modified", new Date(lastModified).toString());
		appendHeaderValue("Content-Type", contentType + contentCharset);
		appendHeaderValue("Content-Length",contentLength + "");
		appendHeaderValue("Date", new Date().toString());
		appendHeaderValue("Connection", "Keep-Alive");
		appendHeaderValue("Server", "Keep-It-Simple/1.0");
		headerBuffer.put(NEWLINE);
		headerBuffer.flip();
		channel.write(encoder.encode(headerBuffer));
	}

	private void appendHeaderValue(String name, String value) {
		headerBuffer.put(name);
		headerBuffer.put(": ");
		headerBuffer.put(value);
		headerBuffer.put(NEWLINE);
	}

	public void sendError(int i, String msg)
		throws CharacterCodingException, IOException {
		System.out.println("not modified");
		headerBuffer.clear();
		headerBuffer.put("HTTP/1.1 " + i + " " + msg);
		headerBuffer.put("Date: " + new Date() + NEWLINE);
		headerBuffer.put(NEWLINE);
		headerBuffer.flip();
		channel.write(encoder.encode(headerBuffer));
	}
}
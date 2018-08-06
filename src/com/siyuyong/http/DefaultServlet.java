package com.siyuyong.http;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class DefaultServlet {
	private static HashMap cache = new HashMap();

	public void service(HttpRequest req, HttpResponse res) throws IOException {
		FileDescriptor fd = getDescriptor(req, req.getRequestedResource());
//		if (fd.lastModified <= req.getModifiedSince())
//			res.sendError(304, "NOT MODIFIED");
//		else
			sendFileContent(res, fd);
			
		if (req.closeAfterResponse())
			res.close();
	}

	private FileDescriptor getDescriptor(HttpRequest req, String url)
		throws IOException {
		return cache.containsKey(url)
			? (FileDescriptor) cache.get(url)
			: cacheResource(req, url);
	}
	
	private void sendFileContent(HttpResponse res, FileDescriptor fd)
		throws IOException {
		res.setContentLength(fd.size);
		res.setContentType(fd.contentType);
		res.setLastModified(fd.lastModified);
		res.sendFile(fd.data);
	}

	private FileDescriptor cacheResource(HttpRequest req, String url)
		throws IOException {
		String realPath = req.getServletContext().getRealPath(url);
		File f = new File(realPath);
		if (! f.exists()) return null;
		FileDescriptor fd = new FileDescriptor();	
		fd.lastModified = f.lastModified();
		fd.size = f.length();
		fd.contentType = req.getServletContext().getMimeType(url);
		FileChannel from = new FileInputStream(f).getChannel();
		fd.data = from.map(FileChannel.MapMode.READ_ONLY, 0, from.size());
		from.close();
		cache.put(url, fd);
		return fd;
	}
}
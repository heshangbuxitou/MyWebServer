package com.siyuyong.http;

import java.io.File;

public class ServletContext {

	private File contextRoot;

	public ServletContext(File root) {
		contextRoot = root;
	}

	public String getMimeType(String file) {
		int index = file.lastIndexOf('.');
		return (index++ > 0)
			? MimeTypes.getContentType(file.substring(index))
			: "unkown/unkown";
	}

	public String getRealPath(String arg0) {
		return new File(contextRoot, arg0).getAbsolutePath();
	}
}

/*
 * Copyright 2019 Laurent PAGE, Apache Licence 2.0
 */
package live.page.web.system.servlet.wrapper;

import live.page.web.system.Settings;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class BaseServletResponse extends HttpServletResponseWrapper {

	public BaseServletResponse(ServletResponse response) throws IOException {
		super((HttpServletResponse) response);
	}

	@Override
	public void setHeader(String name, String value) {
		if (!name.equalsIgnoreCase("Etag")) {
			super.setHeader(name, value);
		}
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		setHeaderNoCache();
		super.sendError(sc, msg);
	}

	@Override
	public void sendError(int sc) throws IOException {
		setHeaderNoCache();
		super.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		sendRedirect(location, 302);
	}

	public void sendRedirect(String location, int code) throws IOException {
		setHeaderNoCache();
		setStatus(code);
		setHeader("Connection", "close");
		setHeader("Location", location);
	}

	public static void setHeaderNoCache(HttpServletResponse resp) {
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", Settings.START_COUNT);

	}

	public void setHeaderNoCache() {
		setHeaderNoCache(this);
	}


	public static void setHeaderMaxCache(HttpServletResponse resp) {
		resp.setHeader("Cache-Control", "public, max-age=" + Settings.MAX_AGE);
		resp.setDateHeader("Expires", Settings.getHttpExpires());
	}

	public void setHeaderMaxCache() {
		setHeaderMaxCache(this);
	}

	public void sendTextError(int sc, String msg) {
		setHeaderNoCache();
		try {
			super.setStatus(sc);
			getWriter().write(msg);
		} catch (Exception e) {
			try {
				super.sendError(sc, msg);
			} catch (Exception ex) {

			}
		}
	}
}

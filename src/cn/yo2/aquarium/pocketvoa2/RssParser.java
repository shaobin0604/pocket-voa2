package cn.yo2.aquarium.pocketvoa2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.yo2.aquarium.pocketvoa2.util.Logger;

public class RssParser {
	private static final int CONN_TIME_OUT = 1000 * 30; // millis
	private static final int READ_TIME_OUT = 1000 * 30; // millis

	private static MyResponseHandler sHandler = new MyResponseHandler();

	private static class MyResponseHandler implements ResponseHandler<String> {

		public static String toString(final HttpEntity entity)
				throws IOException {
			if (entity == null) {
				throw new IllegalArgumentException(
						"HTTP entity may not be null");
			}

			PushbackInputStream in = new PushbackInputStream(entity
					.getContent());

			int c = in.read();

			if (c != 0xEF) {
				in.unread(c);
			} else if ((c = in.read()) != 0xBB) {
				in.unread(c);
				in.unread(0xEF);
			} else if ((c = in.read()) != 0xBF) {
				throw new IOException("Error UTF-8 file");
			}

			if (in == null) {
				return null;
			}
			if (entity.getContentLength() > Integer.MAX_VALUE) {
				throw new IllegalArgumentException(
						"HTTP entity too large to be buffered in memory");
			}
			int i = (int) entity.getContentLength();
			if (i < 0) {
				i = 4096;
			}
			String charset = "utf-8";
			Reader reader = new InputStreamReader(in, charset);
			CharArrayBuffer buffer = new CharArrayBuffer(i);
			try {
				char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
			} finally {
				reader.close();
			}
			return buffer.toString();
		}

		public String handleResponse(final HttpResponse response)
				throws HttpResponseException, IOException {
			StatusLine statusLine = response.getStatusLine();

			for (Header header : response.getAllHeaders()) {
				Logger.d(header.toString());
			}

			if (statusLine.getStatusCode() >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}

			return toString(response.getEntity());
		}

	}

	public static class Entry {
		long _id;
		String title;
		String url;
		String date;

		@Override
		public String toString() {
			return "Entry [_id=" + _id + ", title=" + title + ", url=" + url
					+ ", date=" + date + "]";
		}

	}

	private static ArrayList<Entry> parseText(String text) {
		ArrayList<Entry> list = new ArrayList<Entry>();

		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(new StringReader(text));
			int eventType = xpp.getEventType();

			String tagName = null;
			Entry entry = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					tagName = xpp.getName();

					if ("item".equals(tagName)) {
						entry = new Entry();
					} else if ("title".equals(tagName) && entry != null) {
						entry.title = xpp.nextText();
					} else if ("link".equals(tagName) && entry != null) {
						entry.url = xpp.nextText();
					} else if ("pubDate".equals(tagName) && entry != null) {
						entry.date = xpp.nextText();
					}

				} else if (eventType == XmlPullParser.END_TAG) {
					tagName = xpp.getName();

					if ("item".equals(tagName) && entry != null) {
						list.add(entry);
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public static List<Entry> parse2(String url) {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) new URL(url).openConnection();

			InputStream in = new BufferedInputStream(urlConnection
					.getInputStream());

			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"utf-8"));

			StringBuilder buffer = new StringBuilder();

			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			br.close();

			String body = buffer.toString();
			Logger.d(body);
			return parseText(body);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Collections.emptyList();

	}

	private static void writeToFile(String text) {
		FileWriter out = null;
		try {
			out = new FileWriter("/sdcard/voa.xml");
			out.write(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static List<Entry> parse(String url) {
		DefaultHttpClient httpClient = createHttpClient();

		HttpGet get = new HttpGet(url);

		try {
			String body = httpClient.execute(get, sHandler);

//			writeToFile(body);

			return parseText(body);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			get.abort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			get.abort();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		List<Entry> empty = Collections.emptyList();
		return empty;
	}

	private static DefaultHttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CONN_TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, READ_TIME_OUT);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		SingleClientConnManager cm = new SingleClientConnManager(params,
				schemeRegistry);

		DefaultHttpClient client = new DefaultHttpClient(cm, params);

		client.addRequestInterceptor(new HttpRequestInterceptor() {

			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				request
						.addHeader(
								"User-Agent",
								"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 GTB6 (.NET CLR 3.5.30729)");

			}
		});
		return client;
	}
}

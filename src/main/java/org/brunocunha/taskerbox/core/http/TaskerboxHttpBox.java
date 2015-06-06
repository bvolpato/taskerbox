package org.brunocunha.taskerbox.core.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.brunocunha.taskerbox.core.http.auth.NTLMSchemeFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Http Access Configuration Box It allows to configure Proxy Access via
 * Properties
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class TaskerboxHttpBox {

	private static TaskerboxHttpBox instance;

	@Getter @Setter
	private DefaultHttpClient httpClient;

	@Getter @Setter
	private boolean useProxy;

	@Getter @Setter
	private boolean authProxy;

	@Getter
	private boolean ntlmProxy;

	@Getter @Setter
	private boolean proxySocks;

	@Getter @Setter
	private String socksHost;

	@Getter @Setter
	private int socksPort;

	@Getter @Setter
	private String proxyHost;

	@Getter @Setter
	private int proxyPort;

	@Getter @Setter
	private String proxyDomain;

	@Getter @Setter
	private String proxyUser;

	@Getter @Setter
	private String proxyPassword;

	@Getter @Setter
	private String proxyWorkstation;

	@Getter @Setter
	private boolean useNtlm;

	public static void main(String[] args) throws Exception {
		TaskerboxHttpBox httpBox = TaskerboxHttpBox.getInstance();

		System.out.println(httpBox.getStringBodyForURL("http://google.com/"));

	}

	public static synchronized TaskerboxHttpBox getInstance()
			throws IOException {
		if (instance == null) {

			log.info("Creating new HttpClient...");

			Properties prop = new Properties();
			
			String hostName = InetAddress.getLocalHost().getHostName();
			if (hostName.equals("BR-PC")) {
				hostName = "BRUNOCUNHA-PC";
			}
			
			String propertiesFileName = "local-taskerbox-"
					+ hostName + ".properties";
			File propertiesFile = new File(propertiesFileName);
			if (propertiesFile.exists()) {
				prop.load(new FileInputStream(propertiesFile));
			} else {
				InputStream is = TaskerboxHttpBox.class.getResourceAsStream("/" + propertiesFileName);
				
				if (is != null) {
					prop.load(is);
				}
			}
			log.info("HTTP Using Proxy? " + prop.getProperty("proxy"));

			instance = new TaskerboxHttpBox();
			if (isTrue(prop.getProperty("proxy"))) {
				instance.setUseProxy(true);
				instance.setProxyHost(prop.getProperty("proxy.host"));
				instance.setProxyPort(Integer.valueOf(prop
						.getProperty("proxy.port")));
				instance.setAuthProxy(isTrue(prop.getProperty("proxy.auth")));
				
				instance.setProxySocks(isTrue(prop.getProperty("proxy.socks")));
				instance.setUseNtlm(isTrue(prop.getProperty("http.use.ntlm")));
				instance.setSocksHost(prop.getProperty("proxy.socks.host"));
				instance.setSocksPort(Integer.valueOf(prop
						.getProperty("proxy.socks.port")));
				instance.setNtlmProxy(isTrue(prop.getProperty("proxy.ntlm")));
				instance.setProxyDomain(prop.getProperty("proxy.domain"));
				instance.setProxyUser(prop.getProperty("proxy.user"));
				instance.setProxyPassword(prop.getProperty("proxy.password"));
				instance.setProxyWorkstation(prop
						.getProperty("proxy.workstation"));
			}
			instance.setup();
		}
		return instance;
	}

	private static boolean isTrue(String str) {
		return str != null && str.toLowerCase().equals("true");
	}

	public void setup() {
		this.httpClient = buildNewHttpClient();
	}

	public DefaultHttpClient buildNewHttpClient() {
		return buildNewHttpClient(new BasicHttpParams());
	}
	
	public DefaultHttpClient buildNewHttpClient(HttpParams params) {
		PoolingClientConnectionManager cxMgr = new PoolingClientConnectionManager(
				SchemeRegistryFactory.createDefault());
		cxMgr.setMaxTotal(100);
		cxMgr.setDefaultMaxPerRoute(20);

		DefaultHttpClient httpClient = new DefaultHttpClient(cxMgr, params);
		httpClient
				.getParams()
				.setParameter(
						CoreProtocolPNames.USER_AGENT,
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36");
		// httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
		// CookiePolicy.BROWSER_COMPATIBILITY);
		if (this.useNtlm) {
			httpClient.getAuthSchemes().register("NTLM",
					new NTLMSchemeFactory());
			httpClient.getAuthSchemes().register("BASIC",
					new BasicSchemeFactory());
			httpClient.getAuthSchemes().register("DIGEST",
					new DigestSchemeFactory());
			httpClient.getAuthSchemes().register("SPNEGO",
					new SPNegoSchemeFactory());
			httpClient.getAuthSchemes().register("KERBEROS",
					new KerberosSchemeFactory());
		}

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, getTrustingManager(),
					new java.security.SecureRandom());
			SSLSocketFactory socketFactory = new SSLSocketFactory(sc);
			Scheme sch = new Scheme("https", 443, socketFactory);
			httpClient.getConnectionManager().getSchemeRegistry().register(sch);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		if (this.useProxy) {
			if (this.proxySocks) {

				log.info("Using proxy socks " + this.socksHost + ":"
						+ this.socksPort);

				System.setProperty("socksProxyHost", this.socksHost);
				System.setProperty("socksProxyPort",
						String.valueOf(this.socksPort));

			} else {
				HttpHost proxy = new HttpHost(this.proxyHost, this.proxyPort);
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);

				if (this.authProxy) {

					List<String> authPreferences = new ArrayList<String>();

					if (this.ntlmProxy) {

						NTCredentials creds = new NTCredentials(this.proxyUser,
								this.proxyPassword, this.proxyWorkstation,
								this.proxyDomain);
						httpClient.getCredentialsProvider().setCredentials(
								new AuthScope(this.proxyHost, this.proxyPort),
								creds);
						// httpClient.getCredentialsProvider().setCredentials(
						// AuthScope.ANY, creds);

						authPreferences.add(AuthPolicy.NTLM);
					} else {
						UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
								this.proxyUser, this.proxyPassword);
						httpClient.getCredentialsProvider().setCredentials(
								AuthScope.ANY, creds);

						authPreferences.add(AuthPolicy.BASIC);
					}

					httpClient.getParams().setParameter(
							AuthPNames.TARGET_AUTH_PREF, authPreferences);
				}
			}

		}

		return httpClient;
	}

	public HttpResponse getResponseForURL(DefaultHttpClient client, String url)
			throws ClientProtocolException, IOException, URISyntaxException {
		return getResponseForURL(client, new URI(url));
	}
	
	public HttpResponse getResponseForURL(String url)
			throws ClientProtocolException, IOException, URISyntaxException {
		return getResponseForURL(new URI(url));
	}

	public HttpResponse getResponseForURLNewClient(String url)
			throws ClientProtocolException, IOException, URISyntaxException {
		return getResponseForURLNewClient(new URI(url));
	}

	public HttpResponse getResponseForURL(URI uri)
			throws ClientProtocolException, IOException {
		return getResponseForURL(httpClient, uri);
	}
	
	public HttpResponse getResponseForURLNewClient(URI uri)
			throws ClientProtocolException, IOException {
		return getResponseForURL(buildNewHttpClient(new BasicHttpParams()), uri);
	}
	
	public HttpResponse getResponseForURL(DefaultHttpClient client, URI uri)
			throws ClientProtocolException, IOException {

		HttpGet httpget = new HttpGet(uri);
		HttpResponse response1 = client.execute(httpget);

		return response1;
	}

	public HttpEntity getEntityForURL(URI uri) throws ClientProtocolException,
			IOException, URISyntaxException {
		return getResponseForURL(uri).getEntity();
	}

	public HttpEntity getEntityForURL(String url)
			throws ClientProtocolException, IOException, URISyntaxException {
		return getResponseForURL(url).getEntity();
	}

	public String getStringBodyForURL(URI uri) throws ClientProtocolException,
			IOException, IllegalStateException, URISyntaxException {
		return readResponseFromEntity(getEntityForURL(uri));
	}

	public String getStringBodyForURL(String url)
			throws ClientProtocolException, IOException, IllegalStateException,
			URISyntaxException {
		return readResponseFromEntity(getEntityForURL(url));
	}

	public String readResponseFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		BufferedInputStream bis = new BufferedInputStream(entity.getContent());
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

	public static BasicClientCookie buildCookie(String name, String value,
			String domain, String path) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath(path);
		return cookie;
	}

	private TrustManager[] getTrustingManager() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
				// Do nothing
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
				// Do nothing
			}

		} };
		return trustAllCerts;
	}


	public void setNtlmProxy(boolean ntlmProxy) {
		if (ntlmProxy) {
			this.authProxy = true;
		}
		this.ntlmProxy = ntlmProxy;
	}


	
	public Document getDocumentForURL(String url) throws ClientProtocolException, IllegalStateException, IOException, URISyntaxException {
		return Jsoup.parse(getStringBodyForURL(url));
	}

}

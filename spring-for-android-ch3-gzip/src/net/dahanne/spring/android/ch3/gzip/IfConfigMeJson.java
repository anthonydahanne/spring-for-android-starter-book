package net.dahanne.spring.android.ch3.gzip;

import org.codehaus.jackson.annotate.JsonProperty;
/**
 * Model used to connect to ifconfig.me using json messages.
 * Pay special attention to the fields which are not camel cased !
 * Map them to the class fields using the customization annotation @JsonProperty
 * 
 * @author Anthony Dahanne
 *
 */
public class IfConfigMeJson {

	private String connection;
	
	@JsonProperty("ip_addr")
	private String ipAddr;
	
	private String lang;
	
	@JsonProperty("remote_host")
	private String remoteHost;
	
	@JsonProperty("user_agent")
	private String userAgent;
	
	private String charset;
	private int port;
	private String  via;
	private String forwarded;
	private String  mime;
	
	@JsonProperty("keep_alive")
	private String keepAlive;
	
	private String encoding;
	public String getConnection() {
		return connection;
	}
	public void setConnection(String connection) {
		this.connection = connection;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String opAddr) {
		this.ipAddr = opAddr;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getRemoteHost() {
		return remoteHost;
	}
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public String getForwarded() {
		return forwarded;
	}
	public void setForwarded(String forwarded) {
		this.forwarded = forwarded;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public String getKeepAlive() {
		return keepAlive;
	}
	public void setKeepAlive(String keepAlive) {
		this.keepAlive = keepAlive;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	
}

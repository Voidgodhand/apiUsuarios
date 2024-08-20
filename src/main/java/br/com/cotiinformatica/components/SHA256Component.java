package br.com.cotiinformatica.components;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.springframework.stereotype.Component;

@Component
public class SHA256Component {

	public String hash(String input) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = messageDigest.digest(input.getBytes());
			return byteArrayToHexString(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found.", e);
		}
	}

	private String byteArrayToHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String hexString = formatter.toString();
		formatter.close();
		return hexString;
	}
}

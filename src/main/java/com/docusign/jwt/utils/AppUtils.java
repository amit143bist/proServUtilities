package com.docusign.jwt.utils;

import java.util.Base64;

public class AppUtils {

	/**
	 * @param plainCreds
	 * @return
	 */
	public static String getEncodedBase64Data(String plainCreds) {

		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		return new String(base64CredsBytes);
	}
}

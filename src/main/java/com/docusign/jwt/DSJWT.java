package com.docusign.jwt;

public abstract class DSJWT {

	/**
	 * Returns a Json Web Token builder used to create and sign tokens
	 *
	 * @return a token builder.
	 */
	public static DSJWTCreator.Builder create() {
		return DSJWTCreator.init();
	}
}

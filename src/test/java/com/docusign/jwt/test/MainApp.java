package com.docusign.jwt.test;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.algorithms.Algorithm;
import com.docusign.jwt.DSJWT;
import com.docusign.jwt.domain.AccessToken;
import com.docusign.jwt.utils.PemUtils;

public class MainApp {

	private static final String RSA_ALGO = "RSA";

	public static void main(String[] args) {

		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String privatePemPath = "C:\\Users\\amit.bist\\Box Sync\\DocuSign\\Projects\\Verizon/docusign_uat_key_pkcs8.pem";
		String publicPemPath = "C:\\Users\\amit.bist\\Box Sync\\DocuSign\\Projects\\Verizon/docusign_uat_key_public.pem";
		String integratorKey = "24238094-6ccf-4b92-9589-02a828ced5ff";
		String userId = "c1432e05-0c78-4f5d-8edd-96afab5c5064";
		String scope = "signature";
		String audience = "account-d.docusign.com";// For Demo,
													// account.docusign.com for
													// Prod

		try {
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(privatePemPath, RSA_ALGO);
			RSAPublicKey rsaPublicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(publicPemPath, RSA_ALGO);

			String jwt = createJWT(userId, integratorKey, rsaPrivateKey, rsaPublicKey, scope, audience);

			String msgBody = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=" + jwt;
			
			System.out.println(jwt);

			String url = "https://account-d.docusign.com/oauth/token";

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

			HttpEntity<String> entity = new HttpEntity<String>(msgBody, headers);

			ResponseEntity<AccessToken> response = restTemplate.exchange(url, HttpMethod.POST, entity,
					AccessToken.class);

			System.out.println(response.getBody().getAccessToken());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String createJWT(final String subject, final String issuer, final RSAPrivateKey rsaPrivateKey,
			final RSAPublicKey rsaPublicKey, final String scope, final String audience) {

		Map<String, Object> headerClaims = new HashMap<String, Object>();
		headerClaims.put("alg", "RS256");
		headerClaims.put("typ", "JWT");

		long iat = Instant.now().getEpochSecond();

		Instant expInstant = Instant.now().plusSeconds(3600);
		long exp = expInstant.getEpochSecond();

		String jwt = DSJWT.create().withHeader(headerClaims).withIssuer(issuer).withSubject(subject)
				.withClaim("iat", iat).withClaim("exp", exp).withAudience(audience).withClaim("scope", scope)
				.sign(Algorithm.RSA256(rsaPublicKey, rsaPrivateKey));

		return jwt;
	}

}

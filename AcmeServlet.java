package com.engetc.acme;

import com.google.appengine.api.datastore.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inspiration: ACME on GAE is annoying. If you're like us, deploying your app takes a non-trivial
 * amount of time - so adding static acme-challenge files and then uploading them is burdensome.
 * Since Let's Encrypt requires you to rotate your certs at least every 3 months, this becomse a
 * major friction point. Our idea is to store the ACME challenges in the GAE datastore via the GCP
 * Console and then serve them dynamically - so you can expose new challenge values without deploying
 * code.
 *
 * Usage:
 *  - map this servlet to .well-known/acme-challenge in your web.xml file
 *  - go to your GCP console and create entities of kind "AcmeChallenge", with key being the portion
 *    of the ACME challenge URL after http://yourdomain.com/.well-known/acme-challenge/
 *    each should have a single text property with the name "value", with the value being the text
 *    that you want returned to a GET request for the challenge (eg, what Let's Encrypt wants you
 *    to copy into a flat file)
 *
 * Alternatively, you can add challenge key/value to the Map<> below - which I also think is a more
 * straightforward/maintainable approach to adding random flat files in your directory tree.
 *
 * @author Erik Schultink <erik@worklytics.co>
 *
 *
 * If mapped as described, url is:
 *
 * http://localhost:8080/.well-known/acme-challenge/exampleKey
 *
 */
public class AcmeServlet extends HttpServlet {

	static Map<String, String> challenges = new ConcurrentHashMap<>();

	static {
		challenges.put("exampleKey", "exampleValue");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String keyName = request.getPathInfo().substring(1); //lose leading '/'

		response.setContentType("text/plain");
		try {
			String challengeValue = findValue(keyName);
			response.getWriter().append(challengeValue);
		} catch (EntityNotFoundException e) {
			response.setStatus(404);
			response.getWriter().append("No such key");
		} catch (IOException e) {
			response.setStatus(500);
		}
	}

	private String findValue(String key) throws EntityNotFoundException {
		if (challenges.containsKey(key)) {
			return challenges.get(key);
		} else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Entity challenge = datastore.get(KeyFactory.createKey(null, "AcmeChallenge", key));
			return (String) challenge.getProperties().get("value");
		}
	}
}

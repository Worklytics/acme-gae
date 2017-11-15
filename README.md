# acme-gae
Implement [Automatic Certificate Management Environment (ACME) challenge](https://ietf-wg-acme.github.io/acme/draft-ietf-acme-acme.html#rfc.section.7.5)
in Google App Engine (GAE) Java environment, as needed to generate and install certificates for [Let's Encrypt](https://letsencrypt.org/).

NOTE: As of Nov 2017, Google has added support for managed certificates directly in GAE, so we (Worklytics) are no longer actively using this in our environments. We organized our tools here in case it's useful to anyone else, or if future deployment approaches require us to again manage the certificates ourselves.

## Usage - ACME Challenge
This is just for publishing an ACME challenge from your GAE app.  If you want to fully create and install SSL certificates with Let's Encrypt, see the next section.

1. Copy the `AcmeServlet.java` into your GAE app; add appropriate lines to your `web.xml`/etc to make it reachable (see comments in that servlet file).  

```xml
    <!-- ACME challenge for Let's Encrypt SSL support -->
    <servlet>
        <servlet-name>acme-challenge</servlet-name>
        <servlet-class>co.worklytics.acme.AcmeServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>acme-challenge</servlet-name>
        <url-pattern>/.well-known/acme-challenge/*</url-pattern>
    </servlet-mapping>
```

2. Deploy the app to GAE.


3. add your Acme challenge value to the data store.  There are two approaches:
    * push it using our tool as described in `push-acme-challenge-to-cloud/README.md`.
    * manually add it to the GAE datastore. In the Datastore web interface in the GCloud Dev Console, create a new entity of kind `AcmeChallenge`, with key=[your acme challenge key] and a single string property named `value`, with the value being the value of the ACME challenge.  (both key and value are given to you by certbot) 

Your challenge should be available on: http://[your-app-domain]/.well-known/acme-challenge/[key]

## Full Use of Let's Encrypt + Certbot for GAE

### Pre-Reqs

Assuming OS X environment. Certbot site offers lots of 

```bash

# install the tool that Let's Encrypt / EFF has released to support this
brew install certbot

# setup directory structure that certbot wants when running in manual mode as regular user (not sudo)
mkdir config
mkdir log
mkdir work
```

### Actual Cert Generation

1. Run the Certbot command
```bash
certbot certonly --manual -d [YOUR_DOMAIN] -d [ADDITIIONAL_DOMAIN]--config-dir config/ --work-dir work/ --logs-dir log/
```

NOTE: you can use the `-d` option to generate multi-domain certificates.  

2. For each GAE project and domain, follow the steps described in the previous section to provide an ACME challenge value on 

NOTE: The keys are deterministic from the domains. So if you take the approach of creating `AcmeChallenge` keys in the data store, when you renew the certificate, you can just edit the values rather than creating new ones.

 3. Find the certificate in `config/live/[FIRST_DOMAIN]`

Convert it to RSA PEM format that GAE likes. (described [here](https://cloud.google.com/appengine/docs/standard/python/using-custom-domains-and-ssl#converting_private_keys_and_concatenating_ssl_certificates)):

```bash
openssl rsa -in privkey.pem -out rsa-privkey.pem
```

 4. Then in [Dev Console --> App Engine --> Settings --> SSL Certificates](https://console.cloud.google.com/appengine/settings/certificates), click "Upload a new certificate".

Then upload or `cat + copy/paste` these into the form (latter is my preference): 
  * public key is the `fullchain.pem`
  * private key is the `rsa-privkey.pem`

Enable these for the relevant domains.

 5. Repeat step 4 for every project that has one of these domains.

 6. Finally, you can, of course, destroy all these keys for security.


Renewal should be a variant on the above, but for 2 you can just edit the existing `AcmeChallenge`
entities. New ones are only needed for new domains.

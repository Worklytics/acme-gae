# acme-gae
Implement [Automatic Certificate Management Environment (ACME) challenge](https://ietf-wg-acme.github.io/acme/draft-ietf-acme-acme.html#rfc.section.7.5)
in Google App Engine (GAE) Java environment, as needed to generate and install certificates for [Let's Encrypt](https://letsencrypt.org/).

NOTE: As of Nov 2017, Google has added support for managed certificates directly in GAE, so we (Worklytics) are no longer actively using this in our environments. But we wanted to organize our tools for it into their own repo, in case its of future use to anyone.

## Usage

1. Copy the `AcmeServlet.java` into your GAE app; add appropriate lines to your `web.xml`/etc to make it reachable (see comments in that servlet file).  Deploy the app to GAE.

2. push your Acme secret to the app (using the nodejs tool as described in `push-acme-challenge-to-cload/README.md`)

Your challenge should be available on: http://[your-app-domain]/.well-known/acme-challenge/[key]

# ACME Challenge Upload tool

Tool to push ACME challenges into Cloud Datastore, to ease validation.

Written in NodeJS, as that's an easier than Java to use from cmd-line; we already use node/npm for
stuff; and we already use JavaScript for plenty of other things.

```bash

# install datastore nodejs client
npm install --save @google-cloud/datastore

# install this package as an executable in your shell
npm install -g

# authorize yourself
# (PROJECT_KEY == worklytics-staging for us)
gcloud auth application-default login --project PROJECT_KEY

# run it!
push-acme-challenge-to-cloud worklytics-staging
```
#!/usr/bin/env node

// NOTE: must have auth'ed with
// gcloud auth application-default login --project PROJECT_KEY
// Imports the Google Cloud client library
const Datastore = require('@google-cloud/datastore');
const Program = require('commander');


Program
  .option('-p, --projectId <projectId>', 'The projectId to use.')
  .action(function(){
    // Instantiates a client
    const datastore = Datastore({
      projectId: program.projectId
    });

// The kind for the new entity
    const kind = 'AcmeChallenge';

    const challenges = {
      'exampleKey': 'exampleValue',
    };

    Object.keys(challenges).forEach(function(key) {
      const dataStoreKey = datastore.key([kind, key]);
      const entity = {
        key: dataStoreKey,
        data: {
          value: challenges[key],
        }
      };

      datastore.save(entity)
        .then(function() {
          console.log('Saved ${task.key.name}: ${task.data.description}');
        })
        .catch(function(err) {
          console.error('ERROR:', err);
        });
    });
  })
  .parse(process.argv)



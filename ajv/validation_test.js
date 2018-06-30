

'use strict';
var fs = require('fs')
var Ajv = require('ajv');
var ajv = Ajv({allErrors: true, verbose: true,  $data: true, jsonPointers: true});
var validationCallback = require('validation-callback');

//For some reason there is a natural offset of two
if (process.argv.length != 4) {
  console.log('Not enough arguments');
  return;
} else {
    //TODO: Auto replace this with draft-07 earlier on
    ajv.addMetaSchema(require('ajv/lib/refs/json-schema-draft-06.json'));
    var schemaJsonPath = process.argv[2];
    var dataJsonPath = process.argv[3];

    var schemaObj = JSON.parse(fs.readFileSync(schemaJsonPath, 'utf8'));
    var validate = ajv.compile(schemaObj)

    var dataObj = JSON.parse(fs.readFileSync(dataJsonPath, 'utf8'));
    var valid = validate(dataObj);

    if (!valid) {
        console.log(validate.errors)
        validationCallback.notifyResult(validate.errors)
    } else {
        validationCallback.notifyResult(null)
    }
}


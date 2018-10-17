for entry in "./test-data"/*.json
do
  # include the cognito id token and make the endpoint as a parameter
  curl -H "Content-Type: application/fhir+json" -H "Authorization: <<IDToken>>" -i --data "@$entry" https://<<FHIR Service url>>/Prod/Bundle
  echo "$entry"
done
for entry in "."/*.json
do
  # include the cognito id token and make the endpoint as a parameter
  curl -H "Content-Type: application/json+fhir" -i --data "@$entry" https://ihy1s1l9jl.execute-api.us-west-2.amazonaws.com/dev/bundle
  echo "$entry"
done
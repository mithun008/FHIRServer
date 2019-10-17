aws cloudformation package --template-file \
FHIRService-dev-swagger-apigateway.yaml --output-template-file serverless-output.yaml \
--s3-bucket "$1"

aws cloudformation deploy --template-file \
/home/ec2-user/environment/FHIRServer/resources/serverless-output.yaml \
--stack-name "$2" --capabilities CAPABILITY_IAM



aws cloudformation describe-stacks \
    --stack-name $2 \
    --query 'Stacks[].Outputs[?OutputKey==`ApiUrl`][OutputValue]' \
    --output text

API_ENDPOINT="$(aws cloudformation describe-stacks --stack-name $2 --query 'Stacks[].Outputs[?OutputKey==`ApiUrl`][OutputValue]' --output text)"
    
    

curl -H "Accept: application/fhir+json" "${API_ENDPOINT}"metadata | jq

USER_POOL_ID="$(aws cloudformation describe-stacks --stack-name $2 --query 'Stacks[].Outputs[?OutputKey==`UserPoolId`][OutputValue]' --output text)"

CLIENT_ID="$(aws cloudformation describe-stacks --stack-name $2 --query 'Stacks[].Outputs[?OutputKey==`UserPoolClient`][OutputValue]' --output text)"




ID_TOKEN="$(python provision-user.py "${USER_POOL_ID}" "${CLIENT_ID}")"    


curl -H "Accept: application/fhir+json" -H "Authorization:${ID_TOKEN}" ${API_ENDPOINT}Patient | jq


echo "API_EDNPOINT: ${API_ENDPOINT}"

echo "IDToken: ${ID_TOKEN}"

echo "USER_POOL_ID: ${USER_POOL_ID}"

echo "CLIENT_ID: ${CLIENT_ID}"
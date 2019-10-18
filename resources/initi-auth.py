import boto3

client = boto3.client('cognito-idp')

response = client.initiate_auth(
    AuthFlow='USER_PASSWORD_AUTH',
    AuthParameters={
        'USERNAME': 'workshopuser',
        'PASSWORD': 'Master123!'
    },

    ClientId='<<REPLACE_CLIENT_ID>>'
)

sessionid = response['AuthenticationResult']['IdToken']
print(sessionid)
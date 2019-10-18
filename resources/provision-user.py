import boto3
import sys

client = boto3.client('cognito-idp')

response = client.admin_create_user(
    UserPoolId=sys.argv[1],
    Username='workshopuser',
    UserAttributes=[
        {
            'Name': 'email',
            'Value': 'dummy@email.com'
        },
        {
            'Name': 'email_verified',
            'Value': 'True'
        }
        
    ],
    ValidationData=[
        {
            'Name': 'email',
            'Value': 'dummy@email.com'
        }
    ],
    TemporaryPassword='Master123!',
    MessageAction='SUPPRESS'
)

response = client.initiate_auth(
    AuthFlow='USER_PASSWORD_AUTH',
    AuthParameters={
        'USERNAME': 'workshopuser',
        'PASSWORD': 'Master123!'
    },

    ClientId=sys.argv[2]
)
sessionid = response['Session']

response = client.respond_to_auth_challenge(
    ClientId=sys.argv[2],
    ChallengeName='NEW_PASSWORD_REQUIRED',
    Session=sessionid,
    ChallengeResponses={
        'USERNAME' : 'workshopuser',
        'NEW_PASSWORD': 'Master123!'
    }
)

response = client.initiate_auth(
    AuthFlow='USER_PASSWORD_AUTH',
    AuthParameters={
        'USERNAME': 'workshopuser',
        'PASSWORD': 'Master123!'
    },

    ClientId=sys.argv[2]
)

#print(response)
sessionid = response['AuthenticationResult']['IdToken']
print(sessionid)


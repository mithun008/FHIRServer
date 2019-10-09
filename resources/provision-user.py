import boto3

client = boto3.client('cognito-idp')

response = client.admin_create_user(
    UserPoolId='<<REPLACE_POOL_ID>>',
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

    ClientId='<<REPLACE_CLIENT_ID>>'
)


response = client.respond_to_auth_challenge(
    ClientId='<<REPLACE_CLIENT_ID>>',
    ChallengeName='NEW_PASSWORD_REQUIRED',
    Session=sessionid,
    ChallengeResponses={
        'USERNAME' : 'workshopuser',
        'NEW_PASSWORD': 'Master123!'
    }
)

sessionid = response['Session']
print(sessionid)

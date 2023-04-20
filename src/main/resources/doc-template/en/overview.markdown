---
title: Overview
type: get-started-element
---
# First header
Here is the link thing:

1. This is [Drupal Link somewhere](/user/apps) on the portal website.
2. Check out the bold **text right here**.

# Second header

Code sample:

    curl \
    --request POST \
    "https://api.dhl.com/auth/v1/token?response_type=access_token&grant_type=client_credentials" -H "accept: application/json" -H "authorization: <your-basic-base64-key>"

You have to replace the variable placeholders indicated with "\<\>" by your personalized credentials from the developer portal.

Another code sample:

    {
    "access_token": "",
    "id_token": "",
    "scope": "",
    "token_type": "Bearer",
    "expires_in": 1734
    }

\* Mailto link [api@dpdhl.com](mailto:api@dpdhl.com).

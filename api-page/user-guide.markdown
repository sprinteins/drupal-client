---
title: User Guide
---
### Get Access

You must request credentials for any applications you develop

To register your app and get your API subscription keys:

1.  Click [My Apps](/user/apps) on the portal website.
2.  Click Add Developer App.  
    The “Add App” form appears.
3.  Complete the Add App form.  
    You can select the APIs you want to access.
4.  When you have completed the form, click the Add App button.  
    The “Approved” label will appear next to the app name when the app has been approved.  
    Note: Additional verification steps may be required for some applications.

### Authentication

For authentication purposes you need to obtain an OAuth token and pass the same via the request "Authorization" header. To retrieve this token you have to perform the following steps below. In doing so, you need to replace the variable placeholders indicated with "<>" by your personalized credentials from the Developer Portal.

You can find the credentials in the details view of your app which has access to the DHL Label Generator API:

1.  From the [My Apps](/user/apps) screen, click on the name of your app.  
    The Details screen appears.
2.  If you have access to more than one API, click the name of the relevant API.  
    **Note:** The APIs are listed under the "Credentials" section.
3.  Click the **Show** link below the asterisks that is hiding the _API Key_ as well as the _API Secret_.  
    The _API Key_ and the _API Secret_ appear.

**1\. Generate your base64 key**

*   Type the below command in your terminal (unix-based systems):

        $ echo -ne <your-consumer-key>:<your-consumer-secret> | base64

*   Example output:

        V01LWGFtZ5gtVGNVaEhXSHdSNmJkWGQ4cWNuQmVDGEF6TDd5MTFhdk5uakVsa3buQw==

*   This is `<your-base64-encoded-key>`

        Basic V01LWGFtZ5gtVGNVaEhXSHdSNmJkWGQ4cWNuQmVDGEF6TDd5MTFhdk5uakVsa3buQw==

The given output will be used to authorize you to generate the access token.

**2\. Generate your access token**

*   Type the below command in your terminal (curl tool needed):

        curl -X POST \
            'https://api-eu.dhl.com/auth/v1/token?response_type=access_token&grant_type=client_credentials' \
            -H 'Authorization: <your-basic-base64-key>'

*   Response received should be similar to below:

        {
            "access_token": "1ilKYL7sO9IFgGpBeL8HAoT9lvJZ",
            "token_type": "Bearer",
            "expires_in": 1799
        }

The given access token can now be applied to the request "Authorization" header to call the DHL Label Generator API (curl tool needed).

    curl -X POST \
        https://api-eu.dhl.com/label-generator/v1/labels/parcel-international \
        -H 'Authorization: Bearer <OAuth Token>' \
        -H 'Content-Type: application/json'

### Environments

The addressable API base URL/URI environment is:

* Production environment
    * https://api-eu.dhl.com/label-generator/v1

### Rate limits

Rate limits protect the DHL infrastructure from suspicious requests that exceed defined thresholds.

The main request limits are:

* Size S
    * 2 calls per second
    * 500 calls per day

Please contact [support@dpdhl.freshdesk.com](mailto:support@dpdhl.freshdesk.com) if you need higher throughput. 

When the limit is reached, you will receive an HTTP Status code:

    429: Too many requests.

### Additional Information

##### Permitted HTTP methods

The API only offers endpoints which serve POST methods to create labels. 

##### Example requests to the API

Simple HTTP request example:

    curl -X POST \
        https://api-eu.dhl.com/label-generator/v1/labels/parcel-international \
        -H 'Authorization: Bearer <OAuth Token>' \
        -H 'Content-Type: application/json' \
        -d '{
        "labelFormat": "pdf",
        "receiver": {
            "givenName": "Klaus",
            "additionalName": "Dieter",
            "familyName": "Meyer",
            "address": {
            "addressCountry": "DE",
            "postalCode": "53113",
            "addressLocality": "Bonn",
            "streetAddress": "Charles-de-Gaulle-Str. 20"
            }
        },
        "shipper": {
            "givenName": "Michaela",
            "familyName": "Müller",
            "address": {
            "addressCountry": "DE",
            "postalCode": "53113",
            "addressLocality": "Bonn",
            "streetAddress": "Heinrich-Brüning. Str. 5"
            }
        },
        "billingNumber": "123456",
        "routingCode": "21348075016401",
        "trackingNumber": "JJD000123456789101112",
        "quantity": 1,
        "weight": 3.7
        }'

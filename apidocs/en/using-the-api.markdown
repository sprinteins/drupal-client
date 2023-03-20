---
title: Using the API
type: get-started-element
---
This API is built conform to the REST architectural style to provide interoperability.

Exposed API resources can be manipulated using HTTP methods. Requests to a resource URL generate a response with JSON payload containing detailed information about the requested resource. HTTP response codes are used to indicate API errors.

We use a token-based approach to authorize requests coming from your system.

# Http requests

Apply the standardized HTTP method semantics to communicate with the Print-Mailing Automation API.

The API offers clearly identifiable **resources** ("things of the Print-Mailing Automation universe"). You can use a resource's **representation** to create, update, delete and execute resources. There are different types of resources:

* **Single resource**: E.g. a systemuser, a campaign and its corresponding properties. A PUT updates properties of an existing single resource identified by an id.
* **List resource**: A (pageable) list of single resources e.g. a list of systemusers. Usually, a list resource doesn't include all properties of the single resources. A POST adds a new single resource to a list resource.
* **Processing resource**: Triggers a usually long running asynchronous process. This might be a price calculation or a campaign order. A POST is used to trigger processing resources.
* **Lookup resource**: Offer permitted (enum) field values for simpler communication with the API. API clients can see which values they can expect and have to include in requests. E.g. values for campaign states (active, paused, ...). Usually, lookup resources include an internationalized label.

**POST** requests are used to create single resources on a list resource. The semantic for list endpoints is best described as 'please add the enclosed representation to the list resource identified by the URL'. On a successful POST request, the server will create one or multiple new resources and provide their URLs in the response. Successful POST requests will usually generate 201.

**PUT** requests are used to update an **entire** resources, which already exists and thus can be clearly identified by an id. The semantic is best described as "please put the enclosed representation at the resource mentioned by the URL, replacing any existing resource". On successful PUT requests, the server will replace the entire resource addressed by the URL with the representation passed in the payload (subsequent reads will deliver the same payload). Successful PUT requests will usually generate 200.

**GET**, **DELETE** and **OPTIONS** requests follow the standardized HTTP method semantics.

# Headers

You can (and sometimes have to) set HTTP headers. This could be the 'Content-Type', 'Accept', 'Accept-Encoding' or 'Authorization' headers, among others. Please only set headers that are necessary.
> **Caution**
>
> Be aware that HTTP headers with empty values are not allowed for security reasons and such requests will be rejected.

# Payload and media types

The standard media type for requests and responses is **application/json** with default encoding **UTF-8**. However, other media types like **application/xml** are supported, too. See the concrete resource definition for all accepted media types.
> **Tip**
>
> Set the header **'Accept: application/json'** in your JSON requests in order to get a detailed JSON response in case of an error.

Use the same semantics for **null** and **absent** properties. OpenApi 3.x allows to mark properties as required and as nullable to specify whether properties may be absent ({}) or null ({"example":null}). If a property is defined to be not required and nullable (see 2nd row in Table below), this rule demands that both cases must be handled in the exact same manner by specification.

The following table shows all combinations and whether the examples are valid:

| required | nullable | {}  | {"example":null} |
|----------|----------|-----|------------------|
| true     | true     | No  | Yes              |
| false    | true     | Yes | Yes              |
| true     | false    | No  | No               |
| false    | false    | Yes | No               |

**boolean** properties must not be null. A boolean is essentially a closed enumeration of two values, true and false.

# HTTP response compression

The Print-Mailing Automation API will compress all text-based responses using your favorite compression algorithm.
> **Tip**
>
> Please set a correct **Accept-Encoding** http header for all your request, if applicable!

This increases the API user experience and saves bandwidth.

# Date and time formats

Use the date and time formats defined by [RFC 3339](https://tools.ietf.org/html/rfc3339#section-5.6):

* For "date" use strings matching yyyy-MM-dd, for example: 2026-05-28
* For "date-time" use strings matching yyyy-MM-dd'T'HH:mm:ss.SSS'Z', for example 2026-05-28T14:07:17.000Z

Note that the OpenApi format "date-time" corresponds to "date-time" in the RFC and "date" corresponds to "full-date" in RFC. Both are specific profiles, a subset of the international standard ISO 8601.

A zone offset must not be used. Date time values are always interpreted in **UTC** without offsets. Localization of date time should be done by the api client, if necessary.

# Status codes and common error structures

Apply the standardized HTTP status code semantics to communicate with the Print-Mailing Automation API.

The OpenApi specification of every resource provides information about specific success and error responses. It is not a complete list to avoid an overload with common sense information. See below the list of most commonly used status codes in the Print-Mailing Automation API:

|        Status code        |                                                          Meaning                                                          |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------|
| 200 OK                    | Request completed successfully.                                                                                           |
| 201 Created               | Request completed successfully. Resource created.                                                                         |
| 400 Bad Request           | Invalid/not parseable JSON payload. Non existing enum value recognized. Concurrent update of a resource.                  |
| 401 Unauthorized          | Missing access token in the request headers.                                                                              |
| 403 Forbidden             | Access denied because of missing authority. E.g. customer id is not allowed.                                              |
| 404 Not Found             | The resource is not found.                                                                                                |
| 422 Unprocessable Entity  | The request could be parsed but the contents are invalid. See the business validation error code for further information. |
| 500 Internal Server Error | A generic error indication for an unexpected server execution problem.                                                    |
| 503 Service Unavailable   | Service is (temporarily) not available.                                                                                   |

In case of an error or input data validation failure, the services will respond with the following error representation in the response body (including internationalized messages).

For general errors (e.g. service unavailable):

    {
      "timestamp": "2026-02-18T16:11:13.456Z",
      "status": 503,
      "correlationId": "3ea93639-3d7f-4a5c-88ee-f6b6d01dffac",
      "error": "Service Unavailable",
      "errors": [{
        "errorCode": "DM_CO_0012",
        "errorMessage": "The requested functionality is currently not available.Try again later.",
        "arguments": [],
        "fieldName": null
      }]
    }

For specific validation of a sent field:

    {
      "timestamp": "2026-02-18T16:12:05.352Z",
      "status": 422,
      "correlationId": "08a884be-3aec-4fa5-b647-7f82149144d5",
      "error": "Unprocessable Entity",
      "errors": [{
        "errorCode": "NotBlank",
        "errorMessage": "must not be empty",
        "arguments": [],
        "fieldName": "password"
        }]
    }

> **Tip**
>
> Notice the unique **errorCode**: It describes a specific error case which is documented in the OpenApi. Let your application handle these specific error cases and don't only look for http status codes!

If you want to get in contact with us because of an error response, please also supply the **correlationId**. This is a unique id which is generated for every request and can help us to trace requests through our backend.

# Common representation structures

Most of the resource representations include common fields:

    {
       "id":87075,
       "createdOn":"2020-04-02T11:48:51.000Z",
       "changedOn":"2020-04-29T13:02:56.000Z",
       "version":44,
       ".....": "....."
    }

Use this **id** to address the resource. The **version** is a simple counter informing about resource changes.

A **paged list resource** has the following structure:

    {
       "elements":[
          {
             "id":549,
             "createdOn":"2020-03-04T09:05:11.000Z",
             "changedOn":null,
             "version":1,
             ".....": "....."
          },
          { ... }
       ],
       "page":{
          "size":50,
          "totalElements":13,
          "totalPages":1,
          "number":0
       },
       "links":[
          {
             "rel":"self",
             "href":"https://print-mailing-api.deutschepost.de/user/customers?page=0&size=50&sort=company,asc"
          }
       ]
    }

# Pagination and filtering list resources

Several list resources of the Print-Mailing Automation API support pagination and filtering to reduce the amount of returned data and improve the API client experience. If no resource fits to the given filtering criteria an empty list is returned. An HTTP 404 is never returned form a list resource.

|              Request               |                                                   Meaning                                                    |
|------------------------------------|--------------------------------------------------------------------------------------------------------------|
| `?page=0&size=50`                  | Get the first page, maximum 50 items per page.                                                               |
| `?page=0&size=50&company=Dynp`     | Get the first page, maximum 50 items per page. Search in field "company" with value "Dynp".                  |
| `?page=0&size=50&sort=company,asc` | Get the first page, maximum 50 elements per page. Sort ascending by fieldName "company".                     |
| `?sort=company,asc&sort=type,desc` | Get the default number of elements per page. Sort ascending by fieldName "company" and descending by "type". |

> **Caution**
>
> The maximum number of returned elements per list resource is always **2000**. You MUST use paging, if you expect bigger result sets.

List resources also include **link relation fields** for easier pagination:

* **self**: The hyperlink pointing to the same page.
* **prev**: The hyperlink pointing to the previous page.
* **next**: The hyperlink pointing to the next page.

# Lookup resources

Lookup resources offer permitted (enum) field values for simpler communication with the API. There are cases where a API client has to send a specific value from a set of permitted values. If you have a frontend application, these kind of values usually can be picked from a drop down menu.

A lookup resource publishes all of this permitted values (which are mostly ids) together with an internationalized label.

    {
       "Font": [{
          "id": 10,
          "label": "Arial"
       },
       {
          "id": 11,
          "label": "Arial Bold"
       }, "...."]
    }

# Error code lookup resources

Error code lookup resources offer a complete list of error codes, specific resources may return in an error response. This resource can be helpful during API client development. An error code lookup resource publishes all of this codes with an internationalized label.

    {
       "CommonBusinessError":[
          {
             "value":"DM_CO_0001",
             "label":"Auswahlwert existiert nicht."
          }, "..."
       ],
       "RecipientBusinessError":[
          {
             "value":"DM_RC_0001",
             "label":"Die Adressfelder des Print-Mailings sind nicht definiert."
          },
          {
             "value":"DM_RC_0002",
             "label":"Das Print-Mailing existiert nicht."
          }, "..."
       ]
    }

Error code lookup resources may include **common business errors**. This type of errors is common to all resources.

# Versioning

The API has explicit API versioning within the URL (`/v1/`). However, we apply the following rules to evolve the Print-Mailing Automation API in a backward-compatible way to stay on v1 as long as possible:

* Add only optional, never mandatory fields.
* Never change the semantic of fields (e.g. changing the semantic from customer-ekp to customer-id, as both are different unique customer keys)
* Input fields may have (complex) constraints being validated via server-side business logic. Never change the validation logic to be more restrictive and make sure that all constraints are clearly defined in description.
* Support redirection in case an URL has to change 301 (Moved Permanently).

**API clients should apply the following principles:**

* Be conservative with API requests and data passed as input, e.g. avoid to exploit definition deficits like passing megabytes of strings with unspecified maximum length.
* Be tolerant in processing and reading data of API responses.
* Be tolerant with unknown fields in the payload, i.e. ignore new fields **but do not eliminate them from payload if needed for subsequent PUT requests**. Remember, the semantics of PUT.
* Be prepared that enum return parameter may deliver new values; either be agnostic or provide default behavior for unknown values.
* Be prepared to handle HTTP status codes not explicitly specified in endpoint definitions. Note also, that status codes are extensible. Default handling is how you would treat the corresponding 2xx code (see [RFC 7231 Section 6](https://www.rfc-editor.org/rfc/rfc7231#section-6)).
* Follow the redirect when the server returns HTTP status code 301 (Moved Permanently).


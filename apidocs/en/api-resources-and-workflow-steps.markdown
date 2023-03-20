---
title: API resources and workflow steps
type: get-started-element
---
This section walks you through the resources currently available.

# Retrieving Data for your selection

## Via "GET" Endpoints

The Targeting API provides multiple ways of retrieving the data you need for your selection via a variety of requests using the HTTP "GET" method.

For the following example you use the endpoint:

**/postal-code/{postal code}**

where **{postal code}** is replaced by postal code of your choosing.

    GET https://print-mailing-api.deutschepost.de/targeting/postal-code/52349

    HTTP/1.1 200
    {
      [
        {
            "id": "52349",
            "type": "Feature",
            "properties": {
                "id": "52349",
                "centroid": {
                    "type": "Point",
                    "coordinates": [
                        6.465422646782157,
                        50.80030381672309
                    ]
                },
                "hh": 7167,
                "postal_code": "52349"
            },
            "geometry": {
                "type": "MultiPolygon",
                "coordinates": [
                    [
                        [
                            [
                                6.479564800593006,
                                50.80942448326762
                            ],
                                .
                                .
                                .multiple coordinates are following
                                .
                                .
                            [
                                6.479564800593006,
                                50.80942448326762
                            ]
                        ]
                    ]
                ]
            }
        }
    ]
    }

All other data retrieving "GET" endpoints work in the same way as the depicted example. They all return data about the geometric shape and meta information regarding the provided parameter.

Below, you find a list of the available endpoints and information about the data they return.

|                Endpoint                |     Parameter     |                                                            Data returned                                                             |
|----------------------------------------|-------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| /zone                                  | None              | Returns geometric information on all zones. A zone comprises all households with the same first digit of the postal code.            |
| /zone/{zone}                           | Zone ID           | Returns information for a specific zone.                                                                                             |
| /region/{region}                       | Region ID         | Returns information on a specific region shape. A region comprises all households with the same first two digits of the postal code. |
| /postal-code/{postal-code}             | Postal code       | Returns information for a specific postal code                                                                                       |
| /neighbourhood/{neighbourhoodId}       | Neighbourhood ID  | Returns information for a specific neighbourhoodId.                                                                                  |
| /delivery-district/{delivery-district} | Delivery district | Returns information for a specific delivery district                                                                                 |

## Via "POST" Endpoints

Compared to the "GET" requests, the HTTP "POST" endpoints differ only in the way information is fed to them.

Here, you can search by using a number of parameters instead of only one parameter specific to the request.

In the following example, you want to receive delivery-district shapes. Therefore, you use the endpoint:

**/delivery-district/search**

For the body, you provide different parameters that you want data on:

    {
      "cities": [
        {
          "city": "Hamburg",
          "district": "Sternschanze",
          "latitude": 6.2345,
          "longitude": 50.6789
        }
      ],
      "neighbourhoods": [
        "51706"
      ],
      "districtCodes": [
        "52222-21"
      ],
      "postalCodes": [
        "48231"
      ],
      "regions": [
        "64"
      ],
      "zones": [
        "6"
      ],
      "coordinates": [
        {
          "latitude": 6.2345,
          "longitude": 50.6789,
          "distance": 5
        }
      ],
      "ags": [
        "18807060"
      ]
    }

Note that you can mix and match your search parameters as you please. you can even use delivery-districts themselves as search parameters.

Here is the full example with request and response:

    POST https://print-mailing-api.deutschepost.de/targeting/delivery-district/search
    Content-Type: application/json

    {
      "cities": [
        {
          "city": "Hamburg",
          "district": "Sternschanze",
          "latitude": 6.2345,
          "longitude": 50.6789
        }
      ],
      "neighbourhoods": [
        "51706"
      ],
      "districtCodes": [
        "52222-21"
      ],
      "postalCodes": [
        "48231"
      ],
      "regions": [
        "64"
      ],
      "zones": [
        "6"
      ],
      "coordinates": [
        {
          "latitude": 6.2345,
          "longitude": 50.6789,
          "distance": 5
        }
      ],
      "ags": [
        "18807060"
      ]
    }

    HTTP/1.1 200
    [
        {
            "id": "64760-11",
            "type": "Feature",
            "properties": {
                "id": "64760-11",
                "centroid": {
                    "type": "Point",
                    "coordinates": [
                        9.01631706737023,
                        49.536565032187156
                    ]
                },
                "hh": 439,
                "zbn": "64760-11"
            },
            "geometry": {
                "type": "MultiPolygon",
                "coordinates": [
                    [
                        [
                            [
                                9.049622608052276,
                                49.519275801389085
                            ],
                                .
                                .
                                .multiple coordinates following
                                .
                                .
                            [
                                9.048336576795798,
                                49.51598542686777
                            ]
                        ]
                    ]
                ]
            }
        },
        .
        .
    . information on multiple delivery districts following
    .
    .
    ]

As with the "GET" endpoints, all other data retrieving "POST" endpoints work in the same way as the depicted example. They all return data about the geometric shape and meta information regarding the provided parameters.

Below, you find a list of the available endpoints and information about the data they return.

|         Endpoint          |                  Data returned                   |
|---------------------------|--------------------------------------------------|
| /zone/search              | Returns geometric zone information.              |
| /region/search            | Returns geometric region information.            |
| /postal-code/search       | Returns geometric postal code information.       |
| /neighbourhood/search     | Returns geometric neighbourhood information.     |
| /delivery-district/search | Returns geometric delivery-district information. |

## ID's for partially known data

For the previous described endpoints you need the exact names or ID's to request the geographic information. If you don't know the exact ID's, you can use the following general search endpoint.

**/search**

With providing the request parameter "query" to this HTTP "GET" method you can search for the needed ID's.

    GET https://print-mailing-api.deutschepost.de/targeting/search?query=kfurt
    Accept: application/json
    Accept-Encoding: gzip

    HTTP/1.1 200
    {
      "results": [
        {
          "id": "2888450",
          "areaCode": "Stadt Frankfurt",
          "type": "NEIGHBOURHOOD"
        },
        {
          "id": "Frankfurt am Main",
          "areaCode": "Frankfurt am Main",
          "type": "CITY"
        },
        {
          "id": "14868",
          "areaCode": "Frankfurter Berg",
          "type": "NEIGHBOURHOOD"
        },
        {
          "id": "14866",
          "areaCode": "Frankfurt",
          "type": "NEIGHBOURHOOD"
        },
        {
          "id": "55715",
          "areaCode": "Stadt Frankfurt am Main",
          "type": "NEIGHBOURHOOD"
        },
        {
          "id": "14867",
          "areaCode": "Frankfurt",
          "type": "NEIGHBOURHOOD"
        },
        {
          "id": "Frankfurt (Oder)",
          "areaCode": "Frankfurt (Oder)",
          "type": "CITY"
        }
      ]
    }

The parameter has to be URI encoded, so you can search for e.g. "Üb" like "%C3%9Cb". Possible results are postal code, delivery district, city and neighbourhood. The result list is limited to ten items.

## Getting shapes with a KML- or Shape-file

For users of the KML- or Shape-file format we offer endpoints for the postal-code and delivery district detail level. Via a multipart file request it os possible to retrieve all intersecting shapes from our service. The intersection percentage can be parameterized via the "threshold".

**/files/**

The following example uploads a kml file:

    POST https://print-mailing-api.deutschepost.de/targeting/postal-code/files?threshold=50
    Content-Type: multipart/form-data; boundary=boundary
    Accept: application/json

    --boundary
    Content-Disposition: form-data; name="file"; filename="Beispiel.kml"
    Content-Type: application/xml

    < ../the/file/content...
    --boundary

    HTTP/1.1 200
    [
      {
        "id": "26135",
        "type": "Feature",
        "properties": {
          "id": "26135",
          "centroid": {
            "type": "Point",
            "coordinates": [
              8.262068501492301,
              53.12199657456612
            ]
          },
          "hh": 8763,
          "postal_code": "26135"
        },
        "geometry": {
          "type": "MultiPolygon",
          "coordinates": [
            [
              [
                [
                  8.24304468381723,
                  53.14197450152309
                ],
                ...
              ]
            ]
          ]
        }
      },
      ...
    ]

# Using the playground part of the API to explore the possibility\`s with our data

Note that this part of our API is only for exploration and is very different in use from the authenticated part, which is needed for further processing of the selections. There are no cross over points between these API parts at the moment. If you want to store your selection permanently you have to use the authenticated part of the API, which is available for registered and logged in users.

## Creating a selection

Now that you have the necessary data for your selection, you need to create a selection ID. For this process it is necessary to preserve the set cookies between requests to ensure the correct routing. This is as easy as calling the HTTP "POST" method:

**/selections**

In the body of this call you can add several extra parameters to your selection, but for demonstration purposes you are just going to give it a name.

    POST https://print-mailing-api.deutschepost.de/targeting/selections
    Content-Type: application/json

    {
      "name": "Test selection"
    }

As response you receive a body which informs you about different default values set. The most important out of these values is the "id" value which represents the selection ID that identifies this selection.

    HTTP/1.1 200
    {
        "meta": {
            "id": "afa656db-7c1f-44e7-adb6-74ece5e3627b",
            "name": "Test selection",
            "type": "POSTAL_CODE",
            "productType": "POSTAKTUELL_ALL",
            "numberOfAreas": 0,
            "netQuantity": 0,
            "quantity": 0,
            "filters": [],
            "filterCosts": 0
        },
        "areaCodes": []
    }

Right now, your selection only exists in the server cache for a limited time. If you want to store your selection permanently you have to use the authenticated part of the API. Let us move to the next step.

## Adding to and removing from a selection

You add data to your selection by calling the HTTP "PATCH" method:

**selections/{selection_id}/areacodes**

where **{selection_id}** is replaced by your selection ID. In the body, you include the data that you want to be added to your selection. Here, you can use the same kind of information you can use for the search endpoint before.

    PATCH https://print-mailing-api.deutschepost.de/targeting/selections/afa656db-7c1f-44e7-adb6-74ece5e3627b/areacodes
    Content-Type: application/json
    Accept: application/json
    {
      "add": {
        "cities": [
          {
            "city": "Hamburg",
            "neighbourhood": "Altstadt",
            "latitude": 6.2345,
            "longitude": 50.6789
          }
        ],
        "districtCodes": [
          "52222-21"
        ],
        "postalCodes": [
          "48231"
        ],
        "regions": [
          "64"
        ]
      }
    }

    HTTP/1.1 200

    {
      "added": [
        {
          "id": "64839",
          "type": "Feature",
          "properties": {
            "id": "64839",
            "centroid": {
              "type": "Point",
              "coordinates": [
                8.873886799926947,
                49.92399923684779
              ]
            },
            "hh": 5504,
            "postal_code": "64839"
          },
          "geometry": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [
                    8.870207983032461,
                    49.9058558518063
                  ],
                    .
                    .
                    .multiple coordinates are following
                    .
                    .
                ]
              ]
            ]
        }, { ... information on multiple delivery districts following ... }
      ],
      "removed": []
    }

If you want to remove data from your selection, add the this data to the **"remove"** field instead of the **"add"** field:

    PATCH https://print-mailing-api.deutschepost.de/targeting/selections/afa656db-7c1f-44e7-adb6-74ece5e3627b/areacodes
    Content-Type: application/json
    Accept: application/json

    {
      "remove": {
        "districtCodes": [
          "52222-21"
        ]
      }
    }

    HTTP/1.1 200

    {
      "added": [],
      "removed": [
        {
          "id": "52222",
          "type": "Feature",
          "properties": {
            "id": "52222",
            "centroid": {
              "type": "Point",
              "coordinates": [
                6.215589616296115,
                50.78259186178411
              ]
            },
            "hh": 9914,
            "postal_code": "52222"
          },
          "geometry": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [
                    6.196518074681458,
                    50.77214738012821
                  ]
                    .
                    .
                    .multiple coordinates are following
                    .
                    .
                ]
              ]
            ]
          }
        }
    }

## Using filters to target specific recipients with POSTWURFSPEZIAL

When using POSTWURFSPEZIAL, you are able to provide filters targetting only specific recipient groups. The quantity of households returned is lowered to the amount available based on your provided filters. Please note this works only for the detailLevel "POSTAL_CODE", otherwise you will get an error.

**selections/{selection_id}/filters**

where **{selection_id}** is replaced by your selection ID. In the body, you include all filters that you want to be used. Filters are changed by sending the full list of all used filters, therefore using the "PUT" method. This is different from endpoints like **selections/{selection_id}/areacodes**

    PUT https://print-mailing-api.deutschepost.de/targeting/selections/4c436792-5573-47ff-8154-1de446a14319/filters
    Content-Type: application/json
    Accept: application/json
    Accept-Encoding: gzip

    [
      {
        "id": "k_kaufkr_kombi",
        "options": ["1"]
      },
      {
        "id": "k_dom_geschlecht_geb",
        "options": ["3", "4"]
      }
    ]

    HTTP/1.1 200

    {
      "meta": {
        "id": "c529a31d-41e9-43d6-87f5-fed728608f46",
        "type": "POSTAL_CODE",
        "productType": "POSTWURF_SPEZIAL",
        "numberOfAreas": 3,
        "netQuantity": 17937,
        "quantity": 1233,
        "filters": [
          {
            "id": "k_kaufkr_kombi",
            "options": [
              "1"
            ]
          },
          {
            "id": "k_dom_geschlecht_geb",
            "options": [
              "3",
              "4"
            ]
          }
        ]
      },
      "areaCodes": [
        "53129",
        "53119",
        "53117"
      ]
    }

Get all supported filter details from the api endpoint pwsp-filter.

## Receiving all possible filters with options and description via OPTIONS API call

**pws-filter**

This API call responds with all available filters, their options and descriptions.

    OPTIONS https://print-mailing-api.deutschepost.de/targeting/pws-filter
    Accept: application/json
    Accept-Encoding: gzip

    HTTP/1.1 200

    [
      {
        "topic": "k_kaufkr_kombi",
        "name": "Kaufkraft (gewichtet)",
        "description": "Die Kaufkraft-Information liegt auf Straßenabschnittsebene für durchschnittlich 25 Haushalte vor. Bei der gewichteten Kaufkraft werden Westdeutschland und Ostdeutschland getrennt in sieben jeweils gleich große Klassen unterteilt und dadurch die unterschiedlichen Durchschnittswerte berücksichtigt.",
        "options": {
          "1": "stark unterdurchschnittlich",
          "2": "unterdurchschnittlich",
          "3": "unterer Durchschnitt",
          "4": "Durchschnitt",
          "5": "oberer Durchschnitt",
          "6": "überdurchschnittlich",
          "7": "stark überdurchschnittlich"
        }
      },
      {
        "topic": "k_single_mz",
        "name": "Anteil Singles",
        "description": "Auf Basis der Informationen der Postreferenz-Datei werden Haushalte ermittelt, in denen wahrscheinlich nur eine Person lebt.",
        "options": {
          "1": "extrem niedrig",
          "2": "sehr niedrig",
          "3": "niedrig",
          "4": "mittel",
          "5": "hoch",
          "6": "sehr hoch",
          "7": "extrem hoch"
        }
      },
      ...
    ]

## Reading the selection

Now that you are done selecting, you want to extract the needed information to actually use your selection data. In order to do this, you call the HTTP "GET" method:

**selections/{selection_id}/?level={level}**

As you can see, the call has a query parameter called "level". Its value can be set to 3 different values:

| Level  |                                   Data returned                                   |
|--------|-----------------------------------------------------------------------------------|
| META   | Returns meta data of the selection including area codes (default).                |
| DETAIL | Returns detailed data of the selection, including the selection shapes (geodata). |

The one value you are using in the following example is the **"META"** parameter. Using this level gives you the following result:

    GET https://print-mailing-api.deutschepost.de/targeting/selections/3cb2c0b7-e96c-4e58-9768-4e6bfcc59285?level=META
    Content-Type: application/json
    Accept: application/json
    Accept-Encoding: gzip

    HTTP/1.1 200

    {
      "meta": {
        "id": "b725b034-4e38-495e-a861-e0cf463a8119",
        "type": "POSTAL_CODE",
        "productType": "POSTAKTUELL_ALL",
        "numberOfAreas": 64,
        "netQuantity": 303639,
        "quantity": 303639,
        "filters": [],
        "filterCosts": 0
      },
      "areaCodes": [
        "64395",
        "64397",
        "64750",
        "64668",
        "64747",
        .
        .
        multiple postal codes are following
        .
        .
      ]
    }

With this data you gain an overview of the currently selected postal codes.

## Extracting an enumeration from your selection

If you want to evaluate your selection and see how many households will receive a mailing, you can call the HTTP "GET" method:

**selections/{selection_id}/dispatch-information**

Using this method gives you the following result that can be used in conjunction with the Dispatch-Preparation API:

    GET https://print-mailing-api.deutschepost.de/targeting/selections/3cb2c0b7-e96c-4e58-9768-4e6bfcc59285/dispatch-information
    Content-Type: application/json
    Accept: application/json
    Accept-Encoding: gzip

    HTTP/1.1 200

    {
      "numberItemsTariffZoneA": 168667,
      "numberItemsTariffZoneB": 134972,
      "itemWeightInGram": 20,
      "productType": "POSTAKTUELL_ALL",
      "deliveryDistrictSelection": false,
      "inductionDate": "2021-06-24T07:43:56.001Z"
    }

## Creating a snapshot of your selection

Your selection is only temporarily saved in the server cache. However, you can get an immutable snapshot of your selection with an extended lifespan by calling:

**/selections/{selection_id}/save**

where **{selection_id}** is replaced by your selection ID.

    POST https://print-mailing-api.deutschepost.de/targeting/selections/afa656db-7c1f-44e7-adb6-74ece5e3627b/save
    Content-Type: application/json

    HTTP/1.1 200
    {
        "id": "afa656db-7c1f-44e7-adb6-74ece5e3627b",
        "expiryDate": [
            2021,
            9,
            7
        ]
    }

As you can see, you receive a body with a new (semi)permanent ID which represents a read-only copy of your selection, and an expiry date on which your copy will be automatically deleted.

To review this copy of your selection, you can call the HTTP "GET" method:

**/permalinks/{permalinkId}**

where **{permalinkId}** is replaced by your newly acquired ID.

In case the data basis has changed you get a new "dataVersion" in the response and if your selected areas changed you find this information in the "areaCodeHouseholdDifferences" list.

    GET https://print-mailing-api.deutschepost.de/targeting/permalinks/afa656db-7c1f-44e7-adb6-74ece5e3627b

    HTTP/1.1 200
    {
        "meta": {
            "id": "afa656db-7c1f-44e7-adb6-74ece5e3627b",
            "type": "POSTAL_CODE",
            "productType": "POSTAKTUELL_ALL",
            "numberOfAreas": 0,
            "netQuantity": 0,
            "quantity": 0,
            "dataVersion": "2011-09-01T23:56:99",
            "filters": [],
            "filterCosts": 0
        },
        "areaCodes": [
            "64395",
            "64397",
            "64750",
            .
            .
            . multiple area codes following
            .
            .
        ],
        "areaCodeHouseholdDifferences": [
          {
            "areaCode": "64395",
            "households": -1
          }
        ]
    }

If your original selectionId is no longer in the application's cache, you can reload it for further editing. To do this, you can call the HTTP "POST" method:

**/selections/load?permalinkId={permalinkId}**

where **{permalinkId}** is replaced by the ID you received when saving your selection.

In case the data basis has changed you get a new "dataVersion" in the response and if your selected areas changed you find this information in the "areaCodeHouseholdDifferences" list. Please note only the new "dataVersion" is available for further editing of your selection.

    POST https://print-mailing-api.deutschepost.de/targeting/selections/load?permalinkid=afa656db-7c1f-44e7-adb6-74ece5e3627b
    Content-Type: application/json
    Accept: application/json
    Accept-Encoding: gzip

    HTTP/1.1 200
    {
        "meta": {
            "id": "7475f415-6806-45d1-81d8-84c1145dcd3d",
            "type": "POSTAL_CODE",
            "productType": "POSTAKTUELL_ALL",
            "numberOfAreas": 0,
            "netQuantity": 0,
            "quantity": 0,
            "dataVersion": "2011-09-01T23:56:99",
            "filters": [],
            "filterCosts": 0
        },
        "areaCodes": [
            "64395",
            "64397",
            "64750",
            .
            .
            . multiple area codes following
            .
            .
        ],
        "areaCodeHouseholdDifferences": [
          {
            "areaCode": "64395",
            "households": -1
          }
        ]
    }

With the new ID you can now use all **/selection/{selectionId}** requests again. The save and load requests for one id are limited to three requests per hour to prevent abuse.

And that is it. These are the core concepts of this API. For more specific information on the requests, please refer to the [OpenAPI definitions](#reference-docs-section/).

# Using the authenticated part of the API (this is required to proceed to booking or planning)

Everything in the following endpoints is tied to a customer id, therefore the endpoints are starting with: **/targeting/customers/{customer_id}/** To use these resources you need to provide a JWT with the adequate access rights and customer rights.

## Listing all selections of a customer

With the following request you can list all selections of a customer:

**/targeting/customers/{customer_id}/selections**

    GET https://print-mailing-api.deutschepost.de/targeting/customers/1/selections
    Content-Type: application/json
    Accept: application/json

    HTTP/1.1 200
    [
      {
        "id": "1ab4c485-535f-474f-8dd3-4bf005bd3db2",
        "name": "foo"
      },
      {
        "id": "51be899e-ca6f-43a4-b8f1-a659a773fdea",
        "name": "bar"
      }
    ]

## Get a specific selection

With the following request you can get a specific selection of a customer:

**/targeting/customers/{customer_id}/selections/{selection_id}**

The normal behavior of this request is to simply return the saved values from the DB, but this request has an optional url parameter "recalculate" which can be set to true, to trigger a recalculation of all values based on the selected areas, occurring changes show in the filed "areaCodeHouseholdDifferences". We recommend the use of the "recalculation" flag to assure the data response contains the newest data.

    GET https://print-mailing-api.deutschepost.de/targeting/customers/1/selections/51be899e-ca6f-43a4-b8f1-a659a773fdea
    Content-Type: application/json
    Accept: application/json

    HTTP/1.1 200
    {
      "meta": {
        "id": "51be899e-ca6f-43a4-b8f1-a659a773fdea",
        "customerId": "11",
        "name": "bar",
        "type": "POSTAL_CODE",
        "productType": "POSTAKTUELL_DAILY_MAIL",
        "numberOfAreas": 1,
        "netQuantity": 18542,
        "quantity": 12926,
        "itemWeightInGram": 20,
        "dataVersion": "2011-09-01T23:56:99",
        "filters": [],
        "filterCosts": 0
      },
      "areaCodes": [
        "31535"
      ],
      "areaCodeHouseholdDifferences": []
    }

## Save a new selection for a customer

To save a new selection you can use the following url as a POST:

**/targeting/customers/{customer_id}/selections**

    POST https://print-mailing-api.deutschepost.de/targeting/customers/1/selections
    Content-Type: application/json
    Accept: application/json

    {
      "name": "Fooo",
      "areaCodes": [
        "31535"
      ],
      "detailLevel": "POSTAL_CODE",
      "product": "POSTAKTUELL_DAILY_MAIL",
      "filters": [],
      "weight": "20"
    }

    HTTP/1.1 200
    {
      "id": "41d709d6-3a96-41e2-89d6-da51a4a07f96",
      "name": "Fooo"
    }

The return value contains the {selection_id} which is the main identifier for other processes with this selection.

## Change a selection

A selection can be changed by its id, but note that the customer_id can not be changed. The detailLevel of a "POSTWURF_SPEZIAL" selection has to be "POSTAL_CODE", otherwise you will get an error.

**/targeting/customers/{customer_id}/selections/{selection_id}**

    PATCH https://print-mailing-api.deutschepost.de/targeting/customers/1/selections/41d709d6-3a96-41e2-89d6-da51a4a07f96
    Content-Type: application/json
    Accept: application/json

    {
      "name": "FooBar",
      "areaCodes": [
        "20257"
      ],
      "detailLevel": "POSTAL_CODE",
      "product": "POSTAKTUELL_DAILY_MAIL",
      "filters": [],
      "weight": "20"
    }

    HTTP/1.1 200
    {
      "id": "41d709d6-3a96-41e2-89d6-da51a4a07f96",
      "name": "FooBar"
    }

If you change "detailLevel" of the selection you have to adjust the areaCodes to the new value, areaCodes which are not found within the new "detailLevel" will be removed. If you change away from the product "POSTWURF_SPEZIAL" and you have filters defined you'll get an error.

## Rename a selection

A selection can be renamed by its id.

**/targeting/customers/{customer_id}/selections/{selection_id}/name**

    PATCH https://print-mailing-api.deutschepost.de/targeting/customers/1/selections/41d709d6-3a96-41e2-89d6-da51a4a07f96/name
    Accept: application/json

    Subar

    HTTP/1.1 200
    {
      "id": "41d709d6-3a96-41e2-89d6-da51a4a07f96",
      "name": "Subar"
    }

## Get a dispatch information preview of the data from a selection

A selection can be deleted by its id.

**/targeting/customers/{customer_id}/selections/{selection_id}/dispatch-information**

    GET https://print-mailing-api.deutschepost.de/targeting/customers/1/selections/41d709d6-3a96-41e2-89d6-da51a4a07f96/dispatch-information
    Accept: application/json

    HTTP/1.1 200
    {
      "numberItemsTariffZoneA": 0,
      "numberItemsTariffZoneB": 1695,
      "itemWeightInGram": 20,
      "productType": "POSTAKTUELL_DAILY_MAIL",
      "deliveryDistrictSelection": false,
      "inductionDate": "2021-12-21T12:57:42.001Z"
    }

## Delete a selection

A selection can be deleted by its id.

**/targeting/customers/{customer_id}/selections/{selection_id}**

    DELETE https://print-mailing-api.deutschepost.de/targeting/customers/1/selections/41d709d6-3a96-41e2-89d6-da51a4a07f96/name
    Accept: application/json

    HTTP/1.1 200



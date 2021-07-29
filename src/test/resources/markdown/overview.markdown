The **DHL Label Generator API** produces labels and other documents for a given DHL shipping product in different formats (e.g. PDF, ZPL, PNG).

### Scope

This API provides you with labels for different DHL shipping products. At its current stage, this API is intended to be consumed by **DPDHL**internal developers only.

Currently, the API covers the following products:

| Business Unit |           Product            |
|---------------|------------------------------|
| DHL eCommerce | Parcel International         |
| DHL eCommerce | Parcel Connect               |
| DHL eCommerce | Parcel Connect Undeliverable |
| DHL eCommerce | Parcel Return Connect        |
| DGF           | Housebill                    |
| Group Wide    | CN23 Customs Form            |
| Group Wide    | QR Code                      |

Do you have a label request that could impact your business? Contact us! Please get in touch with [Mario Antonio Alvarez Enriquez](mailto:marioaae@dpdhl.com) (Product Owner).

#### DHL Parcel International

|----------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![Parcel International 10x20](/sites/default/files/inline-images/parcel_international_10x20_0.png) | * Business Unit: eCommerce * Product: Parcel International * Product Owner: Miriam Criado (miriam.criado@dhl.com) * Document dimensions: 10x20 and 10x15 cms * Format support: PDF, ZPL |
| ![Parcel International 10x20](/sites/default/files/inline-images/parcel_international_10x20_0.png) | ![Parcel International 10x15](/sites/default/files/inline-images/parcel_international_10x15_0.png)                                                                                      |

#### DHL Parcel Connect {#cke_bm_347S}

|--------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![Parcel Connect 10x15](/sites/default/files/inline-images/parcel_connect_10x15.png) | * Business Unit: eCommerce * Product: Parcel Connect * Product Owner: Beate Belitz-Persé (beate.belitz@dhl.com) * Document dimensions: 10x15 cms * Format support: PDF, ZPL |

#### DHL Parcel Connect Undeliverable {#cke_bm_141S}

|------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![Parcel Connect Undeliverable 10x15](/sites/default/files/inline-images/parcel_connect_undeliverable_10x15.png) | * Business Unit: eCommerce * Product: Parcel Connect Undeliverable * Product Owner: Hannes Richter (hannes.richter@dhl.com) * Document dimensions: 10x15 cms * Format support: PDF, ZPL |

#### DHL Parcel Return Connect

|------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![Parcel Return Connect](/sites/default/files/inline-images/parcel_return_connect_10x15.png){#cke_bm_378S} | * Business Unit: eCommerce * Product: Parcel Return Connect * Product Owner: Hannes Richter (hannes.richter@dhl.com) * Document dimensions: 10x15 cms * Format support: PDF, ZPL |

#### Housebill

|------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| ![Housebill 10x13](/sites/default/files/inline-images/housebill_10x13_1.png) | * Business Unit: DGF * Product: Housebill * Product Owner: Benjamin Baschab * Document dimensions: 102x128 mm * Format support: PDF, ZPL |

#### Customs Form CN23

|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| * Business Unit: Group wide * Product Owner: Group APIs (marioaae@dpdhl.com) * Document dimensions: DIN A4 * Format support: PDF * Additional features: * Attachment pages: when the list of items exceeds 4 items, additional pages will be created with the list of contents. |
| ![Customs Form CN23](/sites/default/files/inline-images/cn23.png)                                                                                                                                                                                                               |

#### QR Code

|------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![QR Code](/sites/default/files/inline-images/qr_code.png) | * Business Unit: Group wide * Product Owner: Group APIs (marioaae@dpdhl.com) * Document dimensions: specified by the user, it's always a square. * Format support: PNG |

### Using the API

1. You need to retrieve an access-token with your credentials. This token will allow you to make calls to generate labels. Detailed information about [authentication](https://developer.dhl.com/api-reference/label-generator#get-started-section/user-guide)can be found [here](https://developer.dhl.com/api-reference/label-generator#get-started-section/user-guide).
2. By calling any of the available methods, the DHL Label Generator API allows the creation of a DHL shipment label or a supporting document. The label will include the information provided within the request-body such as

    - shipper and receiver information,
    - routing and tracking code,
    - billing information,
    - quantity and weight information.

   The [Reference Docs](https://developer.dhl.com/api-reference/label-generator#reference-docs-section) give a full description of the information required and allowed in the request body of each of the supported labels and documents.
3. After a successful request, the API will return a response-body in JSON format with a field "label".

    - ZPL labels will be returned as plain-text which can be read directly by a ZPL reader.
    - PDF and PNG labels will be encoded in a base64 scheme in order to reliably transfer the label in text format over the response of this API. In this case, the label needs to be decoded from base64, back to PDF. Decoding is possible by using any base64 utility available in most systems. Below some examples:

**Linux-based Systems (bash)**

    $ base64 --decode encodedLabel.txt > label.pdf

**Windows-based Systems (cmd)**

    C:\> certutil -decode encodedLabel.txt label.pdf

### Example Use Cases

**Create DHL shipment label for a given DHL shipping product**

With each request to the API, the shipper address, receiver address, billing number, routing code, tracking number, quantity, weight and label format for a given DHL shipping product need to be defined. There is no business logic entailed in the API to create or infer such information. The API will simply generate and return a DHL shipment label in pdf or ZPL format.
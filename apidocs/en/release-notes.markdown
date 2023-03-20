# Latest Release Notes

## 2023-02-08

The **Print-Mailing Automation API moved** to the DPDHL group API Domain under the following URLs:

* https://api-eu.dhl.com/post/advertising/print-mailing - Production environment
* https://api-uat.dhl.com/post/advertising/print-mailing - Test environment

Besides the new base URL, the [Print-Marketing APIs](https://developer.dhl.com/api-catalog?f%5B0%5D=api_catalog_dhl_division%3A38&f%5B1%5D=api_catalog_service%3A89) received a path-based versioning, including the users api, for authentication. Please change your URLs according the following examples.

* Old: <https://print-mailing-api.deutschepost.de/targeting/...>
* New: <https://api-eu.dhl.com/post/advertising/print-mailing/targeting/v1/...>


* Old: <https://print-mailing-api.deutschepost.de/targeting/errorcode-lookups>
* New: <https://api-eu.dhl.com/post/advertising/print-mailing/targeting/v1/errorcode-lookups>


* Old: <https://print-mailing-api.deutschepost.de/user/authentication/...>
* New: <https://api-eu.dhl.com/post/advertising/print-mailing/user/v1/authentication/...>


* Old: <https://print-mailing-api.deutschepost.de/user/authentication/businesslogin>
* New: <https://api-eu.dhl.com/post/advertising/print-mailing/user/v1/authentication/businesslogin>

> HINT: The new integrated [Openapi Viewer](https://developer.dhl.com/api-reference/print-mailing-targeting-api#reference-docs-section) can generate all required URLs to the endpoints.

### 2023-01-19

Added a validation to the /selection endpoints for "POSTWURF_SPEZIAL".

* A selection with the product "POSTWURF_SPEZIAL" is only allowed for the detailLevel "POSTAL_CODE.

#### 2022-11-04

Added more filters for "POSTWURF_SPEZIAL" product.

* Nearly 100 filters can be used in a selection. See endpoint pwsp-filter to get all details of filters.

#### 2022-07-01

Added a new authenticated endpoints section

* changed the usage description of the unauthenticated endpoints sections

#### 2021-12-15

Added hint to use Accept-Encoding header for every request.

#### 2021-11-02

Added further information on changing data for the permalink functionality.

* Changed the response of the Endpoint "/selections/load".

#### 2021-08-24

The base url of the authentication resource changed! **Please change your base urls!**

|                          Old url                           |                             New url                             |
|------------------------------------------------------------|-----------------------------------------------------------------|
| <https://print-mailing-api.deutschepost.de/authentication> | <https://print-mailing-api.deutschepost.de/user/authentication> |

#### 2021-07-21

Added a new Endpoint to the API "selections/load".

* Added a new Endpoint to search for ID's "/search".
* Added a new Endpoint to the API "selections/{selectionID}/filters".

#### 2021-06-15

Created end user documentation for Targeting API.

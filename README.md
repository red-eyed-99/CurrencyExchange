# Currency Exchange REST API
## 📋Description
REST API for describing currencies and exchange rates. Allows you to view and edit lists of currencies and exchange rates, and perform conversions from one currency to another.
## ⚙Technologies
- JavaEE
- SQLite
- Maven
- Tomcat (deploy)
## 🌐Endpoints
### 💵Currency
**GET /currencies** - get list of all currencies

_response example:_
``` json
[
  {
    "id": 1,
    "code": "AUD",
    "name": "Australian Dollar",
    "sign": "A$"
  },
  {
    "id": 2,
    "code": "BHD",
    "name": "Bahraini Dinar",
    "sign": ".د.ب"
  }
]
```
**GET /currency/RUB** - get a specific currency

_response example:_
``` json
{
  "id": 4,
  "code": "RUB",
  "name": "Russian Ruble",
  "sign": "₽"
}
```
**POST /currencies** - add new currency to database

the request is sent in **x-www-form-urlencoded** format with the following form fields: **name**, **code**, **sign**

_response example:_
``` json
{
  "id": 23,
  "code": "JPY",
  "name": "Yen",
  "sign": "¥"
}
```
### 💹Exchange Rate

**GET /exchangeRates** - get list of all exchange rates

_response example:_
``` json
[
  {
    "id": 1,
    "baseCurrency": {
      "id": 6,
      "code": "EUR",
      "name": "Euro",
      "sign": "€"
    },
    "targetCurrency": {
      "id": 5,
      "code": "USD",
      "name": "US Dollar",
      "sign": "$"
    },
    "rate": 1.1171
  }
]
```
**GET /exchangeRate/USDCHF** - get a specific exchange rate

_response example:_
``` json
{
  "id": 4,
  "baseCurrency": {
    "id": 5,
    "code": "USD",
    "name": "US Dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 9,
    "code": "CHF",
    "name": "Swiss Franc",
    "sign": "Fr."
  },
  "rate": 0.8412
}
```
**POST /exchangeRates** - add new exchange rate to database

the request is sent in **x-www-form-urlencoded** format with the following form fields: **baseCurrencyCode**, **targetCurrencyCode**, **rate**

_response example:_
``` json
{
  "id": 0,
  "baseCurrency": {
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  },
  "targetCurrency": {
    "id": 1,
    "name": "US Dollar",
    "code": "USD",
    "sign": "$"
  },
  "rate": 1.1171
}
```
**PATCH /exchangeRate/EURUSD**

the request is sent in **x-www-form-urlencoded** format with **rate** form field 

_response example:_
``` json
{
  "id": 0,
  "baseCurrency": {
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  },
  "targetCurrency": {
    "id": 1,
    "name": "US Dollar",
    "code": "USD",
    "sign": "$"
  },
  "rate": 2.43
}
```
### 💱Currency Exchange

**GET /exchange?from=AUD&to=GBP&amount=5** - conversion of a certain amount of currency into another currency

_response example:_
``` json
{
  "baseCurrency": {
    "id": 1,
    "code": "AUD",
    "name": "Australian Dollar",
    "sign": "A$"
  },
  "targetCurrency": {
    "id": 8,
    "code": "GBP",
    "name": "Pound Sterling",
    "sign": "£"
  },
  "rate": 1.00,
  "amount": 5.00,
  "convertedAmount": 2.58
}
```
### ⛔Error message

_response when error occurs example:_
``` json
{
  "status": 400,
  "message": "Amount parameter value must be a positive number and greater than zero"
}
```
### 💻How to run project locally
- Setup Tomcat
- Create **context.xml** in **webapp/META-INF**

_example_
``` xml
<resource name="jdbc/your app name" 
   auth="Container"
   description="DB Connection" 
   type="javax.sql.DataSource" 
   maxActive="10"
   maxIdle="5" 
   maxWait="10000"
   driverClassName="org.sqlite.JDBC"
   url="jdbc:mysql:jdbc:sqlite:path/to/currency_exchange.db"/>
</resource>
```
- Add reference in **webapp/WEB-IN/web.xml**
``` xml
<resource-ref>
    <res-ref-name>jdbc/your app name</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```
- Run application on Tomcat


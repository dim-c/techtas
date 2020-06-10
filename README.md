## Requirements
Problem definition

Create a tiny RESTful web service with the following business requirements:
Application must expose REST API endpoints for the following functionality:
- apply for loan (loan amount, term, name, surname and personal id must be provided);
- list all approved loans;
- list all approved loans by user.

Service must perform loan application validation according to the following rules and reject application if:
- application comes from blacklisted personal id;
- N application / second are received from a single country (essentially we want to limit number of loan applications coming from a country in a given time frame).

Service must perform origin country resolution using a web service (you should choose one) and store country code together with the loan application. Because network is unreliable and services tend to fail, let's agree on default country code - "lv".

Technical requirements:
You have total control over framework and tools, as long as application is written in Java. Feel free to write tests in any JVM language.

What gets evaluated:
- conformance to business requirements;
- code quality, including testability;
- how easy it is to run and deploy the service (don't make us install Oracle database please ;)

# Techtask
**It's a Spring Boot Application and can be run using java -jar**
## API:
### /loans GET:
Getting all existed loans
### /loans POST:
Apply for loan. Creates Loan and Customer if not exists. Body type - JSON. e.g.:  
`{
  "customer": {
    "name": "UserName",
    "surname": "UserSur",
    "id": 10071
  },
  "loan": {
    "amount": 1000,
    "term": "2020-05-19T22:33:10.0419353"
  }
}`
### /customer/{id}/loans GET: 
Gets all Loans by Customer id

### /loans?page={page}&size={size}     
For best perfoamce paginagion was provided 
E.g.: /loans?page=0&size=3
  
    

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
  
    

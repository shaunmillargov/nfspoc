# nfspoc
A POC for NFS large file retrieval.  
  
## Entry point
http://localhost:8090/demo/start

## Enviro Variables
| Name  | Example Value |
| ------------- | ------------- |
| POC_ORDS_ENDPOINT  | http://myendpoint  |
| POC_ORDS_APP_ID  | 12345  |
| POC_ORDS_APP_PWD | 12345  |
| POC_ORDS_TICKET_LIFETIME  | 120  |
| POC_WRAPPER_BASEPATH | http://myendpoint |

## Installation
Once repo is cloned and environmental variables set:
```
mvn clean
mvn install
mvn spring-boot:run
```  


 

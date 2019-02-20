# samurai-chef-alexa-skill
Skill game for Amazon Alexa like fruit ninja

## Documentation
- https://docs.google.com/document/d/1k1x2lpOMGh5sbF84KtWgylsGiOz_3m0JfIS260C1Um0/edit?ts=5bc4b812#
- https://docs.google.com/document/d/1CN_OAWge4sewDMouTh4eSEzgpUJeQ5QUAvvdmI1xcXg/edit#heading=h.ot6dhoz58t1t
- https://www.lucidchart.com/documents/edit/2cbf03be-0fa8-43db-8f23-679937e5faf0/0
- https://www.lucidchart.com/documents/edit/09202be7-1717-42c4-8778-123703355299/0

## Build
#### with tests
mvn clean assembly:assembly -DdescriptorId=jar-with-dependencies package
#### without tests
mvn clean assembly:assembly -DdescriptorId=jar-with-dependencies -DskipTests package

#### 
Time to time needs `export MAVEN_OPTS=-Xss32m`

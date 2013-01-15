This sample app uses Spring For Android RestTemplate to interact (using GET, POST, PUT and DELETE HTTP verbs) with a web service (http://www.
restfulapp.appspot.com/rest/recipes) designed to read, post, update and delete recipes.

Here are the equivalent curl calls :

    curl --user s4a:s4a -H "Accept: application/json" -i http://www.restfulapp.appspot.com/rest/recipes 
    curl --user s4a:s4a -H "Accept: application/json" -i http://www.restfulapp.appspot.com/rest/recipes/0
    curl --user s4a:s4a -H "Accept: application/json" -H "Content-type: application/json" -X PUT -d '{"id":"0", "title":"a title","description":"a description","type":"a type","author":"an author"}' -i http://www.restfulapp.appspot.com/rest/recipes/
    curl --user s4a:s4a -H "Accept: application/json" -H "Content-type: application/json" -X POST -d '{"title":"a title","description":"a description","type":"a type","author":"an author"}' -i http://www.restfulapp.appspot.com/rest/recipes/
    curl --user s4a:s4a -H "Accept: application/json" -X DELETE -i http://www.restfulapp.appspot.com/rest/recipes/1

![Screenshot](https://raw.github.com/anthonydahanne/spring-for-android-starter-book/master/spring-for-android-ch3-restful-example-recipeapp/screenshot1.png "Screenshot")

![Screenshot](https://raw.github.com/anthonydahanne/spring-for-android-starter-book/master/spring-for-android-ch3-restful-example-recipeapp/screenshot2.png "Screenshot")

### Dependencies : 
* spring-android-rest-template-1.0.0.RELEASE.jar
* spring-android-core-1.0.0.RELEASE.jar
* jackson-mapper-asl-1.9.10.jar
* jackson-core-asl-1.9.10.jar

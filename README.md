# Web Crawler with Finagle
Scrape below blogs:
- [Google Developers Blog](https://developers.googleblog.com/)
- [Google Developers Japan](https://developers-jp.googleblog.com/)
- [Android Developers Blog](https://android-developers.googleblog.com/)

## Getting Started
* Install Docker Compose    
https://docs.docker.com/compose/install/

## Run application

```
$ sbt 'run-main app.Server'
```

## Run with Docker

``` 
$ sbt docker:publishLocal
$ docker-compose build
$ docker-compose run
```

## MySQL container shell

```
$ docker exec -it finagle-web-crawler_db_1 bash
```

- https://docs.docker.com/samples/library/mysql/

Refs
- https://www.scala-sbt.org/sbt-native-packager/formats/docker.html

## Deploy fat JAR
* Create a JAR file

```
$ sbt assembly

```

* Run process

```
$ java -jar target/scala-2.12/finagle-web-crawler-assembly-1.0.jar &
```

* Refs  
https://github.com/sbt/sbt-assembly

## Feed
### GET feed/googleblog/developers
* Example

```
$ curl -X GET 'http://localhost:8080/feed/googleblog/developers'
```

### GET feed/googleblog/developers_jp
* Example

```
$ curl -X GET 'http://localhost:8080/feed/googleblog/developers_jp'
```

## API
### GET api/googleblog/developers
* Example

```
$ curl -X GET 'http://localhost:8080/api/googleblog/developers?count=5&page=0'
```

### GET api/googleblog/developers_jp
* Example

```
$ curl -X GET 'http://localhost:8080/api/googleblog/developers_jp?count=5&page=0'
```

### GET api/developers/android
* Example

```
$ curl -X GET 'http://localhost:8080/api/developers/android?count=5&page=0'
```
## Execute scraping by manual
### GET scrape/googleblog/developers
* Example

```
$ curl -X GET 'http://localhost:8080/scrape/googleblog/developers'
```

### GET scrape/googleblog/developers_jp
* Example

```
$ curl -X GET 'http://localhost:8080/scrape/googleblog/developers_jp'
```

### GET scrape/googleblog/android
* Example

```
$ curl -X GET 'http://localhost:8080/scrape/googleblog/android'
```

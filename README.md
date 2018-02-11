# REST APIs with Finagle
## Getting Started

* Create a Database (MySQL)  

```sql
CREATE SCHEMA IF NOT EXISTS `web_clawler` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

* Start server

``` 
$ sbt 'run-main app.api.ApiServer'
```
    
## Feed
### GET feed/developers/googleblog
#### Example

```
curl -X GET 'http://localhost:8080/feed/developers/googleblog'
```

### GET feed/developers/googleblog/jp
#### Example

```
curl -X GET 'http://localhost:8080/feed/developers/googleblog/jp'
```
## API
### GET api/developers/googleblog
#### Example

```
curl -X GET 'http://localhost:8080/api/developers/googleblog'
```

### GET api/developers/googleblog/jp
#### Example

```
curl -X GET 'http://localhost:8080/api/developers/googleblog/jp'
```

### GET api/developers/android
#### Example

```
curl -X GET 'http://localhost:8080/api/developers/android'
```
## Execute scraping
### GET scrape/developers/googleblog
#### Example

```
curl -X GET 'http://localhost:8080/scrape/developers/googleblog'
```

### GET scrape/developers/googleblog/jp
#### Example

```
curl -X GET 'http://localhost:8080/scrape/developers/googleblog/jp'
```

### GET scrape/developers/android
#### Example

```
curl -X GET 'http://localhost:8080/scrape/developers/android'
```
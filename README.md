<div align="center">
  <a href="https://github.com/HideyoshiNakazone/hideyoshi.com">
    <img src="https://drive.google.com/uc?export=view&id=1ka1kTMcloX_wjAlKLET9VoaRTyRuGmxQ" width="100" height="100" allow="autoplay"\>
  </a>
</div>

# backend-hideyoshi.com

Made using Spring and Java17, this project was made as the backend of the [hideyoshi.com project](https://github.com/HideyoshiNakazone/hideyoshi.com), as so it is  mainly responsible for user authentication and user sessions.

All code in this repo is distributed freely by the GPLv3 License.
## Environment Variables

For the execution of this project the following environment variables must be set:

`FRONTEND_PATH`: for allowed origins

`TOKEN_SECRET`

`ACCESS_TOKEN_DURATION`

`REFRESH_TOKEN_DURATION`

- Default User Configuration:

    `DEFAULT_USER_FULLNAME`

    `DEFAULT_USER_EMAIL`
    
    `DEFAULT_USER_USERNAME`
    
    `DEFAULT_USER_PASSWORD`

- Storage Service MicroService:

    `STORAGE_SERVICE_PATH`

- OAuth2 Configuration:

    - Google:

        `GOOGLE_CLIENT_ID`

        `GOOGLE_CLIENT_SECRET`

        `GOOGLE_REDIRECT_URL`

    - Github:

        `GITHUB_CLIENT_ID`

        `GITHUB_CLIENT_SECRET`

        `GITHUB_REDIRECT_URL`

- Database:

    `DATABASE_URL`

    `DATABASE_USERNAME`

    `DATABASE_PASSWORD`

- Redis:

    `REDIS_URL`

    `REDIS_PORT`

    `REDIS_PASSWORD`
## Usage

Building project:

```bash
./mvnw -Dmaven.test.skip -f pom.xml clean package
```

Executing project:

```bash
java -jar target/backend-*.jar
```
## API Reference

#### Get all users - ADMIN Permission Required

```http
  GET /user
```

#### User Signup - Open Endpoint

```http
  POST /user/signup
```

| Parameter      | Type     | Description                          |
| :--------      | :------- | :-------------------------           |
| `name`         | `string` | **Required**. Fullname               |
| `email`        | `string` | **Required**. Email                  |
| `username`     | `string` | **Required**. Username               |
| `password`     | `string` | **Required**. Password               |

#### Login - Open Endpoint - x-www-form-urlencoded

```http
  POST /user/login
```

| Parameter          | Type     | Description               |
| :--------          | :------- | :-------------------------|
| `username`         | `string` | **Required**.             |
| `password`         | `string` | **Required**.             |



#### Refresh Session - Open Endpoint

```http
  POST /user/login/refresh
```

| Parameter          | Type     | Description               |
| :--------          | :------- | :-------------------------|
| `refreshToken`     | `string` | **Required**.             |

#### Delete Own User - User Permission Required

```http
  DELETE /user/delete
```

#### Delete User by Id - ADMIN Permission Required

```http
  DELETE /user/delete/:id
```

#### Add Profile Picture to User - User Permission Required

```http
  POST /user/profile-picture
```

| Parameter          | Type     | Description               |
| :--------          | :------- | :-------------------------|
| `fileType`         | `string` | **Required**.             |

#### Delete Profile Picture - User Permission Required

```http
  DELETE /user/profile-picture
```

#### Process Profile Picture - User Permission Required

```http
  POST /user/profile-picture/proccess
```

### User Response

```json
{
    "id": int,
    "name": string,
    "email": string,
    "username": string,
    "roles": string[],
    "provider": string,
    "accessToken": {
        "token": string,
        "expirationDate": datetime
    },
    "refreshToken": {
        "token": string,
        "expirationDate": datetime
    }
}
```
## Authors

- [@HideyoshiNakazone](https://github.com/HideyoshiNakazone)


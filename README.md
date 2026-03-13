# One-to-One Chat

Spring Boot 3 + MongoDB backend for one-to-one chat with:
- REST APIs for users, chat sessions, friendships, and message history
- WebSocket/STOMP for realtime messaging and online/offline events
- OpenAPI (Swagger UI) for REST endpoint discovery

## Tech Stack

- Java 17
- Spring Boot 3.3.5
- Spring Web + WebSocket (STOMP + SockJS)
- Spring Data MongoDB
- SpringDoc OpenAPI (Swagger UI)

## Project Structure

- `src/main/java/com/haskar/onetoonechat/controller`: REST and STOMP controllers
- `src/main/java/com/haskar/onetoonechat/service`: business logic
- `src/main/java/com/haskar/onetoonechat/model`: MongoDB documents and enums
- `src/main/java/com/haskar/onetoonechat/respository`: Spring Data repositories
- `src/main/java/com/haskar/onetoonechat/config`: WebSocket + OpenAPI configuration
- `src/main/resources/static`: frontend files

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use `mvnw`)
- MongoDB running locally on `localhost:27017`

You can start MongoDB with Docker:

```bash
docker compose up -d
```

The app is configured in `src/main/resources/application.yml` to use:
- database: `chatApp`
- username: `haskar`
- password: `haskar`

## Run the Application

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Default server URL: `http://localhost:8080`

## OpenAPI / Swagger

After startup, open:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Note: OpenAPI documents REST endpoints. STOMP/WebSocket endpoints are documented below manually.

## REST Endpoints

### User

| Method | Path | Description |
|---|---|---|
| POST | `/user` | Create a user |
| GET | `/user/{id}` | Get user by id |
| GET | `/user/by-username/{nickname}` | Get user by nickname |
| GET | `/user/search/{nickname}` | Search users by partial nickname (case-insensitive) |

Example `POST /user` body:

```json
{
  "fullName": "Alice Doe",
  "nickName": "alice",
  "email": "alice@example.com",
  "gender": "FEMALE",
  "settings": {
    "allowMessages": true
  }
}
```

### Chat Session

| Method | Path | Description |
|---|---|---|
| POST | `/chat-session` | Create chat session (when `id` is null) or fetch by id |
| GET | `/chat-session/all/{userId}` | Paginated chat sessions for a user, ordered by last update |

Example `POST /chat-session` body:

```json
{
  "participantsIds": ["userAId", "userBId"],
  "chatType": "PERSONAL"
}
```

Pagination example:
- `GET /chat-session/all/{userId}?page=0&size=10`

### Chat Message

| Method | Path | Description |
|---|---|---|
| GET | `/message/{chatSessionId}` | Paginated message history for a chat session (`MESSAGE` type only) |

Pagination example:
- `GET /message/{chatSessionId}?page=0&size=30`

### Friendship

| Method | Path | Description |
|---|---|---|
| POST | `/friendships` | Create friendship/friend request |
| GET | `/friendships?userId={id}` | Paginated friendships where user is either side |

Example `POST /friendships` body:

```json
{
  "userId1": "userAId",
  "userId2": "userBId",
  "friendshipStatus": "PENDING"
}
```

## WebSocket / STOMP Endpoints

### Connection

- SockJS endpoint: `/ws`
- Application destination prefix: `/app`
- User destination prefix: `/user`

### Client Send (publish)

- `/app/chat`: send chat message payload (`ChatMessage`)
- `/app/user.connectUser`: notify backend that a user connected (`User`)
- `/app/user.disconnectUser`: notify backend that a user disconnected (`User`)

### Client Subscribe

- `/user/{userId}/queue/messages`: receive direct message events (chat messages + online/offline events)

### Example message payload sent to `/app/chat`

```json
{
  "chatSessionId": "sessionId",
  "senderId": "userAId",
  "recipientId": "userBId",
  "content": "Hello",
  "messageType": "MESSAGE"
}
```

## Notes

- User status is updated to `ONLINE`/`OFFLINE` when WebSocket connect/disconnect events are processed.
- `ChatSession.lastMessage` is updated only when message type is `MESSAGE`.
- Friendship duplicate detection is implemented in service logic (pair match in both directions).

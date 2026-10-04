Spring Boot Framework integrated with MySQL, MariaDB, OracleDB and MongoDB systems, to be deployed on Oracle VM.

Currently under development and testing.

This my friend is what they called the Polyglot Persistence:
- The project has multi-database setup for MySQL, MariaDB and OracleDB datasources, for now to handle permanent datas. We are integrating databases from different hosting websites, mostly from MariaDB and Mysql databases backends.
- MongoDB is included mainly for merging stream chats from platforms like Youtube and Twitch, and show them on one single datasource that will clean itself daily during OBS streaming sessions.
- OracleDB was been considered for this project but it was proven to be too complex to our need and that we have no plans to use that framework at the moment, the codes will be cleaned on final deployment of the project, however we will archive leftover codes related to it and would be reused for future project developments.


Please note that there is the production Spring Boot profile for the app which is not shown and included here in this repository so what you have would only be its localhost variants.

---

#### 🛠 Environment Configuration
To run test this project, open your IntellJ Idea application and configure application env variables here. You can use `.env.example` as a template.

**Security & App Settings**
| Variable | Description | Required | Default |
| :--- | :--- | :--- | :--- |
| `JWT_KEY` | Secret key used to sign and verify JWT tokens. | **Yes** | None |
| `CORS_ALLOWED_ORIGINS` | Allowed origins for frontend API requests. | No | `http://localhost:3000` |

**Polyglot Persistence (Databases)**
| Variable | Description | Required | Default |
| :--- | :--- | :--- | :--- |
| `MYSQL_URL` | Connection string for the local MySQL instance. | No | `localhost:3306` |
| `MARIADB_URL` | Connection string for the local MariaDB instance. | No | `localhost:3308` |
| `ALWAYSDATA_URL` | Remote MariaDB/MySQL connection for hosting. | **Yes** | None |
| `MONGODB_URL` | URI for the MongoDB instance (used for stream chats). | No | `localhost:27017` |
| `ORACLE_ADW_URL`<br>`ORACLE_ATP_URL` | Connection string for Oracle Autonomous Data Warehouse. | No | `localhost:1521` |

**External Integrations**
| Variable | Description | Required | Default |
| :--- | :--- | :--- | :--- |
| `TWITCH_OAUTH_TOKEN`| OAuth token for accessing Twitch API. | **Yes** | None |
| `TWITCH_CHANNEL`| Username of your Twitch Channel, have to match with the Twitch API you provided | **Yes** | None
| `YOUTUBE_API_KEY` | Google Cloud API key for YouTube Data API. | **Yes** | None |
| `YOUTUBE_LIVE_CHAT_ID`| The unique ID for the active live chat stream. | **Yes** | None |

---

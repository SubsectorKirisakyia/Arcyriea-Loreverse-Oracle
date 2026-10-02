Spring Boot Framework integrated with MySQL, MariaDB, OracleDB and MongoDB systems, to be deployed on Oracle VM.

Currently under development and testing.

This my friend is what they called the Polyglot Persistence:
- The project has multi-database setup for MySQL, MariaDB and OracleDB datasources, for now to handle permanent datas. We are integrating databases from different hosting websites, mostly from MariaDB and Mysql databases backends.
- MongoDB is included mainly for merging stream chats from platforms like Youtube and Twitch, and show them on one single datasource that will clean itself daily during OBS streaming sessions.
- OracleDB was been considered for this project but it was proven to be too complex to our need and that we have no plans to use that framework at the moment, the codes will be cleaned on final deployment of the project, however we will archive leftover codes related to it and would be reused for future project developments.


Please note that there is the production Spring Boot profile for the app which is not shown and included here in this repository so what you have would only be its localhost variants.

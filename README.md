# GitHub Tracker

GitHub Tracker is a powerful tool designed to help developers and project managers monitor and manage their GitHub repositories effectively. With GitHub Tracker, you can keep track of issues, pull requests, commits, and more, all in one convenient dashboard.

## Features

- **Pull Request Monitoring**: Keep an eye on all your open and closed pull requests across multiple repositories.

## Installation

1. Clone the repo (requires Java 21+ and Maven):
2. Run the following commands to build and install the application (see Configuration section for database location and server port configuration):

```shell
mvn install
jbang app install --name=github-tracker --force --fresh dev.cf.ai:github-tracker:0.0.1-SNAPSHOT -Dspring.datasource.url=jdbc:h2:file:~/github-tracker/.data/repo-tracker;AUTO_SERVER=TRUE
```

### Configuration

When running the application, it will create a `./.data/repo-tracker*` H2 database files to store the repository data in your running directory.

To fix the database location, set the `spring.datasource.url` when installing the app.

```shell
jbang app install --name=github-tracker --force --fresh -R="-Dspring.datasource.url=jdbc:h2:file:~/github-tracker/.data/repo-tracker;AUTO_SERVER=TRUE" -R"-Dserver.port=7171" dev.cf.ai:github-tracker:0.0.1-SNAPSHOT 
```

Also use this approach to change the `server.port` to avoid conflicts with other running applications.

## Tool and Model Usage

The `github-tracker` uses the Spring OpenAI model and GitHub MCP server to fetch and analyze repository data.

Put the following tokens into your environment in order to successfully run the application:

```shell
GITHUB_TOKEN=your_github_token
SPRING_AI_OPENAI_API_KEY=your_openai_api_key
```

## Usage

```shell
github-tracker

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-11-18T16:16:18.218Z  INFO 29751 --- [github-tracker] [           main] dev.cf.ai.gt.GithubTrackerApplication    : Starting GithubTrackerApplication v0.0.1-SNAPSHOT using Java 21.0.6 with PID 29751 (/Users/robintegg/.m2/repository/dev/cf/ai/github-tracker/0.0.1-SNAPSHOT/github-tracker-0.0.1-SNAPSHOT.jar started by robintegg in /Users/robintegg/IdeaProjects/github-tracker)
2025-11-18T16:16:18.220Z  INFO 29751 --- [github-tracker] [           main] dev.cf.ai.gt.GithubTrackerApplication    : No active profile set, falling back to 1 default profile: "default"
2025-11-18T16:16:19.165Z  INFO 29751 --- [github-tracker] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 7171 (http)
2025-11-18T16:16:19.173Z  INFO 29751 --- [github-tracker] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-11-18T16:16:19.173Z  INFO 29751 --- [github-tracker] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-11-18T16:16:19.198Z  INFO 29751 --- [github-tracker] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
...
```

Goto http://localhost:7171 to access the GitHub Tracker dashboard.

Use `ctrl+c` to stop the application.
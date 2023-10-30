# GitHub Visualization (Backend)


![Word cloud generated from Github Visualization](https://githubvisualizationdeploy-production.up.railway.app/api/repo/freeCodeCamp/freeCodeCamp/issues/wordcloud_pic/200/1000/200)

This repository hosts the backend code (the frontend is already embedded). It is a Springboot application.

Frontend code at: https://github.com/wzy1935/github_visualization_frontend



## Installation Guide

### Database

Our project uses Postgres as the database. You have to create a database manually. You can use `src/main/resources/db.sql` to create necessary tables.



### GitHub Integration

Our application allows users to use their GitHub token to crawl repository data. In order to do so, you should create an [OAuth app](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app). set the Authorization callback URL as `<APPLICATION_URL>/api/oauth/redirect`. e.g., if you are hosting it at a local machine with port 8080, then the callback URL should be `http://localhost:8080/api/oauth/redirect`. Collect your `client_secret` and `client_id` at this step.



### Environment Variables

Finally, you can start the application. Note that you should pass the following to the environment variables. It is recommended to create an `app.properties` file (not provided for this repo for security reasons) to manage them:

```
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.example.github_visualization.POJO
github_visualization.fast_crawler=true
github_visualization.chinese_tokenizer=false

spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...
github_visualization.client_secret=...
github_visualization.client_id=...
```

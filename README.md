# CMPN306-SearchEngine

Advanced Programming Techniques Project - third year CUFE students.
A search engine - named Psyche, after the Greek goddess of the soul Psyche, Cupid's wife, whose life was spent on a search for love and growth, in effect, a search for onself.

## Instructions

- Open project in IntelliJ IDEA
- Configure and build the project with Gradle
- Run the component needed via its class name

```sh
gradle run run -Dexec.mainClass=com.cmpn306.package.class
```

| Component | Class                          |
| --------- | ------------------------------ |
| Crawler   | com.cmpn306.crawler.Crawler    |
| Indexer   | com.cmpn306.indexer.Indexer    |
| Ranker    | com.cmpn306.ranker.Ranker      |
| API       | com.cmpn306.queryprocessor.API |

- Run the web interface by running `npm i` then `npm run dev` from the `CMPN306-SearchEngine\src\main\javascript` directory

## Team Members:

| Member               | ID      | Email                          |
| -------------------- | ------- | ------------------------------ |
| Ahmed Ayman Saad     | 1180475 | ahmedaymansaadelsaid@gmail.com |
| Khaled Ashraf Zohair | 1180564 | khalidzohair12@gmail.com       |
| Kareem Hossam Eldin  | 1152302 |                                |
| Ziyad Sameh Sobhy    | 1180474 | ziyad.ss@hotmail.com           |

## Workload

| Member        | Contributions                  |
| ------------- | ------------------------------ |
| Ahmed Ayman   | Ranker                         |
| Khaled Ashraf | Indexer                        |
| Kareem Hossam | Crawler                        |
| Ziyad Sameh   | Query Processor, Web Interface |

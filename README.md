# TREC Search Engine

A COMP4133 Project.

## Usage

```text
usage: java -jar trec.jar
 -h,--help             print this message
 -i,--index <arg>      build index for the given postings file
 -r,--retrieve <arg>   retrieve documents for the given query file
 -s,--search <arg>     search based on the given query
```

## Build

```bash
./gradlew build

# Output in ./build/libs/trec.jar
```

## Dependency

| Name               | Version | Description            |
|--------------------|---------|------------------------|
| Java               | 8       | Java SDK               |
| Gradle             | 7.4     | Build Tool             |
| Apache Commons CLI | 1.5     | Command Line Interface |
| Log4J              | 2.19.0  | Logging                |
| ./dat/post.txt     | NA      | Postings File          |
| ./dat/files.txt    | NA      | Document Metadata      |
| ./dat/query.txt    | NA      | Query File             |

## Examples

```bash
# Weighted query
./gradlew run --args="-r ./dat/queryTDN.txt -o ./dat/TDN1_1000.txt -p ./dat/stopwords.txt -k 1000 -e 1 -t 20"
```
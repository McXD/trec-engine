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

| Name                | Version | Description            |
|---------------------|---------|------------------------|
| Java                | 8       | Java SDK               |
| Gradle              | 7.4     | Build Tool             |
| Apache Commons CLI  | 1.5     | Command Line Interface |
| Log4J               | 2.19.0  | Logging                |
| ./dat/post.txt      | NA      | Postings File          |
| ./dat/file.txt      | NA      | Document Metadata      |
| ./dat/queryT.txt    | NA      | Query File (short)     |
| ./dat/queryTDN.txt  | NA      | Query File (long)      |
| ./dat/stopwords.txt | NA      | Stopwords  List        |
| ./eval/trec_eval    | NA      | Evaluation Tool        |
| ./eval/judgerrobust | NA      | Benchmarking File      |

## Examples

The following commands are run in the project root directory.

```bash
export REDIS_HOST=localhost
export REDIS_PORT=6379

# Build inverted file index
./gradlew run --args="-i ./dat/post.txt -t 20"

# Build document map
./gradlew run --args="-d ./dat/file.txt"

# VSM Query
./gradlew run --args="-r ./dat/queryT.txt -o ./eval/T_VSM.txt -p ./dat/stopwords.txt -k 1000 -m 0 -e 0 -t 20"
./gradlew run --args="-r ./dat/queryTDN.txt -o ./eval/TDN_VSM.txt -p ./dat/stopwords.txt -k 1000 -m 0 -e 0 -t 20"

# Weighted VSM Query
./gradlew run --args="-r ./dat/queryTDN.txt -o ./eval/TDN_W.txt -p ./dat/stopwords.txt -k 1000 -m 0 -e 1 -t 20"

# Proximity Query
./gradlew run --args="-r ./dat/queryT.txt -o ./eval/T_P.txt -p ./dat/stopwords.txt -k 1000 -m 1 -e 1 -x 15 -t 20"
```
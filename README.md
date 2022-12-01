# TREC Search Engine

## Project Structure

```text
trec-engine
|-- README.md
|-- build.gradle
|-- dat // various data (see dependencies), not version-controlled
|-- eval // evaluation results, not version-controlled
|-- gradle
|-- gradlew
|-- gradlew.bat
|-- scripts // misc scripts
|-- settings.gradle
`-- src // java source files
```

## Usage

```text
usage: java -jar trec.jar
 -d,--docmap <arg>         build document map. default: ./dat/file.txt
 -e,--expansion <arg>      query expansion method for VSM mode: 0 for
                           none, 1 for weighted, 2 for pseudo relevance
                           feedback, 3 for local association analysis, 4
                           for local correlation analysis. default: 0
 -h,--help                 print this message
 -i,--index <arg>          build index for the given postings file.
                           default: ./dat/post.txt
 -k,--top-k <arg>          top k documents to retrieve. default: 1000
 -m,--mode <arg>           query mode: 0 for VSM, 1 for Proximity.
                           default: 0
 -o,--output <arg>         output file path. default: output.txt
 -p,--stopwords <arg>      stopwords file path. default:
                           ./dat/stopwords.txt
 -r,--retrieve <arg>       retrieve documents for the given query file.
                           default: ./dat/queryT.txt
 -t,--threads <arg>        number of threads to use. default: 10
 -x,--max-distance <arg>   maximum distance between two terms in a phrase
                           query. default: 10

```

## Build

```bash
./gradlew build -x test

# Output in ./build/libs/trec.jar
```

The program can also be run without an explicit build step using the Gradle wrapper:

```bash
./gradlew run --args="..."
```

## Data and Dependencies

| Name                | Version | Description        |
|---------------------|---------|--------------------|
| Java                | 8       | Java SDK           |
| Gradle              | 7.4     | Build Tool         |
| ./dat/post.txt      | NA      | Postings File      |
| ./dat/file.txt      | NA      | Document Metadata  |
| ./dat/queryT.txt    | NA      | Query File (short) |
| ./dat/queryTDN.txt  | NA      | Query File (long)  |
| ./dat/stopwords.txt | NA      | Stopwords  List    |
| ./eval/trec_eval    | NA      | Evaluation Tool    |
| ./eval/judgerobust  | NA      | Benchmarking File  |

Since the current indexing procesure is slow, a pre-built index is made available [here](https://drive.google.com/file/d/1FNsLe3iYNzbMkhCEc-0o7tLIi-LDT4JN/view?usp=share_link). Refer to [this post](https://stackoverflow.com/questions/14497234/how-to-recover-redis-data-from-snapshotrdb-file-copied-from-another-machine) to understand how to use this file.

## Examples

The following commands are run in the project root directory. You should also have a Redis server running and set up the proper environment variables.

```bash
export REDIS_HOST=localhost
export REDIS_PORT=6379

# Build inverted file index
./gradlew run --args="-i ./dat/post.txt -t 50"

# Build document map
./gradlew run --args="-d ./dat/file.txt"

# VSM Query
./gradlew run --args="-r ./dat/queryT.txt -o ./eval/T_VSM.ret -p ./dat/stopwords.txt -k 1000 -m 0 -e 0 -t 20"
./gradlew run --args="-r ./dat/queryTDN.txt -o ./eval/TDN_VSM.ret -p ./dat/stopwords.txt -k 1000 -m 0 -e 0 -t 20"

# Weighted VSM Query
./gradlew run --args="-r ./dat/queryTDN.txt -o ./eval/TDN_W.ret -p ./dat/stopwords.txt -k 1000 -m 0 -e 1 -t 20"

# Proximity Query
./gradlew run --args="-r ./dat/queryT.txt -o ./eval/T_P.ret -p ./dat/stopwords.txt -k 1000 -m 1 -e 1 -x 15 -t 20"
```

## Evaluation

The TREC evaluation tool is available at https://github.com/usnistgov/trec_eval. You should install it to the `./eval` folder.

```bash
# Generate evaluation results for all queries
for file in ./eval/*.ret; do
    ./eval/trec_eval ./eval/judgerobust $file > "${file%.ret}".eval
done

# Generate diagrams for the evaluation results
python3 ./scripts/plot.py -i `ls -1 ./eval/*.eval | tr '\n' ','` -o ./eval/PR.png,./eval/P.png
```

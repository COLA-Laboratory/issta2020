# DeepSQLi: Deep Semantic Learning for Testing SQL Injection
DeepSQLi is a deep natural language processing based tool. 
This repository includes the test cases generate module and other dependencies required for reproduce the experiment.


## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### 1. Build the SUT

#### Prerequisites
* Tomcat 5
* Java 1.4
* MySQL 5

#### Useful Setup Information
(1) Each application is distributed as a WAR file in `/SUT/*.war`;

(2) Application's database is in WAR files. You can use the DB initialization script after loading SUT.

(2) `/SUT/Instrument` is used to output SQL statements in the SUT. Its specific execution steps are in `/SUT/Instrument/README.md`.

### 2. Install the crawler
DeepSQLi uses a crawler to automatically parse the Web links of the SUT.
We use Burp Suite (Professional Version) in the experiment.

#### Useful Setup Information
(1) Download [Burp Suite Pro](https://portswigger.net/burp) from the official website;

(2) We first need to set the log path such as `\Demo\demo.log` in order to use the log file obtained by the crawler in the next step;

(3) Keep the browser agent consistent with the Burp Suite and start scanning the SUT.

### 3. Install the Evaluation Module
In order to ensure the accuracy. DeepSQLi uses a powerful tool `SQL Parser` to determine whether or not a SQL statement is malicious.

#### Useful Setup Information
(1) Download [SQL Parser](http://www.sqlparser.com/) from the official website;

(2) Package it and record the path such as `\Demo\demo.jar`.

### 4. Configure the test case module to begin testing
#### Prerequisites
* python 3.4+
* pytorch 1.3.1
* torchtext 0.4.0
* spacy 2.2.2+
* tqdm
* dill
* numpy
* click
* jpype

#### Useful Setup Information

```
python main.py -t <targetDomain> -l <logPath> -i <jarPath>
```
* \<targetDomain> is the target domain of SUT, such as`localhost/empldir`

* \<logPath> is the log path of SUT, such as`/demo/demo.log`

* \<jarPath> is the package path of SQL Parser, such as`/demo/demo.jar`

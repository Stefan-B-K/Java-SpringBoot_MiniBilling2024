# PostgreSQL
PostgreSQL е релационна база данни с отворен код. Поддържа множество типове данни (включително потребителски дефинирани), трансакции, криптиране, регулярни изрази и др.
Тук може да се види документацията за последната версия на PostgreSQL както и на по-старите: https://www.postgresql.org/docs/current/index.html.
Съветвам Ви да не се хвърляте на най-новата, а да ползвате 15-та версия. 

## PostgreSQL инсталиране
Може да си го инсталирате като приложение от тук https://www.postgresql.org/download/ (версия - 15 - ще Ви свърши работа), може и от тук https://www.enterprisedb.com/downloads/postgres-postgresql-downloads
При инсталирането е важно  locale на PostgreSQL да бъде **C**.

Ако имате Docker Desktop, може да вдигнете контейнер. Може да използвате и този docker-compose.yml (той е показан за версия 15.3 на PostgreSQL):

``` yaml
version: '3.3'
services:

  postgresql:
    platform: linux/amd64
    image: postgres:15.3
    container_name: postgres15
    restart: on-failure
    environment:
      POSTGRES_USER: tad
      POSTGRES_PASSWORD: tad
    ports:
      - "5432:5432"
```
Съответно може да изпълните `docker-compose up`.

## Необходими стъпки за конфигурация
 - Сваляне на DBeaver https://dbeaver.io/
 - Добавяне на път в Environment variable за PostgreSQL
 - Създаване на кънекция към базата
 - Създаване на нова база данни
 - Set as default на базата данни
 - Създаване на таблици

## Примери за работа с бази данни
````
CREATE TABLE table_name (
column1 serial NOT NULL,
column2 varchar(20),
column3 varchar(50),
column4 timestamptz,
column5_reference_to_table2 numeric,
CONSTRAINT column1_pkey PRIMARY KEY (column1),
CONSTRAINT fk FOREIGN KEY (column5_reference_to_table2) REFERENCES table2(column)
);
````

````
-primary key:
ALTER TABLE table_name ADD PRIMARY KEY (column_2);
````
````
-add column:
ALTER TABLE table_name add COLUMN column_name data_type;
````

````
-select statement:
SELECT * FROM table_name;
````

````
-insert one record:
INSERT INTO table_name (column1, column2)
VALUES (value1, value2);
````

````
-insert multiple records:
INSERT INTO table_name (column1, column2)
VALUES (value1, value2),
(value3, value4);
````

````
-delete one record:
DELETE FROM table_name WHERE column_name = value;
````

````
-delete all records:
DELETE FROM table_name;
````

````
-delete table:
DROP TABLE table_name;
````

````
-update statement;
UPDATE table_name
SET column_name = value
WHERE column1 = value;
````

```
INT - цяло число
NUMERIC - дробно число
VARCHAR(100) - string
DATE - само дата
TIMESTAMPTZ - дата, число, зона
```
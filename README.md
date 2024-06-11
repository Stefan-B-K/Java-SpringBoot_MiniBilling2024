Mini Billing (Project 3/3 of Methodia 2024 Java 11 Workshop/Self-training)
============
## 1. Приложение с четене / записване на локални файлове (csv, json)

## Вход
Ще разполагаме с потребители, отчети (например за газ) и цени.
Ще получаваме трите типа данни в отделни .csv файлове - users.csv, readings.csv и prices-{номер на листа}.csv. 
Възможно е да има много ценови листи. Всеки потребител ще бъде тарифиран според неговата ценова листа.
Програмата ще се стартира като подаваме първо година и месец, до който искаме да фактурираме,
след това ще подадем директорията, където се намират файловете за фактуриране - 
потребителите, отчетите и ценовите листи.
Например: `java MiniBilling2024 24-03 C:\inputdir\ C:\outputdir\` Това значи, че искаме да фактурираме за март месец на 2024-та, 
а папката somefolder е примерна папка, която трябва да съдържа файловете с информацията
(users.csv, readings.csv и неопределен брой файлове за ценовите листи prices-1,2... n.csv мачват патърна `prices-\d+.csv`). 
**Като последен параметър ще приема изходната директория**

Структурата за всеки файл e:

**Потребители**

- Име (текстов низ)
- Референтен/абонатен номер (текстов низ)
- Номер на ценова листа (цяло число)

**Отчети**

Отчетите във файла следват хронологичен ред - от стари към нови.
- Референтен номер (текстов низ)
- Продукт
- Дата (формат ISO-8601 yyyy-MM-dd'T'HH:mm:ssz)
- Показание (число с плаваща запетая)

`Референтният номер е този на клиента.`

**Цени**

- Продукт (текстов низ)
- Начална дата (формат ISO-8601 yyyy-MM-dd)
- Крайна дата (формат ISO-8601 yyyy-MM-dd)
- Цена (число с плаваща запетая)

`Стойността за "Продукт" може да бъде gas или elec. Датите са включителни и са валидни от 00:00:00 часа на стартовата дата
до 23:59:59 на крайната дата в зона Europe/Sofia.`

Файловете са кодирани в UTF-8.

## Изход

На база на тази информация, трябва за всеки потребител да издадем фактура, която ще е просто един текстов файл във JSON формат.
Фактурите за всеки потребител трябва да бъдат в отделни папки.


### Точност на изчисленията
#### Числа
При междинни калкулации на даден компонент, точността трябва да е до 3-ти знак,
в останалите случаи до 2-ри знак. Закръгляме нагоре.
#### Дати и часове
Датите и часовете са включителни. Когато има и час, точността е до секунда.

### Имена на папки и файлове
Форматът на името за папката е: {Име на потребител}-{референтен номер}.

Форматът на името за текстовия файл, за фактурата е: {пореден-номер}-{месец}-{година}.json

Където {пореден-номер} е поредният номер на фактурата, {месец} е името на месеца (**на български**), за който
е издадена фактурата, а {година} e годината във формат yy, тоест две цифри, например 22.

**Номерата на издадените фактури между клиентите не трябва да се дублират.**

### Модел на фактурата в JSON формат
Съдържанието на фактурата трябва да бъде представено в JSON формат, който е следния:

``` json
{
  "documentDate": {string - date in format ISO-8601 yyyy-MM-dd'T'HH:mm:ss'Z'},
  "documentNumber": {string},
  "consumer": {string},
  "reference": {string},
  "totalAmount": {number},
  "lines": [{
    "index": {number},
    "quantity": {number},
    "lineStart": {string - date in format ISO-8601 yyyy-MM-dd'T'HH:mm:ss'Z'},
    "lineEnd": {string - date in format ISO-8601 yyyy-MM-dd'T'HH:mm:ss'Z'},
    "product": {string},
    "price": {number},
    "priceList": {number},
    "amount": {number},
  },...]
}
```

където
- documentDate - дата на издаване на документа
- documentNumber - номер на документа/фактурата (същия номер, както и на файла), започва от 10000
- consumer - име на потребителя
- reference - абонатен номер
- totalAmount - пълната дължима стойност по фактурата
- lines - съдържа компонентите на фактурата (първоначално ще е само един)
- lines.index - пореден номер на линията
- lines.quantity - изразходено количество
- lines.lineStart - стартова дата за количеството (датата на първия отчет)
- lines.lineEnd - крайна дата за количеството (датата на последния отчет)
- lines.product - ключ на продукта (първоначално gas или elec)
- lines.price - цена, която сме определили
- lines.priceList - номера на ценовата листа
- lines.amount - дължимата сума по тази линия

### Кодировка на файловете
Файловете трябва да бъдат записани в UTF-8.

````diff
+ ===================================== SOLUTION ==================================== 
+ Local file storage of the invoices: resources/ invoices.json and lines.json,
+ structured the same way, as SQL tables in DB.
+ Used 'com.opencsv' and 'com.fasterxml.jackson' for file reading/writing.
+ For the different json content of the storage files and of the output users' folders,
+ Jackson's OutputViews were applied.
+ Repositories/DAOs interfaces were modeled for the storage files reading/writing - 
+ for future implementation and interchangeable use of DB.
+ Separation of invocies generation/creation and publishing (to users' folders).
+ Run with env variables: 24-03 src/main/resources src/main/resources/output
````


## 2. Приложение REST API
Ще надградим приложението в няколко посоки:
- за persistence ще използваме PostgreSQL (вместо файл)
- приложението ще се изпълнява за неопределено време като уеб сървис
- за вход/изход ще използваме HTTP контролери (файлове за цените)


## HTTP ендпойнти
Първо е записана HTTP операцията, която ендпойнтът трябва да поддържа, после е самият ендпойнт.
Обикновено POST операциите съдържат body с данните, които трябва да бъдат приети.
Изберете какъвто желаете формат. Може да използвате подобна структура, както тази от файловете.

В къдрави скоби {} e записан URL параметър, който ще се приема от контролера.
За цените няма ендпойнт, тях може да продължите да ги четете от файл.
### POST /users
За създаване на нов потребител.
### GET /users/{user_ref}
За получаване на информация за потребител
### POST /users/{user_ref}/readings
За подаване на отчет за потребител.
### GET /users/{user_ref}/readings
За получаване на отчетите на даден потребител.
### POST /billing
Ще стартира процес по генериране на фактурите за всички потребители.
### GET /users/{user_ref}/invoices
Ще връща фактурите на даден потребител.
### GET /users/{user_ref}/live
Ще връща текущата сметка (live billing), която все още не е фактурирана.

## Postman/cURL
За да изпращате заявки, може да си свалите [Postman](https://www.postman.com/downloads/),
a cURL е команден инструмент, който също може да използвате.
(Би трябва да е наличен под Windows и през PowerShell да можете да го извикате).

## Модел на базата
Водете се от HTTP ендпойнтите. Имате свободата да определите какъв ще бъде моделът.

## Пропорционално разпределение на количествата
Когато за отчетния период има повече от една цена, трябва да се **дистрибутира пропорционално количеството** на база на подпериодите, на които цените разделят дадения отчетен период.
При разпределението на количеството може да се натрупа **грешка от закръгляне**, затова за последният подпериод ще бъде изчислен като разлика на общото потребление и сумата от разпределеното количество за предишните подпериоди (без последния период).
Цените са валидни до края на последния ден от посочения период.

````diff
+ ===================================== SOLUTION ==================================== 
+ SpringBoot JPA for ORM and Postgre deployed locally using Docker.
+ Centralized DataService for all five repositories.
+ BillingService with separate LinesGenerator and TimeSpanCalculator.
+ Multiple custom Jackson serializers/deserializers to keep the app backwords compatible
+ with json file storage and because of the multiple input date formats.
+ Custom error handling with the validation of the POST requests.
+ Swagger for a completness of the REST API.
+ The app works in both modes - as a local json invoices generator and as Web service.
````
https://java-springboot-minibilling2024.istef.space/
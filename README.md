Mini Billing
============

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



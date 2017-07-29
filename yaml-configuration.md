# YAML Configuration

Besides [Java API](java-api.md) Ranger also supports configuring generators through YAML Configuration. Configuration can be easily parsed into `ObjectGenerator` and after that can be further built upon with [Java API](java-api.md) or can be used directly. Following code block shows how configuration can be loaded from Java.

```java
InputStream inputStream = ...;
String jsonPath = "$.parent";
Map<String, Object> config = (Map<String, Object>) YamlUtils.load(inputStream, jsonPath);
ObjectGenerator<Map<String, Object>> generator = new ConfigurationParser(config).build();
```

This will construct `ObjectGenerator` from input source and use `parent` property as root.
You can use `YamlUtils` or any other way to parse YAML file and select desired node as root. Currently, `YamlUtils` only parses JSON path in format `$.a.b.c`, so no support for anything else. Structure of YAML must be following:

```yaml
firstLevel:
  secondLevel:
    configRoot:
      values:
        firstName: random(['Stephen', "Richard", 'Arnold'])
        addr:
          city: random(["New York", "London"])
          street: 2nd St
          houseNumber: random(1..20)
        user:
          name: $firstName
          address: $addr
      output: $user
```

`configRoot` is in this case root element. Configuration must have two elements below it. `values` where all the values are defined, and `output` which will be return value for constructed `ObjectGenerator`. Any other element below root element will be ignored.

# Value definition

Value can be defined as you would normally in YAML file.

```yaml
values:
  name: Patrick
```

Value can be either [primitive](#value-primitives), [reference](#value-references) or [expression](#value-expressions).

## Value primitives

Value can be of any primitive type (boolean, byte, short, integer, long, float, double, string and date). Following section depicts type usage.

```yaml
values:
  booleanTrueVal1: true
  booleanTrueVal2: True
  booleanFalseVal1: false
  booleanFalseVal2: False
  byteVal: byte(23)
  shortVal: short(-832)
  implicitIntegerVal: 3242
  explicitIntegerVal: int(3221)
  implicitLongVal: 332848429842932
  explicitLongVal: long(323)
  explicitFloatVal: float(-88.64)
  implicitDoubleVal: 32.23
  explicitDoubleVal: double(-0.11)
  stringVal1: some text
  stringVal2: 'some text'
  stringVal3: "some text"
  dateVal: 2017-21-06
```

## Value references

Defined value can be referenced at other places using `'$'` sign, anywhere you can define value you can also use value reference.
Value reference honor local scope and can be referenced with `'.'` dereference operator.

```yaml
values:
  randomNames: random(["Peter", "Patrick", "Nick"])
  a:
    b:
      c: random(1..10)
  text:
    firstLine: "Global first line"
  user:
    text:
      firstLine: "User first line"
    innerUser:
      firstName: $randomNames
      num: $a.b.c
      text: $text.firstLine

output: $user.innerUser
```
`text` field would in this case have 'User first line' value due to local scope and shading.


## Value expressions

Value can be expression.

```yaml
values:
  age: random(7..77)
output: $age
```

# Expressions

In YAML configuration there are analogous expressions for every helper method defined in [Java API](java-api.md).

## Random

Has two meanings depending on the arguments.

### Random with discrete values

Generates random value from list of possible values. Has optional `distribution` which can be set.
Default `distribution` is `UniformDistribution`. Elements of the list can be of any type and it does not need to be same type for all the elements, although it is probably rare use cases that different types within the list will be needed.
There are two parameter variations:

```yaml
values:
  name: random(["Mike", "Peter", "Adam", "Mathew"])
  name: random(["Mike", "Peter", "Adam", "Mathew"], uniform())
output: $name
```

Any variation would create `ObjectGenerator` which can generate possible sequence:
```
"Peter", "Peter", "Mathew", "Adam", "Mathew", "Peter", "Mike", "Mike", "Adam", ...
```

### Random with range

Generates random value within specified range. Has optional `useEdgeCases` and `distribution` which can be set.
Default value for `useEdgeCases` is `false` and default `distribution` is `UniformDistribution`.
There are several parameter variations:

```yaml
values:
  age: random(1..100)
  age: random(1..100, false)
  age: random(int(1)..int(100), false, uniform())
output: $age
```

Any variation would create `ObjectGenerator` which can generate possible sequence:
```
1, 36, 17, 87, 43, 55, 91, 83, 2, 21, 76
```

If `useEdgeCases` is set to true, first value from sequence will be one set for beginning, and last value would be one before end value since range is inclusive only at beginning [a, b).

```yaml
values:
  age: random(short(1)..short(100), true)
output: $age
```

This code would generate sequence that always has first two elements 1 and 99 as those are the edge cases. After that, any random value would be picked.

## Distributions

Currently only two distributions are supported: [Uniform](#uniform-distribution) and [Normal](#normal-distribution) distribution.

### Uniform distribution

Uniform distribution can be simply used by stating `uniform()`.

```yaml
values:
  age: random([1, 5, 17, 18, 20], uniform())
output: $age
```
### Normal distribution

Normal distribution can be used in two ways.
`normal()` where default values are `mean=0.5`, `standardDeviation=0.125`, `lowerBound=0`, `upperBound=1`.
And `normal(mean, standardDeviation, lowerBound, upperBound)`.

```yaml
values:
  age:
    age1: random(byte(1)..byte(100), true, normal())
    age2: random(double(1)..double(100), false, normal(0, 1, -4, 4))
output: $age
```

## Circular

Has two meanings depending on the arguments.

### Circular with discrete values

Generates values in the order they are specified until the end. Then starts again from beginning.

```yaml
values:
  serverIpAddress: circular(["10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4"])
output: $serverIpAddress
```

This would create `ObjectGenerator` which will generate following sequence:
```
"10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", ...
```

### Circular with range

Generates values from the beginning of the range to the end using step as increment. When end is reached, values are generated again from the beginning.
Currently supports long and double ranges.

```yaml
values:
  temperature: circular(float(12.0)..float(25.0), float(0.2))
  dayOfYear: circular(1..365, 1)
```

First generator would generate following sequence:
```
12.0, 12.2, 12.4, 12.6, 12.8, 13.0, ..., 24.6, 24.8, 25.0, 12.0, 12.2, ...
```

And second would generate:
```
1, 2, 3, 4, 5, 6, ..., 363, 364, 365, 1, 2, 3, ...
```

## List

Generates list out of specified values.

```yaml
values:
  names: list(["Ema", circular(["Mike", "Steve", "John"]), "Ned", circular(["Jessica", "Lisa"])])
```

This would create `ObjectGenerator` which will generate following sequence:
```
["Ema", "Mike", "Ned", "Jessica"]
["Ema", "Steve", "Ned", "Lisa"]
["Ema", "John", "Ned", "Jessica"]
["Ema", "Mike", "Ned", "Lisa"]
.
.
.
```

## Weighted distribution

Generates values with probability based on their weights.

```yaml
values:
  names: weighted(("Stephen", 11.5), ("George", 50), ("Charles", 38.5))
```

This would create `ObjectGenerator` which can generate possible sequence:

```
"Stephen", "George", "Charles", "George", "Charles", "George", "George", "Stepen", "Charles", ...
```

Where probability for name "George" is 50%, for "Charles" 38.5% and for "Stephen" 11.5%. However, weights do not need to sum up to 100, this example has it just for purpose of calculating the probability easily.

## Exact weighted distribution

Having weighted distribution is great, at least for some use cases. But there are times where you will need to be precise, you cannot have with weighted distribution, especially when working with small numbers (< 1 000 000). Exact weighted distribution gives you precision, at the cost of limited number of objects.

```yaml
values:
  names: exactly(("Stephen", 11), ("George", 50), ("Charles", 39))
output: $names
```

Values will be generated by probability specified by weight. Weight in this case needs to be of long type.
If 100 elements are generated in this case, "George" would be generated exactly 50, "Charles" 39 and "Stephen" 11 times.
If generation of more than 100 elements is attempted, exception will be thrown.
If less than 100 elements are generated, they will follow weighted distribution.
In order to provide precision, exact weighted distribution discards particular value from possible generation if value reached its quota. That is the reason that there is a limitation to number of generated values.

## UUID

Generates UUID strings.

```yaml
values:
  id: uuid()
output: $id
```

Possible sequence is:
```
"27dbc38f-cadf-4d42-b18a-44c839e8b8f1", "575fb812-bb98-4f76-b31b-bf42e3ac2d62", "a7e229f3-875d-4a6a-9a5d-fb0670c3afdf", ...
```

## Random content string

Generates random string of specified length with optional character ranges. If ranges not specified, string will contain only characters from following ranges: `'A'-'Z'`, `'a'-'z'` and `'0'-'9'`. Length can be specified as a number, but also as an expression which can evaluate to different number each time. Uniform distribution is used to select characters from character ranges.

There are two parameter variations:

```yaml
values:
  randomString1: randomContentString(5)
output: $randomString1
```
```yaml
values:
  randomString2: randomContentString(8, ['A'..'F', '0'..'9'])
output: $randomString2
```
```yaml
values:
  randomString3: randomContentString(random(5..10), ['A'..'Z'])
output: $randomString3
```

`randomString1` will generate strings of length 5 with characters from ranges: `'A'-'Z'`, `'a'-'z'` and `'0'-'9'`.
```
"Ldsfa", "3Jdf0", "AOSyu", "qr4Qe", "sf23c", "sdFfi", "320fS", ...
```

`randomString2` will generate strings of length 8 from specified range of characters.
```
"EF893232", "2E49D0AB", "BE129E15", "938FFC1C", "BB8A43ED", "829D1CA2", ...
```

`randomString3` will generate strings of length from 5 to 10 with characters from range: `'A'-'Z'`.
```
"SDFAD", "LJAOSDUF", "DJSKIEMNLS", "KEUXLANX", "DFSAW", "DFAEAN", ...
```

## Now functions

These functions return current time:
* `now()` returns `long` UTC time in milliseconds
* `nowDate()` returns `Date`
* `nowLocalDate()` returns `LocalDate`
* `nowLocalDateTime()` returns `LocalDateTime`

## String transformer

Creates a formatted string using the specified format string and values.

```yaml
values:
  name: random("Peter", "Stephen", "Charles")
  age: random(15.40)
  text: string("{} is {} years old.", $name, $age)
output: $text
```

Possible generated values are:
```
"Peter is 18 years old.", "Peter is 34 years old.", "Charles is 27 years old.", ...
```

## Time transformer

Transforms long, Date, LocalDate and LocalDateTime value into date format.

```yaml
values:
  date: time("yyyy-MM-dd", random(1483228800000, 1514764800000))
  date: time("yyyy-MM-dd HH:mm:ss.SSS", nowDate())
output: $date
```

Possible generated values are:
```
"2017-03-25", "2017-08-08", "2017-10-11", ...
```

```yaml
values:
  date: time("yyyy-MM-dd HH:mm:ss.SSS", nowDate())
output: $date
```

This configuration will generate string time stamps, which can be helpful in many cases.

## JSON transformer

Transforms value of complex `ObjectGenerator` into JSON.

```yaml
values:
  user:
    id: circular(1..2000000, 1)
    username: string("{}{}", random(["aragorn", "johnsnow", "mike", "batman"]), random(1..100))
    firstName: random(["Peter", "Rodger", "Michael"])
    lastName: random(["Smith", "Cooper", "Stark", "Grayson", "Atkinson", "Durant"])
    maried: false
    accountBalance: random(0.0..10000.0)
    address:
      city: random(["New York", "Washington", "San Francisco"])
      street: random(["2nd St", "5th Avenue", "21st St", "Main St"])
      houseNumber: random(1..55)
output: json($user)
```

Possible generated values are:

```
{"id":1,"username":"mike1","firstName":"Michael","lastName":"Cooper","maried":false,"accountBalance":0.0,"address":{"city":"San Francisco","street":"Main St","houseNumber":1}}

{"id":2,"username":"mike99","firstName":"Rodger","lastName":"Smith","maried":false,"accountBalance":9999.99999999999,"address":{"city":"San Francisco","street":"21st St","houseNumber":54}}

{"id":3,"username":"johnsnow35","firstName":"Michael","lastName":"Atkinson","maried":false,"accountBalance":9636.00274910154,"address":{"city":"New York","street":"Main St","houseNumber":37}}
```

# Combining API

Since output of the YAML Configuration is `ObjectGenerator`, it can be further combined within [Java API](java-api.md)
if needed. However, vice versa is not possible.
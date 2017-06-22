# Java API

Central type of Java API is `ObjectGenerator` with its two simple methods: `T next()` and `List<T> generate(int numberOfObjects)`. `T next()` evaluates next value and `List<T> generate(int numberOfObjects)` evaluates next `numberOfObjects` values and puts them into list.

To construct `ObjectGenerator` use either`ObjectGeneratorBuilder` or any method from `BuilderMethods` class.

# Object generator builder

`ObjectGeneratorBuilder` has `<V> ObjectGeneratorBuilder prop(String property, V value)` method which will construct rule to be used for specified property. Value can be any object. Depending on whether value is another `ObjectGenerator` or any other object, `prop` method will have different behavior.

If value is `ObjectGenerator`, it will be evaluated every time.
```java
ObjectGenerator<Integer> intGenerator = random(range(1, 7));
ObjectGenerator<Map<String, Object>> generator = new ObjectGeneratorBuilder().prop("dice", intGenerator).build();
```

This will result in possible value sequence: 2, 4, 1, 1, 5, 2, 6, 5, 6, 3, 1.

If value is any other object, its value will be used as is.
```java
ObjectGenerator<Map<String, Object>> generator = new ObjectGeneratorBuilder().prop("dice", 4).build();
```

This will result in generator always generating value 4.

# Builder methods

Below is given a list of helper methods for construction of `ObjectGenerator`. Methods are provided by `BuilderMethods` class.

## Random

Has two meanings depending on the arguments.

### Random with discrete values

Generates random value from list of possible values. Has optional `distribution` which can be set.
Default `distribution` is `UniformDistribution`.
There are several parameter variations:

```java
ObjectGenerator<String> names = random("Mike", "Peter", "Adam", "Mathew");

List<String> values = Arrays.asList("Mike", "Peter", "Adam", "Mathew");
ObjectGenerator<String> names = random(values);

ObjectGenerator<String> names = random(new UniformDistribution(), "a", "b", "c", "d");

List<String> values = Arrays.asList("Mike", "Peter", "Adam", "Mathew");
ObjectGenerator<String> names = random(new UniformDistribution(), values);
```

Any variation would create `ObjectGenerator` which can generate possible sequence:
```
"Peter", "Peter", "Mathew", "Adam", "Mathew", "Peter", "Mike", "Mike", "Adam", ...
```

### Random with range

Generates random value within specified range. Has optional `useEdgeCases` and `distribution` which can be set.
Default value for `useEdgeCases` is `false` and default `distribution` is `UniformDistribution`.
There are several parameter variations:

```java
ObjectGenerator<Integer> age = random(range(1, 100));

ObjectGenerator<Integer> age = random(range(1, 100), false);

ObjectGenerator<Integer> age = random(range(1, 100), false, new UniformDistribution());
```

Any variation would create `ObjectGenerator` which can generate possible sequence:
```
1, 36, 17, 87, 43, 55, 91, 83, 2, 21, 76
```

If `useEdgeCases` is set to true, first value from sequence will be one set for beginning, and last value would be one before end value since range is inclusive only at beginning [a, b).

```java
ObjectGenerator<Integer> age = random(range(1, 100), true);
```

This code would generate sequence that always has first two elements 1 and 99 as those are the edge cases. After that, any random value would be picked.

## Circular

Has two meanings depending on the arguments.

### Circular with discrete values

Generates values in the order they are specified until the end. Then starts again from beginning.
There are two parameter variations:

```java
ObjectGenerator<String> serverIpAddress = circular("10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4");

List<String> ipAddresses = Arrays.asList("10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4");
ObjectGenerator<String> serverIpAddress = circular(ipAddresses);
```

Any variation would create `ObjectGenerator` which will generate following sequence:
```
"10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", ...
```

### Circular with range

Generates values from the beginning of the range to the end using step as increment. When end is reached, values are generated again from the beginning.
Currently supports int, long and double ranges.

```java
ObjectGenerator<Double> temperature = circular(range(12d, 25d), 0.2d);

ObjectGenerator<Integer> dayOfYear = circular(range(1, 365), 1);
```

First generator would generate following sequence:
```
12.0, 12.2, 12.4, 12.6, 12.8, 13.0, ..., 24.6, 24.8, 25.0, 12.0, 12.2, ...
```

And second would generate:
```
1, 2, 3, 4, 5, 6, ..., 363, 364, 365, 1, 2, 3, ...
```

## Weighted distribution

Generates values with probability based on their weights.

```java
WeightPair<String> weightPair1 = weightPair("Stephen", 11.5d);
ObjectGenerator<String> names = weighted(weightPair1, weightPair("George", 50), weightPair("Charles", 38.5));

List<WeightPair<String>> weightPairs = Arrays.asList(weightPair1, weightPair("George", 50), weightPair("Charles", 38.5));
ObjectGenerator<String> names = weighted(weightPairs);
```

Any variation would create `ObjectGenerator` which can generate possible sequence:

```
"Stephen", "George", "Charles", "George", "Charles", "George", "George", "Stepen", "Charles", ...
```

Where probability for name "George" is 50%, for "Charles" 38.5% and for "Stephen" 11.5%. However, weights do not need to sum up to 100, this example has it just for purpose of calculating the probability easily.

## Exact weighted distribution

Having weighted distribution is great, at least for some use cases. But there are times where you will need to be precise, you cannot have with weighted distribution, especially when working with small numbers (< 1 000 000). Exact weighted distribution gives you precision, at the cost of limited number of objects.

```java
CountPair<String> countPair1 = countPair("Stephen", 11);
ObjectGenerator<String> names = exactly(countPair1, countPair("George", 50), countpair("Charles", 39));

List<CountPair<String>> countPairs = Arrays.asList(countPair1, countPair("George", 50), countpair("Charles", 39));
ObjectGenerator<String> names = exactly(countPairs);
```

Values will be generated by probability specified by weight. Weight in this case needs to be of long type.
If 100 elements are generated in this case, "George" would be generated exactly 50, "Charles" 39 and "Stephen" 11 times.
If generation of more than 100 elements is attempted, exception will be thrown.
If less than 100 elements are generated, they will follow weighted distribution.
In order to provide precision, exact weighted distribution discards particular value from possible generation if value reached its quota. That is the reason that there is a limitation to number of generated values.

## UUID

Generates UUID strings.

```java
ObjectGenerator<String> uuid = uuid();
```

Possible sequence is:
```
"27dbc38f-cadf-4d42-b18a-44c839e8b8f1", "575fb812-bb98-4f76-b31b-bf42e3ac2d62", "a7e229f3-875d-4a6a-9a5d-fb0670c3afdf", ...
```

## Random length string

Generates random string of specified length with optional character ranges. If ranges not specified, string will contain only characters from following ranges: `'A'-'Z'`, `'a'-'z'` and `'0'-'9'`.

There are several parameter variations:

```java
ObjectGenerator<String> randomString1 = randomLengthString(5);

ObjectGenerator<String> randomString2 = randomLengthString(8, range('A', 'F'), range('0', '9'));

List<Range<Character>> ranges = Arrays.asList(range('A', 'F'), range('0', '9'));
ObjectGenerator<String> randomString2 = randomLengthString(8, ranges);
```

First generator will generate string of length 5 with characters from ranges: `'A'-'Z'`, `'a'-'z'` and `'0'-'9'`.
```
"Ldsfa", "3Jdf0", "AOSyu", "qr4Qe", "sf23c", "sdFfi", "320fS", ...
```

Second generator would generate strings of length 8 from specified range of characters.
```
"EF893232", "2E49D0AB", "BE129E15", "938FFC1C", "BB8A43ED", "829D1CA2", ...
```

## Now methods

These methods return current time:
* `now()` returns `long` UTC time in milliseconds
* `nowDate()` returns `Date`
* `nowLocalDate()` returns `LocalDate`
* `nowLocalDateTime()` returns `LocalDateTime`

## String transformer

Creates a formatted string using the specified format string and values.

```java
ObjectGenerator<String> name = random("Peter", "Stephen", "Charles");
ObjectGenerator<Integer> age = random(range(15, 40));

ObjectGenerator<String> text = string("{} is {} years old.", name, age);
```

Possible generated values are:
```
"Peter is 18 years old.", "Peter is 34 years old.", "Charles is 27 years old.", ...
```

## Time transformer

Transforms long, Date, LocalDate and LocalDateTime value into date format.

```java
ObjectGenerator<Long> dateRange = random(range(1483228800000, 1514764800000));
ObjectGenrator<String> date = time("yyyy-MM-dd", dateRange);
```

Possible generated values are:
```
"2017-03-25", "2017-08-08", "2017-10-11", ...
```

```java
ObjectGenerator<String> date = time("yyyy-MM-dd HH:mm:ss.SSS", nowLocalDate());
```

This will generate string time stamps, which can be helpful in many cases.

## JSON transformer

Transforms value of complex `ObjectGenerator` into JSON.

```java
ObjectGenerator<Map<String, Object>> address = new ObjectGeneratorBuilder()
    .prop("city", random("New York", "Washington", "San Francisco"))
    .prop("street", random("2nd St", "5th Avenue", "21st St", "Main St"))
    .prop("houseNumber", random(range(1, 55))).build();

ObjectGenerator<Map<String, Object>> user = new ObjectGeneratorBuilder()
    .prop("id", circular(range(1L, 2_000_000L), 1L))
    .prop("username", string("{}{}", random("aragorn", "johnsnow", "mike", "batman"), random(range(1, 100))))
    .prop("firstName", random("Peter", "Rodger", "Michael"))
    .prop("lastName", random("Smith", "Cooper", "Stark", "Grayson", "Atkinson", "Durant"))
    .prop("maried", false)
    .prop("accountBalance", random(range(0.0d, 10_000.0d)))
    .prop("address", address).build();

ObjectGenerator<String> output = json(user);
```

Possible generated values are:

```
{"id":1,"username":"mike1","firstName":"Michael","lastName":"Cooper","maried":false,"accountBalance":0.0,"address":{"city":"San Francisco","street":"Main St","houseNumber":1}}

{"id":2,"username":"mike99","firstName":"Rodger","lastName":"Smith","maried":false,"accountBalance":9999.99999999999,"address":{"city":"San Francisco","street":"21st St","houseNumber":54}}

{"id":3,"username":"johnsnow35","firstName":"Michael","lastName":"Atkinson","maried":false,"accountBalance":9636.00274910154,"address":{"city":"New York","street":"Main St","houseNumber":37}}
```
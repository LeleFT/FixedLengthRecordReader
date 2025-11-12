FixedLengthRecordReader
=======================

Java library for reading fixed-length records.

It offers methods for getting a variety of type values from the string
representing a single data-source record and it takes care of automatically
advance the character pointer.

It can parse all of the primitives data types and offers methods for
parsing LocalDate, LocalTime and LocalDateTime types in various formats.

Here's some example codes:

```java
// Read a line of data e.g. from a text file
String record = "000321PRODUCT DESCRIPTION                     2025-11-125.23";
FixedLengthRecordReader reader = new FixedLengthRecordReader( record );

// Read the KPI as an int value using first 6 characters (expected: 321)
int id = reader.getInt( 6 );

// Read the product description as a String value using the next 40
// characters (expected: "PRODUCT DESCRIPTION")
String description = reader.getString( 40 ).trim();

// Read the purchase date as a LocalDate (expected: 2025-11-12)
LocalDate purchaseDate = reader.getExtendedISODate();

// Read the price as a double value (expected: 5.23)
double price = reader.getDouble(4);
```

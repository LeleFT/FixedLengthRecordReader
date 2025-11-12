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

// Read an int value using first 6 characters (e.g. a KPI)
int id = reader.getInt( 6 );

// Read a String value using the next 40 characters (e.g. description)
String description = reader.getString( 40 );

// Read a LocalDate (e.g. purchase date)
LocalDate purchaseDate = reader.getExtendedISODate();

// Read a double value (e.g. the price)
double price = reader.getDouble(4);
```

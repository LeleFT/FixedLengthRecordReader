package it.leleft;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * This class manages a single line of text that represents a fixed-length record.
 * It offers methods for getting a variety of type values from the string and
 * automatically advances the character pointer.
 * 
 * @author LeleFT
 */
public class FixedLengthRecordReader {
    
    /**
     * The single line of text that represents a fixed-length record
     */
    private final String buffer;
    
    /**
     * The character pointer
     */
    private int actualPos;
    
    /**
     * Constructs a {@code FixedLengthRecordReader} object, using the
     * {@code buffer} parameter as data-source.
     * 
     * @param buffer the text that represents a fixed-length record to parse
     */
    public FixedLengthRecordReader(String buffer) {
        this.buffer = buffer;
        actualPos = 0;
    }
    
    /**
     * Returns a buffer's substring of {@code length} characters starting from
     * the actual pointer position.
     * 
     * @param length the number of characters to extract from buffer's actual
     *               pointer position
     * 
     * @return       the substring extracted from buffer's actual pointer
     *               position
     * 
     * @throws FixedLengthParseException if the length of the remaining buffer
     * is less then {@code length} characters.
     */
    public String getString(int length) throws FixedLengthParseException {
        String ret = "";
        try {
            ret = buffer.substring(actualPos, actualPos + length);
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a String from ")
               .append( actualPos )
               .append(" to ")
               .append(actualPos + length)
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, actualPos);
        }
        actualPos += length;
        return ret;
    }
    
    /**
     * Returns the next buffer's character.
     * 
     * @return the next buffer's character
     * 
     * @throws FixedLengthParseException if the buffer is completely consumed.
     */
    public char getChar() throws FixedLengthParseException {
        char c = '\0';
        if (buffer.length() > actualPos) {
            c = buffer.charAt( actualPos++ );
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a char from ")
               .append( actualPos )
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), actualPos);
        }
        return c;
    }
    
    /**
     * Returns a byte from a hexadecimal representation consuming the next 2 or
     * 4 characters, depending on the {@code withPrefix} parameter.
     * 
     * <p>If the {@code withPrefix} parameter is set to {@code true}, it is
     * expected to find the value in the canonical {@code "0xXX"} form, so it
     * consumes the next 4 characters and ignores the first two; otherwise it is
     * expected to find only the hexadecimal representation of the value, so
     * only the next 2 characters are consumed and parsed.
     * 
     * <p>For example, assuming the pointer is at the beginnning of the buffer,
     * giving the following string:
     * 
     * <blockquote>
     *  {@code 0x0Dblablabla}
     * </blockquote>
     * 
     * you have to pass {@code true} to {@code withPrefix} parameter in order to
     * correctly parse the value because of the "0x" prefix.
     * 
     * @param withPrefix if {@code true} it parses the string expecting the
     *                   hexadecimal representation with {@code 0x} prefix
     * @return           the byte value of parsing the buffer as hexadecimal
     *                   string
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public byte getByte(boolean withPrefix) throws FixedLengthParseException {
        byte ret = (byte) 0;
        int oldPos = actualPos;
        try {
            String strByte = getString(withPrefix ? 4 : 2);
            if ( withPrefix ) strByte = strByte.substring(2);
            ret = (byte) ((Character.digit(strByte.charAt(0), 16) << 4)
                         + Character.digit(strByte.charAt(1), 16));
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a byte from ")
               .append( oldPos )
               .append((withPrefix ? " with " : " without "))
               .append("prefix in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, oldPos);
        }
        return ret;
    }

    /**
     * Returns an int parsing the next {@code length} characters of the buffer.
     * 
     * @param length the number of characters to parse
     * @return       the int value parsed
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public int getInt(int length) throws FixedLengthParseException {
        int val = 0;
        try {
            val = Integer.parseInt(buffer.substring(actualPos, actualPos + length), 10);
            actualPos += length;
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting an int from ")
               .append( actualPos )
               .append(" to ")
               .append(actualPos + length)
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, actualPos);
        }
        return val;
    }
    
    /**
     * Returns a long parsing the next {@code length} characters of the buffer.
     * 
     * @param length the number of characters to parse
     * @return       the long value parsed
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public long getLong(int length) throws FixedLengthParseException {
        long val = 0L;
        try {
            val = Long.parseLong(buffer.substring(actualPos, actualPos + length), 10);
            actualPos += length;
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a long from ")
               .append( actualPos )
               .append(" to ")
               .append(actualPos + length)
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, actualPos);
        }
        return val;
    }
    
    /**
     * Returns a float parsing the next {@code length} characters of the buffer
     * assuming it represents a floating point number.  It assumes the
     * representation has (optional) decimal point sign.
     * 
     * <p>For example, assuming the pointer is at the beginnning of the buffer,
     * giving the following string:
     * 
     * <blockquote>
     * {@code 005.234blablabla}
     * </blockquote>
     * 
     * the following code
     * 
     * <blockquote>
     * {@code float f = reader.getFloat(7);}
     * </blockquote>
     * 
     * assigns the variable {@code f} the float value 5.234
     * 
     * @param length       the number of characters to parse
     * @return             the float value parsed
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public float getFloat(int length) throws FixedLengthParseException {
        float val = 0.0f;
        try {
            val = Float.parseFloat( buffer.substring(actualPos, actualPos + length) );
            actualPos += length;
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a float from ")
               .append( actualPos )
               .append(" to ")
               .append(actualPos + length)
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, actualPos);
        }
        return val;
    }

    /**
     * Returns a float parsing the next {@code length} characters of the buffer
     * assuming it represents a floating point number with {@code numDecimals}
     * decimals.  It assumes the representation is without decimal point sign.
     * 
     * <p>For example, assuming the pointer is at the beginnning of the buffer,
     * giving the following string:
     * 
     * <blockquote>
     * {@code 005234blablabla}
     * </blockquote>
     * 
     * the following code
     * 
     * <blockquote>
     * {@code float f = reader.getFloat(6, 3);}
     * </blockquote>
     * 
     * assigns the variable {@code f} the float value 5.234
     * 
     * @param length       the number of characters to parse
     * @param numDecimals  the number of decimals
     * @return             the float value parsed
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public float getFloat(int length, int numDecimals) throws FixedLengthParseException {
        float val = 0.0f;
        try {
            val = Float.parseFloat( buffer.substring(actualPos, actualPos + length) );
            if (numDecimals > 0) {
                val = val / (float) Math.pow(10.0, numDecimals);
            }
            actualPos += length;
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a float from ")
               .append( actualPos )
               .append(" to ")
               .append(actualPos + length)
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, actualPos);
        }
        return val;
    }

    /**
     * Returns a double parsing the next {@code length} characters of the buffer
     * assuming it represents a floating point number.  It assumes the
     * representation has (optional) decimal point sign.
     * 
     * <p>For example, assuming the pointer is at the beginnning of the buffer,
     * giving the following string:
     * 
     * <blockquote>
     * {@code 005.234blablabla}
     * </blockquote>
     * 
     * the following code
     * 
     * <blockquote>
     * {@code double d = reader.getDouble(7);}
     * </blockquote>
     * 
     * assigns the variable {@code f} the double value 5.234
     * 
     * @param length       the number of characters to parse
     * @return             the double value parsed
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public double getDouble(int length) throws FixedLengthParseException {
        double val = 0.0d;
        try {
            val = Double.parseDouble( buffer.substring(actualPos, actualPos + length) );
            actualPos += length;
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a double from ")
               .append( actualPos )
               .append(" to ")
               .append(actualPos + length)
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, actualPos);
        }
        return val;
    }
    
    /**
     * Returns a double parsing the next {@code length} characters of the buffer
     * assuming it represents a floating point number with {@code numDecimals}
     * decimals.  It assumes the representation is without decimal point sign.
     * 
     * <p>For example, assuming the pointer is at the beginnning of the buffer,
     * giving the following string:
     * 
     * <blockquote>
     * {@code 005234blablabla}
     * </blockquote>
     * 
     * the following code
     * 
     * <blockquote>
     * {@code double d = reader.getDouble(6, 3);}
     * </blockquote>
     * 
     * assigns the variable {@code f} the double value 5.234
     * 
     * @param length       the number of characters to parse
     * @param numDecimals  the number of decimals
     * @return             the double value parsed
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public double getDouble(int length, int numDecimals) throws FixedLengthParseException {
        double val = 0.0d;
        try {
            val = Double.parseDouble( buffer.substring(actualPos, actualPos + length) );
            if (numDecimals > 0) {
                val = val / Math.pow(10.0, numDecimals);
            }
            actualPos += length;
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error getting a double from ")
               .append( actualPos )
               .append(" to ")
               .append(actualPos + length)
               .append(" in string [")
               .append( buffer )
               .append("]");
            throw new FixedLengthParseException(msg.toString(), e, actualPos);
        }
        return val;
    }

    /**
     * Returns a {@code LocalDate} object parsing the next 8 characters in the
     * buffer. It assumes the date is in ISO-8601 basic format (yyyyMMdd).
     * 
     * @return the {@code LocalDate} object from parsing next 8 characters.
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public LocalDate getBasicISODate() throws FixedLengthParseException {
        LocalDate ret = null;
        
        int oldPos = actualPos;
        String strData = getString(8);
        if ((strData != null) && !"".equals(strData.trim()) && !"00000000".equals(strData)) {
            StringBuilder sbData = new StringBuilder( strData );
            sbData.insert(6, '-');
            sbData.insert(4, '-');
            try {
                ret = LocalDate.parse( sbData.toString() );
            } catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error getting a LocalDate from ")
                   .append( oldPos )
                   .append(" to ")
                   .append(oldPos + 8)
                   .append(" in string [")
                   .append( buffer )
                   .append("]");
                throw new FixedLengthParseException(msg.toString(), e, oldPos);
            }
        }
        return ret;
    }

    /**
     * Returns a {@code LocalDate} object parsing the next 10 characters in the
     * buffer. It assumes the date is in ISO-8601 extended format (yyyy-MM-dd).
     * 
     * @return the {@code LocalDate} object from parsing next 10 characters.
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public LocalDate getExtendedISODate() throws FixedLengthParseException {
        LocalDate ret = null;
        
        int oldPos = actualPos;
        String strData = getString(10);
        if ((strData != null) && !"".equals(strData.trim()) && !"00000000".equals(strData)) {
            try {
                ret = LocalDate.parse( strData );
            } catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error getting a LocalDate from ")
                   .append( oldPos )
                   .append(" to ")
                   .append(oldPos + 10)
                   .append(" in string [")
                   .append( buffer )
                   .append("]");
                throw new FixedLengthParseException(msg.toString(), e, oldPos);
            }
        }
        return ret;
    }

    /**
     * Returns a {@code LocalTime} object parsing the next 8 characters in the
     * buffer. It assumes the time is in ISO-8601 extended format (HH:mm:ss).
     * 
     * @return the {@code LocalTime} object from parsing next 8 characters.
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public LocalTime getExtendedISOTime() throws FixedLengthParseException {
        LocalTime ret = null;
        
        int oldPos = actualPos;
        String strTime = getString(8);
        if ((strTime != null) && !"".equals(strTime.trim()) && !"00000000".equals(strTime)) {
            try {
                ret = LocalTime.parse( strTime );
            } catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error getting a LocalTime from ")
                   .append( oldPos )
                   .append(" to ")
                   .append(oldPos + 8)
                   .append(" in string [")
                   .append( buffer )
                   .append("]");
                throw new FixedLengthParseException(msg.toString(), e, oldPos);
            }
        }
        return ret;
    }

    /**
     * Returns a {@code LocalTime} object parsing the next 6 characters in the
     * buffer. It assumes the time is in ISO-8601 basic format (HHmmss).
     * 
     * @return the {@code LocalTime} object from parsing next 8 characters.
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public LocalTime getBasicISOTime() throws FixedLengthParseException {
        LocalTime ret = null;
        
        int oldPos = actualPos;
        String strTime = getString(6);
        if ((strTime != null) && !"".equals(strTime.trim()) && !"00000000".equals(strTime)) {
            StringBuilder sbTime = new StringBuilder( strTime );
            sbTime.insert(4, ':');
            sbTime.insert(2, ':');
            try {
                ret = LocalTime.parse( sbTime.toString() );
            } catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error getting a LocalTime from ")
                   .append( oldPos )
                   .append(" to ")
                   .append(oldPos + 6)
                   .append(" in string [")
                   .append( buffer )
                   .append("]");
                throw new FixedLengthParseException(msg.toString(), e, oldPos);
            }
        }
        return ret;
    }

    /**
     * Returns a {@code LocalDateTime} object parsing the next 19 characters in
     * the buffer. It assumes the datetime is in ISO-8601 extendend format
     * (yyyy-MM-ddTHH:mm:ss) unless {@code timeOffset} is set to
     * {@code true} so it parse the next 25 characters, assuming the datetime
     * has the time-offset part in the format (yyyy-MM-ddTHH:mm:ss&plusmn;HH:mm).
     * 
     * <p>For example, assuming the pointer is at the beginnning of the buffer,
     * giving the following string:
     * 
     * <blockquote>
     * {@code 2025-07-25T09:11:23+02:00blablabla}
     * </blockquote>
     * 
     * the following code
     * 
     * <blockquote>
     * {@code LocalDateTime d1 = reader.getExtendedISODateTime(false);}
     * {@code LocalDateTime d2 = reader.getExtendedISODateTime(true);}
     * </blockquote>
     * 
     * produces 
     * 
     * <ul>
     *    <li>
     *       {@code d1} a LocalDateTime object that represents the 25th of July
     *       2025, 09:11:23 UTC</li>
     *    <li>
     *       {@code d2} a LocalDateTime object that represents the 25th of July
     *       2025, 11:11:23 UTC</li>
     * </ul>
     * 
     * @param timeOffset if {@code true} considers a string of 25 characters to
     *                   include timezone information
     * @return the {@code LocalDateTime} object from parsing next 19 or 25
     *         characters.
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public LocalDateTime getExtendedISODateTime(boolean timeOffset) throws FixedLengthParseException {
        LocalDateTime ret = null;
        
        int oldPos = actualPos;
        int newPos = actualPos + (timeOffset ? 25 : 19);
        String strDateTime = getString(timeOffset ? 25 : 19);
        if ((strDateTime != null) && !"".equals(strDateTime.trim()) && !"00000000".equals(strDateTime)) {
            try {
                ret = LocalDateTime.parse(strDateTime,
                        timeOffset ? 
                                DateTimeFormatter.ISO_OFFSET_DATE_TIME :
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error getting a LocalDateTime from ")
                   .append( oldPos )
                   .append(" to ")
                   .append( newPos )
                   .append(" in string [")
                   .append( buffer )
                   .append("]");
                throw new FixedLengthParseException(msg.toString(), e, oldPos);
            }
        }
        return ret;
    }

    /**
     * Returns a {@code LocalDateTime} object parsing the next 15 characters in
     * the buffer. It assumes the datetime is in ISO-8601 extendend format
     * (yyyyMMddTHHmmss) unless {@code timeOffset} is set to {@code true} so it
     * parse the next 20 characters, assuming the datetime has the time-offset
     * part in the format (yyyyMMddTHHmmss&plusmn;HHmm).
     * 
     * <p>For example, assuming the pointer is at the beginnning of the buffer,
     * giving the following string:
     * 
     * <blockquote>
     * {@code 20250725T091123+0200blablabla}
     * </blockquote>
     * 
     * the following code
     * 
     * <blockquote>
     * {@code LocalDateTime d1 = reader.getBasicISODateTime(false);}
     * {@code LocalDateTime d2 = reader.getBasicISODateTime(true);}
     * </blockquote>
     * 
     * produces 
     * 
     * <ul>
     *    <li>
     *       {@code d1} a LocalDateTime object that represents the 25th of July
     *       2025, 09:11:23 UTC</li>
     *    <li>
     *       {@code d2} a LocalDateTime object that represents the 25th of July
     *       2025, 11:11:23 UTC</li>
     * </ul>
     * 
     * @param timeOffset if {@code true} considers a string of 20 characters to
     *                   include timezone information
     * @return the {@code LocalDateTime} object from parsing next 15 or 20
     *         characters.
     * @throws FixedLengthParseException if an error occurred during parsing
     */
    public LocalDateTime getBasicISODateTime(boolean timeOffset) throws FixedLengthParseException {
        LocalDateTime ret = null;
        
        int oldPos = actualPos;
        int newPos = actualPos + (timeOffset ? 20 : 15);
        String strDateTime = getString(timeOffset ? 20 : 15);
        if ((strDateTime != null) && !"".equals(strDateTime.trim()) && !"00000000".equals(strDateTime)
            ) {
            StringBuilder sbDateTime = new StringBuilder( strDateTime );
            sbDateTime.insert(18, ':');
            sbDateTime.insert(13, ':');
            sbDateTime.insert(11, ':');
            sbDateTime.insert(6, '-');
            sbDateTime.insert(4, '-');
            try {
                ret = LocalDateTime.parse(sbDateTime.toString(),
                        timeOffset ?
                                DateTimeFormatter.ISO_OFFSET_DATE_TIME :
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error getting a LocalDateTime from ")
                   .append( oldPos )
                   .append(" to ")
                   .append( newPos )
                   .append(" in string [")
                   .append( buffer )
                   .append("]");
                throw new FixedLengthParseException(msg.toString(), e, oldPos);
            }
        }
        return ret;
    }
}

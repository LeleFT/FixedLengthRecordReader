package it.leleft;

/**
 * This class represents an exception during the parse process of a fixed-length
 * record.
 * 
 * @author LeleFT
 */
public class FixedLengthParseException extends Exception {
    
    /**
     * The offset of the error in the fixed-length record string
     */
    private final int offset;
    
    /**
     * Constructs a <code>FixedLengthParseException</code> object with a message
     * and the offset where the error was found.
     * 
     * @param message the detail message of the exception
     * @param offset  the offset where the error was found
     */
    public FixedLengthParseException(String message, int offset) {
        super( message );
        this.offset = offset;
    }

    /**
     * Constructs a <code>FixedLengthParseException</code> object with a message,
     * the cause and the offset where the error was found.
     * 
     * @param message the detail message of the exception
     * @param cause the original cause of this exception
     * @param offset the offset where the error was found
     */
    public FixedLengthParseException(String message, Throwable cause, int offset) {
        super(message, cause);
        this.offset = offset;
    }
    
    /**
     * Returns the offset where the error was found.
     * 
     * @return the offset where the error was found
     */
    public int getOffset() {
        return offset;
    }
}

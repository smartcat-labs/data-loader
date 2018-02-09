package io.smartcat.ranger.core.csv;

/**
 * Settings available for CSV parser.
 */
public class CSVParserSettings {

    private final String path;
    private final char delimiter;
    private final String recordSeparator;
    private final boolean trim;
    private final Character quote;
    private final char commentMarker;
    private final boolean ignoreEmptyLines;
    private final String nullString;

    /**
     * Creates settings with specified path to the CSV file. Default values for other parameters:
     * <ul>
     * <li><code>delimiter</code> - <code>','</code></li>
     * <li><code>recordSeparator</code> - <code>"\n"</code></li>
     * <li><code>trim</code> - <code>true</code></li>
     * <li><code>quote</code> - <code>null</code> (disabled)</li>
     * <li><code>commentMarker</code> - <code>'#'</code></li>
     * <li><code>ignoreEmptyLines</code> - <code>true</code></li>
     * <li><code>nullString</code> - <code>null</code> (disabled)</li>
     * </ul>
     *
     * @param path Path to the CSV file.
     */
    public CSVParserSettings(String path) {
        this(path, ',');
    }

    /**
     * Creates settings with specified path to the CSV file and delimiter. Default values for other parameters:
     * <ul>
     * <li><code>recordSeparator</code> - <code>"\n"</code></li>
     * <li><code>trim</code> - <code>true</code></li>
     * <li><code>quote</code> - <code>null</code> (disabled)</li>
     * <li><code>commentMarker</code> - <code>'#'</code></li>
     * <li><code>ignoreEmptyLines</code> - <code>true</code></li>
     * <li><code>nullString</code> - <code>null</code> (disabled)</li>
     * </ul>
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     */
    public CSVParserSettings(String path, char delimiter) {
        this(path, delimiter, "\n", true, null, '#', true, null);
    }

    /**
     * Creates settings with specified parameters.
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param recordSeparator Delimiter of records within CSV file.
     * @param trim True if each column value is to be trimmed for leading and trailing whitespace, otherwise
     *            <code>false</code>.
     * @param quote Character that will be stripped from beginning and end of each column if present. If set to
     *            <code>null</code>, no characters will be stripped (nothing will be used as quote character).
     * @param commentMarker Character to use as a comment marker, everything after it is considered comment.
     * @param ignoreEmptyLines True if empty lines are to be ignored, otherwise <code>false</code>.
     * @param nullString Converts string with given value to <code>null</code>. If set to <code>null</code>, no
     *            conversion will be done.
     */
    public CSVParserSettings(String path, char delimiter, String recordSeparator, boolean trim, Character quote,
            char commentMarker, boolean ignoreEmptyLines, String nullString) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path cannot be null nor empty");
        }
        if (recordSeparator == null || recordSeparator.isEmpty()) {
            throw new IllegalArgumentException("recordSeperator cannot be null nor empty");
        }
        this.path = path;
        this.delimiter = delimiter;
        this.recordSeparator = recordSeparator;
        this.trim = trim;
        this.quote = quote;
        this.commentMarker = commentMarker;
        this.ignoreEmptyLines = ignoreEmptyLines;
        this.nullString = nullString;
    }

    /**
     * Returns path to the CSV file.
     *
     * @return Path to the CSV file.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns column delimiter.
     *
     * @return Column delimiter.
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Returns record separator.
     *
     * @return Record separator.
     */
    public String getRecordSeparator() {
        return recordSeparator;
    }

    /**
     * Indicates whether to trim column values or not.
     *
     * @return True if values are to be trimmed, otherwise false.
     */
    public boolean isTrim() {
        return trim;
    }

    /**
     * Returns quote character.
     *
     * @return Quote character, if null, no character will be used as quote.
     */
    public Character getQuote() {
        return quote;
    }

    /**
     * Returns comment marker.
     *
     * @return Comment marker.
     */
    public char getCommentMarker() {
        return commentMarker;
    }

    /**
     * Indicates whether to ignore empty lines or not.
     *
     * @return True if empty lines are ignored, otherwise false.
     */
    public boolean isIgnoreEmptyLines() {
        return ignoreEmptyLines;
    }

    /**
     * Returns null string.
     *
     * @return String which should be interpreted as null, if null, no string will be interpreted as null.
     */
    public String getNullString() {
        return nullString;
    }
}

package io.smartcat.ranger.core.csv;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import io.smartcat.ranger.core.Value;

/**
 * Value that read CSV file and returns columns in properties 'c0', 'c1', c2', ...
 */
public class CsvReaderValue extends Value<Map<String, String>> {

    private final Map<String, String> evaluatedValues;
    private final CSVParser csvParser;
    private Iterator<CSVRecord> iterator;

    /**
     * Constructs composite value with specified initial child values.
     *
     * @param parserSettings Settings for the CSV parser.
     */
    public CsvReaderValue(CSVParserSettings parserSettings) {
        if (parserSettings == null) {
            throw new IllegalArgumentException("parserSettings cannot be null.");
        }
        this.evaluatedValues = new HashMap<>();
        this.csvParser = createCSVParser(parserSettings);
        this.iterator = csvParser.iterator();
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void eval() {
        evaluatedValues.clear();
        CSVRecord record = iterator.next();
        for (int i = 0; i < record.size(); i++) {
            String value = record.get(i);
            evaluatedValues.put("c" + i, value);
        }
        val = evaluatedValues;
    }

    private CSVParser createCSVParser(CSVParserSettings parserSettings) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(parserSettings.getDelimiter())
                .withRecordSeparator(parserSettings.getRecordSeparator())
                .withTrim(parserSettings.isTrim())
                .withQuote(parserSettings.getQuote())
                .withCommentMarker(parserSettings.getCommentMarker())
                .withIgnoreEmptyLines(parserSettings.isIgnoreEmptyLines())
                .withNullString(parserSettings.getNullString());
        try {
            File file = new File(parserSettings.getPath());
            return new CSVParser(new FileReader(file), csvFormat);
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }
}

package io.smartcat.ranger.load.generator.csv.datasource;

import java.util.Iterator;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.ranger.load.generator.api.AlreadyClosedException;
import io.smartcat.ranger.load.generator.api.DataSource;

/**
 * Data source that reads data from CSV file.
 *
 * @param <T> Type of the data provided by data source implementation.
 */
public class CSVDataSource<T> implements DataSource<T>, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVDataSource.class);

    private final CSVParser csvParser;
    private final RowMapper<T> rowMapper;

    private Iterator<CSVRecord> iterator;
    private boolean closed = false;

    /**
     * Constructs CSV data source with specified <code>csvParser</code> and <code>rowMapper</code>.
     *
     * @param csvParser parser which provides CSV records to this data source.
     * @param rowMapper mapper which maps CSV records to type {@code <T>}.
     */
    public CSVDataSource(CSVParser csvParser, RowMapper<T> rowMapper) {
        if (csvParser == null) {
            throw new IllegalArgumentException("CSV parser cannot be null.");
        }
        if (rowMapper == null) {
            throw new IllegalArgumentException("Row mapper cannot be null.");
        }
        this.rowMapper = rowMapper;
        this.csvParser = csvParser;
        this.iterator = this.csvParser.iterator();
    }

    @Override
    public boolean hasNext(long time) {
        if (closed) {
            throw new AlreadyClosedException("Data source is already closed.");
        }
        return iterator.hasNext();
    }

    /**
     * @return Next value from this data source, or null if {@link RowMapper} returns null or if error while mapping
     *         happens.
     */
    @Override
    public T getNext(long time) {
        if (closed) {
            throw new AlreadyClosedException("Data source is already closed.");
        }
        CSVRecord record = iterator.next();
        try {
            T result = rowMapper.map(record);
            if (result == null) {
                LOGGER.warn("Returned null for {}. record. Record raw value: {}", record.getRecordNumber(),
                        record.toString());
            }
            return result;
        } catch (RuntimeException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error while mapping " + record.getRecordNumber() + ". record. Record raw value: "
                        + record.toString(), e);
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }

    /**
     * Maps single CVS row represented by {@link CSVRecord} to result object.
     *
     * @param <T> Type of result object.
     */
    public interface RowMapper<T> {

        /**
         * Returns mapped object of {@code <T>} type.
         *
         * @param record record to be mapped.
         * @return mapped object of {@code <T>} type, or null if unable to map record to type {@code <T>}.
         */
        T map(CSVRecord record);
    }
}

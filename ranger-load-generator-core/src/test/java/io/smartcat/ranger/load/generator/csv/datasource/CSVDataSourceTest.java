package io.smartcat.ranger.load.generator.csv.datasource;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.load.generator.api.AlreadyClosedException;
import io.smartcat.ranger.load.generator.csv.datasource.CSVDataSource.RowMapper;

public class CSVDataSourceTest {

    private static final int NUM_OF_RECORDS_IN_SIMPLE_CSV = 5;
    private static final char LF = '\n';

    @Test
    public void hasNext_should_return_true_at_the_beginning_of_non_empty_file() throws Exception {
        // GIVEN
        boolean result = false;
        try (CSVParser csvParser = getCsvParser("simple-csv.csv");
                CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper())) {

            // WHEN
            result = dataSource.hasNext(0);
        }

        // THEN
        Assert.assertEquals(true, result);
    }

    @Test
    public void hasNext_should_return_false_at_the_beginning_of_empty_file() throws Exception {
        // GIVEN
        boolean result = false;
        try (CSVParser csvParser = getCsvParser("empty-csv.csv");
                CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper())) {

            // WHEN
            result = dataSource.hasNext(0);
        }

        // THEN
        Assert.assertEquals(false, result);
    }

    @Test
    public void hasNext_should_return_false_at_the_end_of_file() throws Exception {
        // GIVEN
        boolean result = true;
        try (CSVParser csvParser = getCsvParser("simple-csv.csv");
                CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper())) {
            // file has NUM_OF_RECORDS_IN_SIMPLE_CSV records, run getNext() NUM_OF_RECORDS_IN_SIMPLE_CSV times and then
            // try hasNext()
            for (int i = 0; i < NUM_OF_RECORDS_IN_SIMPLE_CSV; i++) {
                dataSource.getNext(0);
            }

            // WHEN
            result = dataSource.hasNext(0);
        }

        // THEN
        Assert.assertEquals(false, result);
    }

    @Test
    public void first_getNext_should_return_first_row() throws Exception {
        // GIVEN
        User expected = new User("John", "Doe", 31, "555-232-13143", true);
        User result = null;
        try (CSVParser csvParser = getCsvParser("simple-csv.csv");
                CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper())) {

            // WHEN
            result = dataSource.getNext(0);
        }

        // THEN
        Assert.assertEquals(expected, result);
    }

    @Test(expected = NoSuchElementException.class)
    public void getNext_at_the_end_of_file_should_throw_NoSuchElementException() throws Exception {
        // GIVEN
        try (CSVParser csvParser = getCsvParser("simple-csv.csv");
                CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper())) {
            // file has NUM_OF_RECORDS_IN_SIMPLE_CSV records, run getNext() NUM_OF_RECORDS_IN_SIMPLE_CSV times and then
            // try hasNext()
            for (int i = 0; i < NUM_OF_RECORDS_IN_SIMPLE_CSV; i++) {
                dataSource.getNext(0);
            }

            // WHEN
            dataSource.getNext(0);
        }

        // THEN
        // NoSuchElementException is thrown
    }

    @Test(expected = AlreadyClosedException.class)
    public void hasNext_should_throw_AlreadyClosedException_when_dataSource_is_closed() throws Exception {
        // GIVEN
        try (CSVParser csvParser = getCsvParser("simple-csv.csv")) {
            CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper());
            dataSource.close();

            // WHEN
            dataSource.hasNext(0);
        }

        // THEN
        // AlreadyClosedException is thrown
    }

    @Test(expected = AlreadyClosedException.class)
    public void getNext_should_throw_AlreadyClosedException_when_dataSource_is_closed() throws Exception {
        // GIVEN
        try (CSVParser csvParser = getCsvParser("simple-csv.csv")) {
            CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper());
            dataSource.close();

            // WHEN
            dataSource.getNext(0);
        }

        // THEN
        // AlreadyClosedException is thrown
    }

    @Test
    public void getNext_should_return_null_when_row_mapper_throws_exception() throws Exception {
        // GIVEN
        User result = null;
        try (CSVParser csvParser = getCsvParser("invalid-csv.csv");
                CSVDataSource<User> dataSource = new CSVDataSource<>(csvParser, getRowMapper())) {

            // WHEN
            result = dataSource.getNext(0);
        }

        // THEN
        Assert.assertNull(result);
    }

    private CSVParser getCsvParser(String filename) {
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.withQuote(null).withRecordSeparator(LF).withTrim();
            Path filePath = Paths.get(ClassLoader.getSystemResource(filename).toURI());
            return new CSVParser(new FileReader(filePath.toFile()), csvFormat);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RowMapper<User> getRowMapper() {
        return new RowMapper<User>() {

            @Override
            public User map(CSVRecord record) {
                return new User(record.get(0), record.get(1), Integer.parseInt(record.get(2)), record.get(3),
                        Boolean.parseBoolean(record.get(4)));
            }
        };
    }

    private class User {
        private final String firstName;
        private final String lastName;
        private final int age;
        private final String phone;
        private final boolean gender;

        public User(String firstName, String lastName, int age, String phone, boolean gender) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.phone = phone;
            this.gender = gender;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + age;
            result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
            result = prime * result + (gender ? 1231 : 1237);
            result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
            result = prime * result + ((phone == null) ? 0 : phone.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            User other = (User) obj;
            if (age != other.age)
                return false;
            if (firstName == null) {
                if (other.firstName != null)
                    return false;
            } else if (!firstName.equals(other.firstName))
                return false;
            if (gender != other.gender)
                return false;
            if (lastName == null) {
                if (other.lastName != null)
                    return false;
            } else if (!lastName.equals(other.lastName))
                return false;
            if (phone == null) {
                if (other.phone != null)
                    return false;
            } else if (!phone.equals(other.phone))
                return false;
            return true;
        }
    }
}

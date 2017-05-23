package io.smartcat.ranger.runner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;

public class LoadGeneratorRunnerTest {

    @Test(timeout = 3000)
    public void should_not_block_thread_when_worker_is_wrapped_in_async_worker() {
        // GIVEN
        String configFilePath = getFilePath("thread-blocking-runner-test-config.yml");

        // WHEN
        LoadGeneratorRunner.main(new String[] { "-c", configFilePath });

        // THEN
        // if wrapped in async worker, it should finish before timeout
    }

    private String getFilePath(String fileName) {
        URL resource = getClass().getClassLoader().getResource(fileName);
        try {
            return Paths.get(resource.toURI()).toFile().getCanonicalPath();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

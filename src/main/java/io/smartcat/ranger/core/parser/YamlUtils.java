package io.smartcat.ranger.core.parser;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class for loading and selecting specified path of YAML.
 */
public class YamlUtils {

    private static final Pattern JSON_PATH_INDEX = Pattern.compile("^(.+)\\[([0-9]+)\\]$");

    private YamlUtils() {
    }

    /**
     * Loads YAML from input stream.
     *
     * @param inputStream Stream representing YAML file.
     * @return Object representing YAML.
     */
    public static Object load(InputStream inputStream) {
        return load(inputStream, "$");
    }

    /**
     * Loads YAML from input stream with specified path to read.
     *
     * @param inputStream Stream representing YAML file.
     * @param path Path in file to parse.
     * @return Object representing specified path of YAML file.
     */
    public static Object load(InputStream inputStream, String path) {
        Yaml yaml = new Yaml();
        Object parsedYaml = yaml.load(inputStream);
        return select(parsedYaml, path);
    }

    /**
     * Loads YAML from string.
     *
     * @param rawYaml String representing YAML file.
     * @return Object representing YAML.
     */
    public static Object load(String rawYaml) {
        return load(rawYaml, "$");
    }

    /**
     * Loads YAML from string wth specified path to read.
     *
     * @param rawYaml String representing YAML file.
     * @param path Path in file to parse.
     * @return Object representing specified path of YAML file.
     */
    public static Object load(String rawYaml, String path) {
        Yaml yaml = new Yaml();
        Object parsedYaml = yaml.load(rawYaml);
        return select(parsedYaml, path);
    }

    /**
     * Selects element from parsed YAML file for specified path.
     *
     * @param parsedYaml Parsed YAML file.
     * @param path Path in file to select.
     * @return Object representing specified path of YAML file.
     */
    public static Object select(Object parsedYaml, String path) {
        List<Object> pathComponents = toPathList(path);
        if (!"$".equals(pathComponents.get(0))) {
            throw new IllegalArgumentException(
                    String.format("Given path expression %s is expected to start with $", pathComponents));
        }
        if (pathComponents.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Given path expression %s has to have at least one component.", pathComponents));
        }
        return extractPathRecursive(parsedYaml, pathComponents.subList(1, pathComponents.size()));
    }

    private static Object extractPathRecursive(Object input, List<Object> path) {
        Object obj = null;
        if (path.isEmpty()) {
            return input;
        } else {
            Object firstElement = path.get(0);
            if (firstElement instanceof Integer) {
                Integer childIndex = (Integer) firstElement;
                obj = ((List<?>) input).get(childIndex);
            } else {
                String childName = (String) firstElement;
                obj = ((Map<?, ?>) input).get(childName);
            }
            return extractPathRecursive(obj, path.subList(1, path.size()));
        }
    }

    private static List<Object> toPathList(String jsonPath) {
        String[] pathComponents = jsonPath.split("\\.");
        List<Object> pathList = new ArrayList<>();
        for (String component : pathComponents) {
            Matcher m = JSON_PATH_INDEX.matcher(component);
            if (m.matches()) {
                pathList.add(m.group(1));
                pathList.add(Integer.valueOf(m.group(2)));
            } else {
                pathList.add(component);
            }
        }
        return pathList;
    }
}

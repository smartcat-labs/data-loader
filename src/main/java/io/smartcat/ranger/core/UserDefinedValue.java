package io.smartcat.ranger.core;

import com.jayway.jsonpath.JsonPath;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hkilari
 *
 *         UserDefined Value Implementation to allow Berserker User define a Custom Value in
 *         berserker ranger configuration. The Custom Value will be retrieved from the data source
 *         configured by User based on his query config
 *
 */
public class UserDefinedValue extends Value<String> {

  private Value<String> jsonName;
  private Value<String> filter;
  private Value<String> regExpression;
  private List<String> finalContent;
  private long contentlastUpdationTime;
  private Value<String> refreshInterval;
  private Value<String> defaultRefreshInterval = new Value<String>() {
  };

  /**
   * Constructor for UserDefinedValue.
   *
   * @param regExpression
   *          To support the case where the requirement is not complete field and only specific
   *          part.
   * @param refreshInterval
   *          To refresh the data at configured intervals if the source support dynamic data
   *          retrieval.
   * @param filter
   *          JSON path query to extract a Filed from specified Data Source. For semantics, please
   *          refer to https://github.com/json-path/JsonPath.
   * @param jsonName
   *          Can be a name of JSON file available in classpath/ A Rest endpoint that return a JSON
   *          response.
   */
  public UserDefinedValue(Value<String> regExpression, Value<String> refreshInterval,
      Value<String> filter, Value<String> jsonName) {
    defaultRefreshInterval.val = "600";
    if (jsonName == null) {
      throw new IllegalArgumentException("datasource cannot be null.");
    }
    this.jsonName = jsonName;
    this.filter = filter;
    this.regExpression = regExpression;
    this.refreshInterval = refreshInterval == null ? defaultRefreshInterval : refreshInterval;
    this.finalContent = new ArrayList<>();
  }

  @Override
  public void reset() {
    jsonName.reset();
    filter.reset();
    regExpression.reset();
    refreshInterval.reset();
    super.reset();

  }

  @Override
  protected void eval() {
    if (isContentExpired()) {
      getDataFromJson();
    }
    val = getRandomValue();
  }

  private boolean isContentExpired() {
    // Checks if the finalContent to be refreshed based on used defined refreshInterval.
    if (System.currentTimeMillis() - contentlastUpdationTime > Long.parseLong(refreshInterval.val)
        * 1000) {
      return true;
    }
    return false;
  }

  private String getRandomValue() {
    if (finalContent != null && !finalContent.isEmpty())
      return extractField(finalContent.get(new Random().nextInt(finalContent.size())).toString(),
          regExpression.get());
    else
      return "";
  }

  private void getDataFromJson() {
    try {
      // check If Source is a http service
      if (jsonName.get().contains("http")) {
        getDataFromHttpRequest();
      } else {
        // Source treated as a local json file
        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(jsonName.get() + ".json");
        if (in == null) {
          in = new FileInputStream(jsonName.get() + ".json");
        }
        if (in != null) {
          finalContent = JsonPath.read(in, filter.get());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves data from a http service endpoint.
   */
  private void getDataFromHttpRequest() {
    try {
      InputStream in;
      URL obj = new URL(jsonName.val);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("accept", "application/json");
      int responseCode = con.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) { // success
        in = con.getInputStream();
        finalContent = JsonPath.read(in, filter.get());
        in.close();
        contentlastUpdationTime = System.currentTimeMillis();
      } else {
        System.out.println("GET request not worked");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param latestCommandOutput
   * @param regExpression
   * @return
   */
  private String extractField(String inputString, String regExpression) {
    if (regExpression != null && !"".equals(regExpression.trim())) {
      Pattern p = Pattern.compile(regExpression);
      Matcher m = p.matcher(inputString);
      if (m.find()) {
        if (m.groupCount() > 0)
          return m.group(1);
      }
      return null;
    }
    return inputString;
  }
}

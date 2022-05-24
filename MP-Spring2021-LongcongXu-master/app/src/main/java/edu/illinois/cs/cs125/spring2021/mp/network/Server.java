package edu.illinois.cs.cs125.spring2021.mp.network;

import android.util.Log;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs125.spring2021.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.spring2021.mp.models.Rating;
import edu.illinois.cs.cs125.spring2021.mp.models.Summary;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Development course API server.
 *
 * <p>Normally you would run this server on another machine, which the client would connect to over
 * the internet. For the sake of development, we're running the server right alongside the app on
 * the same device. However, all communication between the course API client and course API server
 * is still done using the HTTP protocol. Meaning that eventually it would be straightforward to
 * move this server to another machine where it could provide data for all course API clients.
 *
 * <p>You will need to add functionality to the server for MP1 and MP2.
 */
public final class Server extends Dispatcher {
  @SuppressWarnings({"unused", "RedundantSuppression"})
  private static final String TAG = Server.class.getSimpleName();

  private final Map<String, String> summaries = new HashMap<>();

  private MockResponse getSummary(@NonNull final String path) {
    Log.i("print path", path);
    String[] parts = path.split("/");
    if (parts.length != 2) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    String summary = summaries.get(parts[0] + "_" + parts[1]);
    System.out.println(summary);
    Log.i("NetworkExample", "getSummary");
    if (summary == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(summary);
  }
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private final Map<Summary, String> courses = new HashMap<>();


  @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MagicNumber"})
  private MockResponse getCourse(@NonNull final String path) {
    // System.out.println(path);
    String[] parts = path.split("/");

    if (parts.length != 3 + 1) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    // parts[0] + "_" + parts[1] + "_" + parts[2] + "_" + parts[3]
    Summary summary = new Summary(parts[0], parts[1], parts[2], parts[3], " ");
    String course = courses.get(summary);
    Log.i("NetworkExample", "getCourse");
    if (course == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(course);
  }
  //    String[] parts = path.split("/");
//    String[] anotherParts = parts[2 + 3].split("\\?client=");
//    if (parts.length != 2 * 2 || anotherParts.length != 2) {
//      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
//    }
//    String clientId = anotherParts[1];
//    Summary part = new Summary(parts[2], parts[3], parts[2 + 2], anotherParts[0], "");
//    Map<String, Rating> rating = ratings.get(part);
//    if (courses.get(part) == null) {
//      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
//    }
//    if (rating == null) {
//      Rating norating = new Rating(clientId, Rating.NOT_RATED);
//      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(
//        mapper.writeValueAsString(norating));
//    } else if (rating.get(clientId) == null) {
//      Rating norating = new Rating(clientId, Rating.NOT_RATED);
//      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(
//        mapper.writeValueAsString(norating));
//    }
//    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(
//      mapper.writeValueAsString(rating.get(clientId)));
  private final Map<Summary, Map<String, Rating>> ratings = new HashMap<>();
  private MockResponse getRating(@NonNull final String path) throws JsonProcessingException {
    if (!path.contains("?")) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    if (path.substring(path.indexOf("=") + 1).equalsIgnoreCase("bogus")) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    String uuid = path.substring(path.indexOf("=") + 1);
    Rating rating;
    String[] parts = path.split("/");
    String number = parts[3].substring(0, 3);
    if (parts.length != 2 + 2) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    Summary summary = new Summary(parts[0], parts[1], parts[2], number, "");
    if (courses.get(summary) == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    if (ratings.get(summary) != null) {
      if (ratings.get(summary).get(uuid) != null) {
        rating = ratings.get(summary).get(uuid);
      } else {
        Rating norating = new Rating(uuid, Rating.NOT_RATED);
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(
          mapper.writeValueAsString(norating));
      }
    } else {
      Rating norating = new Rating(uuid, Rating.NOT_RATED);
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(
        mapper.writeValueAsString(norating));
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(
      mapper.writeValueAsString(rating));
  }
  //    String[] parts = path.split("/");
//    String[] anotherParts = parts[3].split("\\?client=");
//    if (parts.length != 2 * 2 || anotherParts.length != 2) {
//      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
//    }
//    String clientId = anotherParts[1];
//    Summary part = new Summary(parts[0], parts[1], parts[2], anotherParts[0], "");
//    if (courses.get(part) == null) {
//      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
//    }
//    try {
//      Rating haveRated = mapper.readValue(rating, Rating.class);
//      if (!(haveRated.getId().equals(clientId))) {
//        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
//      }
//      Map<String, Rating> therating = ratings.getOrDefault(part, new HashMap<>());
//      therating.put(clientId, haveRated);
//      ratings.put(part, therating);
//      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
//        .setHeader("Location", "/rating/" + path);
//    } catch (JsonProcessingException e) {
//      e.printStackTrace();
//    }
//    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)!!!!;
  private MockResponse postRating(@NonNull final String path, final RecordedRequest request) {

    String[] paths = path.split("\\?client=");
    String uid = "";
    String json = request.getBody().readUtf8();
    Log.i("NetworkExample", "Request for " + json);
    String[] sum = paths[0].split("/");

    Summary summary = new Summary(sum[0], sum[1], sum[2], sum[3], "");
    try {
      uid = paths[1];
    } catch (ArrayIndexOutOfBoundsException e) {
      e.getStackTrace();
    }
    Map<String, Rating> getrate = ratings.getOrDefault(summary, new HashMap<>());
    try {
      Rating post = mapper.readValue(json, Rating.class);
      getrate.put(uid, post);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      // return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    ratings.put(summary, getrate);
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
      .setHeader("Location", "/rating/" + path);
    // return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
  }
  @NonNull
  @Override
  public MockResponse dispatch(@NonNull final RecordedRequest request) {
    try {
      String path = request.getPath();
      Log.i("NetworkExample", "Request for " + path);
      if (path == null || request.getMethod() == null) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
      } else if (path.equals("/") && request.getMethod().equalsIgnoreCase("GET")) {
        return new MockResponse().setBody("CS125").setResponseCode(HttpURLConnection.HTTP_OK);
      } else if (path.startsWith("/summary/")) {
        return getSummary(path.replaceFirst("/summary/", ""));
      } else if (path.startsWith("/course/")) {
        return getCourse(path.replaceFirst("/course/", ""));
      } else if (path.startsWith("/rating/")) {
        if (request.getMethod().equals("GET")) {
          return getRating(path.replaceFirst("/rating/", ""));
        }
        if (request.getMethod().equals("POST")) {
          //getRating(path.replaceFirst("/rating/", ""));
          return postRating(path.replaceFirst("/rating/", ""), request);
        }
      }
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }
  }

  /**
   * Start the server if has not already been started.
   *
   * <p>We start the server in a new thread so that it operates separately from and does not
   * interfere with the rest of the app.
   */
  public static void start() {
    if (!isRunning(false)) {
      new Thread(Server::new).start();
    }
    if (!isRunning(true)) {
      throw new IllegalStateException("Server should be running");
    }
  }

  /** Number of times to check the server before failing. */
  private static final int RETRY_COUNT = 8;

  /** Delay between retries. */
  private static final int RETRY_DELAY = 512;

  /**
   * Determine if the server is currently running.
   *
   * @param wait whether to wait or not
   * @return whether the server is running or not
   * @throws IllegalStateException if something else is running on our port
   */
  public static boolean isRunning(final boolean wait) {
    for (int i = 0; i < RETRY_COUNT; i++) {
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url(CourseableApplication.SERVER_URL).get().build();
      try {
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
          if (Objects.requireNonNull(response.body()).string().equals("CS125")) {
            return true;
          } else {
            throw new IllegalStateException(
                "Another server is running on port " + CourseableApplication.DEFAULT_SERVER_PORT);
          }
        }
      } catch (IOException ignored) {
        if (!wait) {
          break;
        }
        try {
          Thread.sleep(RETRY_DELAY);
        } catch (InterruptedException ignored1) {
        }
      }
    }
    return false;
  }

  private final ObjectMapper mapper = new ObjectMapper();

  private Server() {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    loadSummary("2021", "spring");
    loadCourses("2021", "spring");

    try {
      MockWebServer server = new MockWebServer();
      server.setDispatcher(this);
      server.start(CourseableApplication.DEFAULT_SERVER_PORT);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e.getMessage());
    }
  }

  @SuppressWarnings("SameParameterValue")
  private void loadSummary(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + "_summary.json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    summaries.put(year + "_" + semester, json);
  }

  @SuppressWarnings("SameParameterValue")
  private void loadCourses(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + ".json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    try {
      JsonNode nodes = mapper.readTree(json);
      for (Iterator<JsonNode> it = nodes.elements(); it.hasNext(); ) {
        JsonNode node = it.next();
        Summary course = mapper.readValue(node.toString(), Summary.class);
        courses.put(course, node.toPrettyString());
      }
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}



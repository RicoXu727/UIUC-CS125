package edu.illinois.cs.cs125.spring2021.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import edu.illinois.cs.cs125.spring2021.mp.R;
import edu.illinois.cs.cs125.spring2021.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.spring2021.mp.databinding.ActivityCourseBinding;

import edu.illinois.cs.cs125.spring2021.mp.models.Course;
import edu.illinois.cs.cs125.spring2021.mp.models.Summary;
import edu.illinois.cs.cs125.spring2021.mp.network.Client;


/**
 * CourseActivity showing the course summary list.
 */
public class CourseActivity extends AppCompatActivity implements Client.CourseClientCallbacks {
  @SuppressWarnings({"unused", "RedundantSuppression"})
  private static final String TAG = CourseActivity.class.getSimpleName();
  private ActivityCourseBinding binding;

  /**
   * Callback called when the client has retrieved the list of courses for this component to
   * display.
   */
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "CourseActivity lunched");

    binding = DataBindingUtil.setContentView(this, R.layout.activity_course);

    Intent intent = getIntent();
    String course = intent.getStringExtra("COURSE");
    //Log.i(TAG, course);
    ObjectMapper mapper = new ObjectMapper();
    //String json;
    Summary summary = null;
    try {
      summary = mapper.readValue(course, Summary.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    // Retrieve the API client from the application and initiate a course summary request
    CourseableApplication application = (CourseableApplication) getApplication();
    Log.i("NetworkExample", "MainActivity getCourse");
    assert summary != null;
    application.getCourseClient().getCourse(summary, this);

  }

  /**
   * Callback called when the client has retrieved the list of courses for this component to
   * display.
   *
   * @param summary the year that was retrieved
   * @param course  the semester that was retrieved
   */

  @Override
  public void courseResponse(final Summary summary, final Course course) {
    // Bind to the layout in activity_course.xml
    binding.title.setText(summary.getTitle());
    binding.description.setText(course.getDescription());
  }
}

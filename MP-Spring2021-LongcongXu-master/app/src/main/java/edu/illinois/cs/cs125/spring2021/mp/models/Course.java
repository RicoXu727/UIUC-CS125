package edu.illinois.cs.cs125.spring2021.mp.models;

/**
 * Create Course class.
 *
 * <p> finish the class
 */
@SuppressWarnings({"checkstyle:JavadocStyle", "checkstyle:RegexpSingleline"})
public class Course extends Summary {
  private String description;
  /**
   * Create an empty Course.
   */
  public Course() {}

  /**
   * Create a Summary with the provided fields.
   *
   * @param setDescription the description for this Course
   *
   */

  public Course(
      final String setDescription) {
    description = setDescription;
  }
  /**
   * Get the description for this Course.
   *
   * @return the description for this Course
   */
  public String getDescription() {
    return description;

  }
}

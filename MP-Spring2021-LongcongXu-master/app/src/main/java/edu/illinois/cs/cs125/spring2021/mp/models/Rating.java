package edu.illinois.cs.cs125.spring2021.mp.models;


/**
 * Create Rating class.
 *
 * <p> finish the class
 */
public class Rating {
  /**
   * Create an fianl NOT_RATED.
   */
  public static final double NOT_RATED = -1.0;

  private String id;
  private double rating;
  /**
   * Create a Summary with the provided fields!!!!.
   *
   */
  public Rating() { }
  /**
   * Create a Summary with the provided fields.
   *
   * @param setRating the rating for this Course
   * @param setId the id for this Client
   */
  public Rating(final String setId, final double setRating) {
    id = setId;
    rating = setRating;
  }

  /**
   * Get the description for this Course.
   *
   * @return the description for this Course
   */
  public String getId() {
    return id;
  }

  /**
   * Get the rating for this Course.
   *
   * @return the rating for this Course
   */
  public double getRating() {
    return rating;
  }
}

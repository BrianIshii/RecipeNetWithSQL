package EntityTests;

import java.sql.Date;
import java.time.LocalDate;

public class Constants {
  public static final String USER_NAME = "tester";
  public static final String USER_EMAIL = "tester@test";
  public static final String USER_PASSWORD = "test";

  public static final String[] INGREDIENTS =
      new String[] {
        "Salt",
        "Pepper",
        "Avocado",
        "Refried beans",
        "Green Onion",
        "Black beans",
        "Water",
        "Tortilla"
      };

  public static final String[] INSTRUCTIONS =
      new String[] {"Add ingredient 1", "Add ingredient 2", "Mix the ingredients", "Throw it away"};

  public static final String RECIPE_TITLE = "Guac";
  public static final String RECIPE_URL = "URL";
  public static final Date RECIPE_DATE = Date.valueOf(LocalDate.now());
  public static final Integer RECIPE_RATING = 5;
}

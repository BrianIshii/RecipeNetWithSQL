package RecipeNet.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Unit {
    private static String[] words = {
            "cup", "gallon","tsp.", "tbsp.", "fl oz",
            "pint", "quart", "ml", "litre", "lb", "oz",
            "mg", "g", "kg"};

    public static List<String> units = new ArrayList<String>(Arrays.asList(words));
}

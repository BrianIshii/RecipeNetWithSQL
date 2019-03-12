package schema;

import java.util.ArrayList;
import java.util.Collection;

public class RequestSchema extends Schema {
    public RequestSchema(Collection<Field> fields) {
        this.fields = new ArrayList<>();
        for (Field f : fields) {
            this.fields.add(f.clone());
        }
    }

    public RequestSchema() {
        fields = new ArrayList<>();
    }
}

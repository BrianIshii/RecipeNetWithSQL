package RecipeNet.schema;

import java.util.ArrayList;

public class ResponseSchema extends Schema {
    public ResponseSchema(RequestSchema requestSchema) {
        fields = deepCopyFields(requestSchema);
    }

    public ResponseSchema(Schema schema) {
        fields = deepCopyFields(schema);
    }

    public ResponseSchema() {
        this.fields = new ArrayList<>();
    }
}

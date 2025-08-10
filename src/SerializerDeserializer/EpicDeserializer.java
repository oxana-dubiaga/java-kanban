package SerializerDeserializer;

import com.google.gson.*;
import model.Epic;

import java.lang.reflect.Type;

public class EpicDeserializer implements JsonDeserializer<Epic> {

    @Override
    public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int id = jsonObject.get("id").getAsInt();
        return new Epic(name, description, id);
    }
}

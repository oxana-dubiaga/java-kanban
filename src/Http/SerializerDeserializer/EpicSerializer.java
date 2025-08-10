package Http.SerializerDeserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Epic;

import java.lang.reflect.Type;

public class EpicSerializer implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic epic, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonEpic = new JsonObject();
        jsonEpic.addProperty("name", epic.getName());
        jsonEpic.addProperty("description", epic.getDescription());
        jsonEpic.addProperty("id", epic.getId());

        return jsonEpic;
    }
}

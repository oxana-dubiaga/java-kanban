package SerializerDeserializer;

import com.google.gson.*;
import model.Subtask;

import java.lang.reflect.Type;

public class SubtaskDeserializer implements JsonDeserializer<Subtask> {
    @Override
    public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int id = jsonObject.get("id").getAsInt();
        int epicId = jsonObject.get("epicId").getAsInt();
        String startTime = jsonObject.get("startTime").getAsString();
        int duration = jsonObject.get("duration").getAsInt();

        return new Subtask(name, description, id, epicId, duration, startTime);
    }
}

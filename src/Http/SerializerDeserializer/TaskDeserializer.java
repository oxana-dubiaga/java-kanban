package Http.SerializerDeserializer;

import com.google.gson.*;
import model.Task;

import java.lang.reflect.Type;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int id = jsonObject.get("id").getAsInt();
        String startTime = jsonObject.get("startTime").getAsString();
        int duration = jsonObject.get("duration").getAsInt();

        return new Task(name, description, id, duration, startTime);
    }

}

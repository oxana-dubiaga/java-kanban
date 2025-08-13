package serializerdeserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Subtask;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonTask = new JsonObject();
        jsonTask.addProperty("name", subtask.getName());
        jsonTask.addProperty("description", subtask.getDescription());
        jsonTask.addProperty("id", subtask.getId());
        jsonTask.addProperty("epicId", subtask.getEpicId());
        jsonTask.add("duration", context.serialize(subtask.getDuration(), Duration.class));
        jsonTask.add("startTime", context.serialize(subtask.getStartTime(), LocalDateTime.class));

        return jsonTask;
    }
}

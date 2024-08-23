package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history = new ArrayList<>();


    //в классе InMemoryTaskManager в методах получения всех типов задач по id идет проверка,
    // содержится ли задача с таким ключом в соответствующей мапе,
    // и только в этом случае возвращается задача по id и добавляется в историю,
    // а при запросе несуществующей задачи возвращается null, но в историю он не попадает
    //И при добавлении задач в мапы тоже делаю проверку на null
    //то есть null не может попасть в историю
    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void clearHistory() {
        history.clear();
    }
}

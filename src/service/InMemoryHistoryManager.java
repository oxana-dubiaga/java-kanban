package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    //private ArrayList<Task> history = new ArrayList<>();


    static class Node<T> {
        Node<T> previous;
        Node<T> next;
        T value;

        public Node(T value) {
            this.value = value;
            this.previous = null;
            this.next = null;
        }
    }

    private HashMap<Integer, Node<Task>> mapHistory = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;


    @Override
    public void add(Task task) {
        Node<Task> newNode = new Node<>(task);
        //если история была пуста, то добавляемая задача будет first
        if (mapHistory.isEmpty()) {
            first = newNode;
            mapHistory.put(task.getId(), newNode);
        } else if (mapHistory.size() == 1) {
            // если история состоит из одного элемента, то этот элемент first
            if (mapHistory.containsKey(task.getId())) {
                mapHistory.remove(task.getId());
                mapHistory.put(task.getId(), newNode);
                first = newNode;
            } else {
                first.next = newNode;
                newNode.previous = first;
                last = newNode;
                mapHistory.put(first.value.getId(), first);
                mapHistory.put(task.getId(), newNode);
            }
        } else {
            if (mapHistory.containsKey(task.getId())) {
                remove(task.getId());
            }
            Node<Task> oldLast = last;
            oldLast.next = newNode;
            newNode.previous = oldLast;
            last = newNode;

            mapHistory.put(oldLast.value.getId(), oldLast);
            mapHistory.put(task.getId(), newNode);

        }


    }

    @Override
    public void remove(int id) {
        if (mapHistory.containsKey(id)) {
            Node<Task> nodeToRemove = mapHistory.get(id);

            Node<Task> oldPrev = nodeToRemove.previous;
            Node<Task> oldNext = nodeToRemove.next;

            //если у удаляемой ноды предыдущий нод это null, то удаляемая нода - это first
            //и после ее удаления следующая за ней должна стать first
            if (oldPrev != null) {
                oldPrev.next = oldNext;
                mapHistory.put(oldPrev.value.getId(), oldPrev);
            } else {
                first = oldNext;
            }

            //если у удаляемой ноды следующий нод это null, то удаляемая нода - это last
            //и после ее удаления предыдущая нода должна стать last
            if (oldNext != null) {
                oldNext.previous = oldPrev;
                mapHistory.put(oldNext.value.getId(), oldNext);
            } else {
                last = oldPrev;
            }

            mapHistory.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> current = first;
        for (int i = 0; i < mapHistory.size(); i++) {
            tasks.add(current.value);
            try {
                current = current.next;
            } catch (NullPointerException ignored) {
            }
        }
        return tasks;
    }

    @Override
    public void clearHistory() {
        mapHistory.clear();
    }
}

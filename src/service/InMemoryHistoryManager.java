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
        remove(task.getId());
        linkLast(task);
    }


    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);

        //если first == null - то история пустая, и добавляемый нод будет как first, так и last
        //если не пустая, просто добавляем в конец с корректировкой поля next предудущего (прошлого last) нода
        if (first == null) {
            first = newNode;
            last = newNode;
            mapHistory.put(task.getId(), newNode);
        } else {
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
            removeNode(nodeToRemove);
        }
    }

    private void removeNode(Node<Task> nodeToRemove) {
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

        mapHistory.remove(nodeToRemove.value.getId());
    }


    @Override
    public ArrayList<Task> getTasks() {
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
        first = null;
        last = null;
    }
}

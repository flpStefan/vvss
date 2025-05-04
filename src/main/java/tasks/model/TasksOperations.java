package tasks.model;

import javafx.collections.ObservableList;

import java.util.*;

public class TasksOperations
{
    private final ArrayList<Task> tasks;

    public TasksOperations(ObservableList<Task> tasksList)
    {
        tasks = new ArrayList<>();
        tasks.addAll(tasksList);
    }

    public Iterable<Task> incoming(Date start, Date end)
    {
        ArrayList<Task> incomingTasks = new ArrayList<>();
        if (!end.before(start)) {
            for (Task task : tasks) {
                Date nextTime = task.nextTimeAfter(start);
                if (nextTime != null) {
                    if ((nextTime.before(end) || nextTime.equals(end))) {
                        incomingTasks.add(task);
                        System.out.println(task.getTitle());
                    }
                }
            }
        }

        return incomingTasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
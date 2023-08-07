package com.samyuueruu.todolist.todolistmanager.model;

import java.io.*;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class ToDoList {
    private List<Task> taskList;

    public ToDoList() {
        taskList = new ArrayList<>();
        loadTaskList();
    }

    public Task getTask(int index){
        return taskList.get(index);
    }

    public int getIndexOfTask(Task task){
        return taskList.indexOf(task);
    }

    public void createTask(String title, String description, boolean completed, int day, int month, int year){
        // Adds a task to list
        taskList.add(new NonRecurringTask(title, description, completed, day, month, year));
    }

    public void createTask(String title, String description, boolean completed, String dayName){
        // Adds a task to list
        taskList.add(new RecurringTask(title, description, completed, dayName));
    }

    public void deleteTask(Task task){
        if (!taskList.contains(task)){
            System.out.println("Task cannot be deleted because it doesn't exist.");
            return;
        }
        taskList.set(taskList.indexOf(task), null);
    }

    public void displayTaskList(){
        for (Task task:taskList) {
            System.out.println(task.showDetails());
        }
    }

    public void saveTaskList(){
        taskList.removeAll(Collections.singleton(null));
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Writer writer;
        try {
            writer = new FileWriter("TaskList.json");
            gson.toJson(taskList, writer);
            writer.flush();
            writer.close();
            System.out.println("Task list saved successfully!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTaskList(){
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonReader reader;

        try {
            reader = new JsonReader(new FileReader("TaskList.json"));
            reader.hasNext();
        } catch (FileNotFoundException e) {
            System.out.println("File TaskList.json not found!");
            return;
        } catch (IOException e) {
            System.out.println("File TaskList.json is empty!");
            return;
        }

        JsonArray jsonArray = gson.fromJson(reader, JsonElement.class);
        System.out.println("File TaskList.json loaded successfully!");

        for (JsonElement jsonElement:jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.has("dayName")){
                createTask(jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString(),
                        jsonObject.get("completed").getAsBoolean(), jsonObject.get("dayName").getAsString());
            }
            else{
                createTask(jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString(),
                        jsonObject.get("completed").getAsBoolean(), jsonObject.get("day").getAsInt(),
                        jsonObject.get("month").getAsInt(), jsonObject.get("year").getAsInt());
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sortList(Type type){
        // Sort by task title in ascending order
        if (type.equals(String.class)){
            this.taskList.sort((o1, o2) -> o1.title.compareTo(o2.title));
        } // Sort by task completion in ascending order
        else if (type.equals(Boolean.class)) {
            this.taskList.sort((o1, o2) -> Boolean.compare(!o1.completed,!o2.completed));
        } // Sort by task due date in ascending order
        else if (type.equals(LocalDate.class)){
            this.taskList.sort((o1, o2) -> {
                if (o1 instanceof NonRecurringTask && o2 instanceof NonRecurringTask){
                    return ((NonRecurringTask)o1).dueDate.compareTo(((NonRecurringTask)o2).dueDate);
                } else if (o1 instanceof NonRecurringTask && o2 instanceof RecurringTask){
                    return -1;
                } else if (o1 instanceof RecurringTask && o2 instanceof NonRecurringTask){
                    return 0;
                }
                return 0;
            });
        } // Sort by recurrence - first NonRecurring
        else if (type.equals(NonRecurringTask.class)){
            this.taskList.sort((o1, o2) -> {
                if (o1 instanceof NonRecurringTask && o2 instanceof RecurringTask) {
                    return -1;
                }
                return 0;
            });
        } // Sort by day on which task recurs in ascending order
        else if (type.equals(DayOfWeek.class)){
            this.taskList.sort((o1, o2) -> {
                if (o1 instanceof RecurringTask && o2 instanceof RecurringTask) {
                    return DayOfWeek.valueOf(((RecurringTask) o1).dayName).compareTo(DayOfWeek.valueOf(((RecurringTask) o2).dayName));
                } else if (o1 instanceof RecurringTask && o2 instanceof NonRecurringTask){
                    return -1;
                } else if (o1 instanceof NonRecurringTask && o2 instanceof RecurringTask){
                    return 0;
                }
                return 0;
            });
        }
    }

    public void reverseSortList(){
        Collections.reverse(this.taskList);
    }

    public int getListSize(){
        return this.taskList.size();
    }
}

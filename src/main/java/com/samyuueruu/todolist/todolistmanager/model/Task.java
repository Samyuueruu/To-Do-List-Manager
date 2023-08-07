package com.samyuueruu.todolist.todolistmanager.model;

public abstract class Task{
    protected String title;
    protected String description;
    protected boolean completed = false;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public void toggleCompleted(){
        this.completed = !this.completed;
    }
    public abstract String showDetails();
    public abstract void editDetails(String title, String description, int day, int month, int year);
    public abstract void editDetails(String title, String description, String dayName);

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public boolean isCompleted() {
        return completed;
    }
}

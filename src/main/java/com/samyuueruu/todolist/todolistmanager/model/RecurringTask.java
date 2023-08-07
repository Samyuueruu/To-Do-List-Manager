package com.samyuueruu.todolist.todolistmanager.model;

public class RecurringTask extends Task{
    protected String dayName;

    public RecurringTask(String title, String description, boolean completed, String dayName) {
        super(title,description, completed);
        this.dayName = dayName;
    }

    @Override
    public String showDetails() {
        return super.title + " " + super.description + " " + dayName;
    }

    @Override
    public void editDetails(String title, String description, int day, int month, int year) {
        System.out.println("Wrong editDetails method - this task is a recurring task.");
    }

    @Override
    public void editDetails(String title, String description, String dayName) {
        super.title = title;
        super.description = description;
        this.dayName = dayName;
    }

    public String getDayName() {
        return dayName;
    }
}

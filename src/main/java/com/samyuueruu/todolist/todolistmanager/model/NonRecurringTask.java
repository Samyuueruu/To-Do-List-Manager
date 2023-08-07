package com.samyuueruu.todolist.todolistmanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NonRecurringTask extends Task{
    protected int day;
    protected int month;
    protected int year;
    protected transient LocalDate dueDate;

    public NonRecurringTask(String title, String description, boolean completed, int day, int month, int year) {
        super(title, description, completed);
        this.day = day;
        this.month = month;
        this.year = year;
        this.dueDate = LocalDate.of(year,month,day);
    }

    @Override
    public String showDetails() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return super.title + " " + super.description + " " + dueDate.format(dateTimeFormatter);
    }

    @Override
    public void editDetails(String title, String description, int day, int month, int year) {
        super.title = title;
        super.description = description;
        this.day = day;
        this.month = month;
        this.year = year;
        this.dueDate = LocalDate.of(year,month,day);
    }

    @Override
    public void editDetails(String title, String description, String dayName) {
        System.out.println("Wrong editDetails method - this task is a non-recurring task.");
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}

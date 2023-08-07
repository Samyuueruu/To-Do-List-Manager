package com.samyuueruu.todolist.todolistmanager.ui;

import com.samyuueruu.todolist.todolistmanager.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainWindow extends Application {
    private VBox tasksVBox; // Child of ScrollPane
    private HBox dateInputHBox; // Child of StackPane and addTaskVBox
    private HBox dayNameHBox; // Child of StackPane and addTaskVBox
    private VBox addTaskVBox; // Child of rootVBox
    private final ToDoList toDoList = new ToDoList();
    private int numberOfTasksAdded;

    @Override
    public void start(Stage primaryStage) {
        tasksVBox = new VBox();
        tasksVBox.setSpacing(10);
        tasksVBox.setPadding(new Insets(10));

        TextField titleField = new TextField();
        TextField descriptionField = new TextField();

        ComboBox<String> dayNameComboBox = new ComboBox<>(); // Create ComboBox to hold string values of days in a drop-down menu
        dayNameComboBox.getItems().addAll("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        // Default value in drop-down menu
        dayNameComboBox.setValue("MONDAY");

        dayNameHBox = new HBox(); // Create an HBox to hold name of the recurring day
        dayNameHBox.setSpacing(5);
        dayNameHBox.getChildren().addAll(new Label("Recurring on:"), dayNameComboBox);
        dayNameHBox.setVisible(false);
        dayNameHBox.setManaged(false);

        dayNameComboBox.setOnAction(e -> {
            String selectedOption = dayNameComboBox.getValue();
        });

        // Non-recurring task fields
        TextField dayField = new TextField();
        dayField.setPrefWidth(30);
        TextField monthField = new TextField();
        monthField.setPrefWidth(35);
        TextField yearField = new TextField();
        yearField.setPrefWidth(50);

        dateInputHBox = new HBox(); // Create an HBox to hold the date input fields
        dateInputHBox.setSpacing(5);
        dateInputHBox.getChildren().addAll(
                new Label("Day:"), dayField,
                new Label("Month:"), monthField,
                new Label("Year:"), yearField
        );

        StackPane stackPane = new StackPane(dayNameHBox, dateInputHBox);

        CheckBox recurringCheckBox = new CheckBox("Recurring Task");
        Button addButton = new Button("Add Task");

        ComboBox<String> orderByComboBox = new ComboBox<>(); // Create ComboBox to hold string values of days in a drop-down menu
        orderByComboBox.getItems().addAll("by title", "by completion", "by due date", "by recurrence", "by day of week");

        orderByComboBox.setValue("sort by");

        orderByComboBox.setOnAction(e -> {
            String selectedOption = orderByComboBox.getValue();
            switch (selectedOption) {
                case "by title":
                    toDoList.sortList(String.class);
                    break;
                case "by completion":
                    toDoList.sortList(Boolean.class);
                    break;
                case "by due date":
                    toDoList.sortList(LocalDate.class);
                    break;
                case "by recurrence":
                    toDoList.sortList(NonRecurringTask.class);
                    break;
                case "by day of week":
                    toDoList.sortList(DayOfWeek.class);
                    break;
            }
            tasksVBox.getChildren().clear();
            numberOfTasksAdded = 0;
            this.loadTasksToBoxes();
        });

        HBox addHBox = new HBox();
        addHBox.setSpacing(5);
        addHBox.getChildren().addAll(addButton, orderByComboBox);

        // Event on select recurringCheckBox
        recurringCheckBox.setOnAction(e -> {
            // If the checkbox is checked, hide date input fields
            boolean isRecurring = recurringCheckBox.isSelected();
            dateInputHBox.setVisible(!isRecurring);
            dateInputHBox.setManaged(!isRecurring);
            dayNameHBox.setVisible(isRecurring);
            dayNameHBox.setManaged(isRecurring);
        });

        // Event on action (click on button) addButton
        addButton.setOnAction(e -> {

            String title = titleField.getText();
            String description = descriptionField.getText();

            boolean isRecurring = recurringCheckBox.isSelected();

            String day = isRecurring ? "" : dayField.getText();
            String month = isRecurring ? "" : monthField.getText();
            String year = isRecurring ? "" : yearField.getText();

            if (recurringCheckBox.isSelected()){
                toDoList.createTask(title, description, false, dayNameComboBox.getValue());
                VBox newTaskBox = createTaskBox(titleField.getText(), descriptionField.getText(), dayNameComboBox.getValue());
                tasksVBox.getChildren().add(newTaskBox);
            } else {
                toDoList.createTask(title, description, false, Integer.parseInt(day), Integer.parseInt(month),
                        Integer.parseInt(year));
                VBox newTaskBox = createTaskBox(titleField.getText(), descriptionField.getText(),
                        ((NonRecurringTask) toDoList.getTask(toDoList.getListSize()-1)).getDueDate());
                tasksVBox.getChildren().add(newTaskBox);
            }

            // Clear the input fields after adding the task
            titleField.clear();
            descriptionField.clear();
            dayField.clear();
            dayField.clear();
            monthField.clear();
            yearField.clear();
            recurringCheckBox.setSelected(false);
        });

        // Event on closing app window
        primaryStage.setOnCloseRequest(event -> {
            toDoList.saveTaskList();
        });

        if (toDoList.getListSize() > 0){
            loadTasksToBoxes();
        }

        // Create a VBox to hold the input fields, the Recurring Task switch, and the Add Task button
        addTaskVBox = new VBox(
                new Label("Title:"), titleField,
                new Label("Description:"), descriptionField,
                recurringCheckBox, stackPane, addHBox);
        addTaskVBox.setSpacing(5);
        addTaskVBox.setPadding(new Insets(10));

        // Create a ScrollPane and set the root tasksVBox as its content
        ScrollPane scrollPane = new ScrollPane(tasksVBox);
        scrollPane.setFitToWidth(true); // Make the ScrollPane fit the width of the window

        // Combine the addTaskVBox section and the scrollPane using another VBox
        VBox rootVBox = new VBox(addTaskVBox, scrollPane);

        // Create a scene and set the rootVBox on the stage
        Scene scene = new Scene(rootVBox, 560, 720);
        primaryStage.setTitle("To-Do-List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create a single task details box
    private VBox createTaskBox(String title, String description, String dayName) {
        Label titleLabel = new Label("Title: " + title);
        Label descriptionLabel = new Label("Description: " + description);
        Label dayNameLabel = new Label("Recurring on: " + dayName);
        int index = numberOfTasksAdded;
        numberOfTasksAdded++;

        Button completeButton = new Button("Complete");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button saveEditButton = new Button("Save Edit");
        saveEditButton.setDisable(true);

        completeButton.setOnAction(e ->{
            toDoList.getTask(index).toggleCompleted();
        });

        deleteButton.setOnAction(e -> {
            toDoList.deleteTask(toDoList.getTask(index));
            tasksVBox.getChildren().remove(deleteButton.getParent().getParent());
        });

        editButton.setOnAction(e -> {
            saveEditButton.setDisable(false);
            // Set title and description variables to corresponding TextFields
            ((TextField) addTaskVBox.getChildren().get(1)).setText(title);
            ((TextField) addTaskVBox.getChildren().get(3)).setText(description);
            // Set datName variable to corresponding TextField
            ((TextField) dayNameHBox.getChildren().get(1)).setText(dayName);
        });

        saveEditButton.setOnAction(e -> {
            toDoList.getTask(index).editDetails(
                    ((TextField) addTaskVBox.getChildren().get(1)).getText(),
                    ((TextField) addTaskVBox.getChildren().get(3)).getText(),
                    ((TextField) dayNameHBox.getChildren().get(1)).getText()
            );

            // Clear title and description text in corresponding TextFields
            ((TextField) addTaskVBox.getChildren().get(1)).clear();
            ((TextField) addTaskVBox.getChildren().get(3)).clear();
            // Clear text in corresponding TextField
            ((TextField) dayNameHBox.getChildren().get(1)).clear();

            saveEditButton.setDisable(true);
            tasksVBox.getChildren().clear();
            numberOfTasksAdded = 0;
            this.loadTasksToBoxes();
        });

        VBox detailsBox = new VBox(titleLabel, descriptionLabel, dayNameLabel);
        HBox buttonBox = new HBox(completeButton, editButton, deleteButton, saveEditButton);
        buttonBox.setSpacing(5);
        VBox taskBox = new VBox(detailsBox, buttonBox);
        taskBox.setStyle("-fx-border-color: black; -fx-padding: 10px;");
        taskBox.setSpacing(5);

        return taskBox;
    }

    private VBox createTaskBox(String title, String description, LocalDate dueDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Label titleLabel = new Label("Title: " + title);
        Label descriptionLabel = new Label("Description: " + description);
        Label dueDateLabel = new Label("Due date: " + dueDate.format(dateTimeFormatter));
        int index = numberOfTasksAdded;
        numberOfTasksAdded++;

        Button completeButton = new Button("Complete");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button saveEditButton = new Button("Save Edit");
        saveEditButton.setDisable(true);

        completeButton.setOnAction(e ->{
            toDoList.getTask(index).toggleCompleted();
        });

        deleteButton.setOnAction(e -> {
            toDoList.deleteTask(toDoList.getTask(index));
            tasksVBox.getChildren().remove(deleteButton.getParent().getParent());
        });

        editButton.setOnAction(e -> {
            saveEditButton.setDisable(false);
            // Set title and description variables to corresponding TextFields
            ((TextField) addTaskVBox.getChildren().get(1)).setText(title);
            ((TextField) addTaskVBox.getChildren().get(3)).setText(description);
            // Set date variables to corresponding TextFields
            ((TextField) dateInputHBox.getChildren().get(1)).setText(((Integer) dueDate.getDayOfMonth()).toString());
            ((TextField) dateInputHBox.getChildren().get(3)).setText(((Integer) dueDate.getMonthValue()).toString());
            ((TextField) dateInputHBox.getChildren().get(5)).setText(((Integer) dueDate.getYear()).toString());
        });

        saveEditButton.setOnAction(e -> {
            toDoList.getTask(index).editDetails(
                    ((TextField) addTaskVBox.getChildren().get(1)).getText(),
                    ((TextField) addTaskVBox.getChildren().get(3)).getText(),
                    Integer.parseInt(((TextField) dateInputHBox.getChildren().get(1)).getText()),
                    Integer.parseInt(((TextField) dateInputHBox.getChildren().get(3)).getText()),
                    Integer.parseInt(((TextField) dateInputHBox.getChildren().get(5)).getText())
            );

            // Set title and description variables to corresponding TextFields
            ((TextField) addTaskVBox.getChildren().get(1)).clear();
            ((TextField) addTaskVBox.getChildren().get(3)).clear();
            // Set date variables to corresponding TextFields
            ((TextField) dateInputHBox.getChildren().get(1)).clear();
            ((TextField) dateInputHBox.getChildren().get(3)).clear();
            ((TextField) dateInputHBox.getChildren().get(5)).clear();

            saveEditButton.setDisable(true);
            tasksVBox.getChildren().clear();
            numberOfTasksAdded = 0;
            this.loadTasksToBoxes();
        });

        VBox detailsBox = new VBox(titleLabel, descriptionLabel, dueDateLabel);
        HBox buttonBox = new HBox(completeButton, editButton, deleteButton, saveEditButton);
        buttonBox.setSpacing(5);
        VBox taskBox = new VBox(detailsBox, buttonBox);
        taskBox.setStyle("-fx-border-color: black; -fx-padding: 10px;");
        taskBox.setSpacing(5);

        return taskBox;
    }

    private void loadTasksToBoxes(){
        for (int i = 0; i < toDoList.getListSize(); i++){
            Task task = toDoList.getTask(i);
            if (task instanceof RecurringTask){
                tasksVBox.getChildren().add(this.createTaskBox(task.getTitle(), task.getDescription(),
                        ((RecurringTask) task).getDayName()));
                continue;
            }
            tasksVBox.getChildren().add(this.createTaskBox(task.getTitle(), task.getDescription(),
                    ((NonRecurringTask) task).getDueDate()));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
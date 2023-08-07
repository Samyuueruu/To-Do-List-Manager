module com.samyuueruu.todolist.todolistmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires com.google.gson;
//    requires eu.hansolo.tilesfx;

    opens com.samyuueruu.todolist.todolistmanager to javafx.fxml;
    exports com.samyuueruu.todolist.todolistmanager;
    exports com.samyuueruu.todolist.todolistmanager.model;
    opens com.samyuueruu.todolist.todolistmanager.model to javafx.fxml, com.google.gson;
    exports com.samyuueruu.todolist.todolistmanager.ui;
    opens com.samyuueruu.todolist.todolistmanager.ui to javafx.fxml;
}
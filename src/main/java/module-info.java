module com.example.expensetrackerhomemade {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.expensetrackerhomemade to javafx.fxml;
    exports com.example.expensetrackerhomemade;
}
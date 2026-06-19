module osk {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires transitive javafx.graphics;
    
    requires org.apache.pdfbox;
    requires org.apache.pdfbox.io;
    requires org.json;

    opens osk to javafx.fxml;
    opens osk.app to javafx.fxml;
    exports osk;
}

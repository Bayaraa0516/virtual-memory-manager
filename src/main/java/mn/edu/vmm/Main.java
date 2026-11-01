
package mn.edu.vmm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import mn.edu.vmm.ui.DashboardView;
import mn.edu.vmm.ui.MainController;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        DashboardView dashboardView =
                new DashboardView();

        new MainController(dashboardView);

        Scene scene = new Scene(
                dashboardView,
                1200,
                750
        );

        stage.setTitle("Virtual Memory Manager");

        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

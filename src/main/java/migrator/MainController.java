package migrator;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import migrator.component.PaneRenderer;
import migrator.app.Container;
import migrator.app.Gui;
import migrator.javafx.router.ColumnRoute;
import migrator.javafx.router.CommitRoute;
import migrator.javafx.router.ConnectionsRoute;
import migrator.javafx.router.DatabasesRoute;
import migrator.javafx.router.IndexRoute;
import migrator.javafx.router.MainRenderer;
import migrator.javafx.router.TableRoute;
import migrator.javafx.router.TableViewRoute;
import migrator.router.Router;

public class MainController implements Initializable {
    @FXML protected VBox centerPane;
    @FXML protected VBox leftPane;

    protected Gui gui;
    protected PaneRenderer centePaneRenderer;
    protected PaneRenderer leftPaneRenderer;
    protected Router router;
    protected Container container;

    protected MainController(Container container, Gui gui, Router router) {
        this.container = container;
        this.gui = gui;
        this.router = router;
    }

    private void registerRoutes() {
        MainRenderer mainRenderer = new MainRenderer(this.centerPane, this.leftPane);

        this.router.connect("connections", new ConnectionsRoute(mainRenderer, this.container, this.gui));
        this.router.connect("databases", new DatabasesRoute(mainRenderer, this.container, this.gui));
        this.router.connect("tables", new TableRoute(mainRenderer, this.container, this.gui));
        this.router.connect("tables.view", new TableViewRoute(mainRenderer, this.container, this.gui));
        this.router.connect("column", new ColumnRoute(mainRenderer, this.container, this.gui));
        this.router.connect("index", new IndexRoute(mainRenderer, this.container, this.gui));
        this.router.connect("commit", new CommitRoute(mainRenderer, this.container, this.gui));
        this.router.show("connections");
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.centePaneRenderer = new PaneRenderer(this.centerPane);
        this.leftPaneRenderer = new PaneRenderer(this.leftPane);

        this.registerRoutes();
    }
}
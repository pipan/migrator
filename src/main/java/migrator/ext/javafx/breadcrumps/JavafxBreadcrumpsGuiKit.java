package migrator.ext.javafx.breadcrumps;

import java.util.Arrays;
import java.util.List;

import migrator.app.Container;
import migrator.app.breadcrumps.ActiveRouteBreadcrump;
import migrator.app.breadcrumps.Breadcrump;
import migrator.app.breadcrumps.BreadcrumpsComponent;
import migrator.app.breadcrumps.BreadcrumpsGuiKit;
import migrator.app.breadcrumps.VoidBreadcrump;
import migrator.app.domain.connection.model.Connection;
import migrator.app.domain.database.model.DatabaseConnection;
import migrator.app.domain.project.model.Project;
import migrator.app.domain.table.model.Table;
import migrator.app.router.ActiveRoute;
import migrator.app.router.Route;
import migrator.ext.javafx.component.ViewLoader;

public class JavafxBreadcrumpsGuiKit implements BreadcrumpsGuiKit {
    protected ViewLoader viewLoader;
    protected Container container;

    public JavafxBreadcrumpsGuiKit(ViewLoader viewLoader, Container container) {
        this.viewLoader = viewLoader;
        this.container = container;
    }

    @Override
    public BreadcrumpsComponent createBreadcrumps(List<Breadcrump> breadcrumps) {
        return new JavafxBreadcrumpsComponent(breadcrumps, this.viewLoader);
    }

    @Override
    public BreadcrumpsComponent createBreadcrumps(Connection connection) {
        return this.createBreadcrumps(
            Arrays.asList(
                new ActiveRouteBreadcrump(
                    "Home", 
                    new Route("connection.index"), 
                    this.container.getActiveRoute()
                ),
                new VoidBreadcrump(connection.getName())
            )
        );
    }

    @Override
    public BreadcrumpsComponent createBreadcrumps(DatabaseConnection databaseConnection) {
        ActiveRoute activeRoute = this.container.getActiveRoute();
        return this.createBreadcrumps(
            Arrays.asList(
                new ActiveRouteBreadcrump("Home", new Route("connection.index"), activeRoute),
                new ActiveRouteBreadcrump(
                    databaseConnection.getConnection().getName(),
                    new Route("database.index"),
                    activeRoute
                ),
                new VoidBreadcrump(databaseConnection.getDatabase())
            )
        );
    }

    @Override
    public BreadcrumpsComponent createBreadcrumps(Table table) {
        ActiveRoute activeRoute = this.container.getActiveRoute();
        return this.createBreadcrumps(
            Arrays.asList(
                new ActiveRouteBreadcrump("Home", new Route("connection.index"), activeRoute),
                new ActiveRouteBreadcrump(
                    table.getDatabase().getConnection().getName(),
                    new Route("database.index"),
                    activeRoute
                ),
                new ActiveRouteBreadcrump(
                    table.getDatabase().getDatabase(),
                    new Route("table.index"),
                    activeRoute
                ),
                new VoidBreadcrump(table.nameProperty())
            )
        );
    }

    @Override
    public BreadcrumpsComponent createBreadcrumps(Project project) {
        return this.createBreadcrumps(
            Arrays.asList(
                new ActiveRouteBreadcrump(
                    "Projects", 
                    new Route("project.index"), 
                    this.container.getActiveRoute()
                ),
                new VoidBreadcrump(project.nameProperty())
            )
        );
    }
}
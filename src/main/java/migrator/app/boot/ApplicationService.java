package migrator.app.boot;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import migrator.app.domain.column.service.ColumnService;
import migrator.app.domain.index.service.IndexService;
import migrator.app.domain.modification.ModificationService;
import migrator.app.domain.project.service.ProjectService;
import migrator.app.domain.project.versionmigration.DatabasePasswordEncrypt;
import migrator.app.domain.table.service.TableService;
import migrator.app.extension.ExtensionService;
import migrator.app.gui.route.Route;
import migrator.app.gui.route.SimpleRoute;
import migrator.app.service.Service;
import migrator.app.version.VersionService;
import migrator.ext.flyway.FlywayExtension;
import migrator.ext.mariadb.MariadbExtension;
import migrator.ext.mysql.MysqlExtension;
import migrator.ext.phinx.PhinxExtension;
import migrator.ext.php.PhpExtension;
import migrator.ext.postgresql.PostgresqlExtension;
import migrator.ext.sentry.SentryExtension;
import migrator.ext.sql.SqlExtension;
import migrator.lib.dispatcher.SimpleEvent;
import migrator.lib.version.Version;

public class ApplicationService implements Service {
    private List<Service> services;
    private Container container;
    private Service userInterface;

    public ApplicationService(Container container) {
        this.container = container;
        this.services = new LinkedList<>();

        this.container.versionContainer()
            .addVersionMigration(
                new Version("0.5.0"),
                new DatabasePasswordEncrypt(
                    container.securityContainer().encryption(),
                    container.filesystem()
                )
            );

        this.services.add(
            new VersionService(container)
        );
        this.services.add(
            new ModificationService(container)
        );
        this.services.add(
            new ProjectService(container)
        );
        this.services.add(
            new ColumnService(container)
        );
        this.services.add(
            new IndexService(container)
        );
        this.services.add(
            new TableService(container)
        );

        this.services.add(
            new ExtensionService(Arrays.asList(
                new MysqlExtension(container),
                new MariadbExtension(container),
                new PostgresqlExtension(container),
                new PhinxExtension(container),
                new FlywayExtension(container),
                new PhpExtension(container),
                new SqlExtension(container),
                new SentryExtension(
                    container,
                    container.configContainer().enviroment().getProperties("sentry")
                )
            ))
        );
    }

    public void bindUserInterface(Service userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public void start() {
        for (Service service : this.services) {
            service.start();
        }

        if (this.userInterface != null) {
            this.userInterface.start();
        }

        this.container.dispatcher().dispatch(
            new SimpleEvent<Route>("route.change", new SimpleRoute("projects"))
        );
    }

    @Override
    public void stop() {
        for (Service service : this.services) {
            service.stop();
        }

        if (this.userInterface != null) {
            this.userInterface.stop();
        }
    }
}
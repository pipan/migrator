package migrator.ext.mysql;

import migrator.app.database.driver.DatabaseDriverConfig;

import migrator.app.ConfigContainer;
import migrator.app.extension.Extension;

public class MysqlExtension implements Extension {
    @Override
    public void load(ConfigContainer config) {
        DatabaseDriverConfig databaseDriverConfig = config.getDatabaseDriverConfig();

        config.databseContainerConfig().get()
            .addStrucutreFactory("mysql", new MysqlDatabaseStructureFactory());

        databaseDriverConfig.addDriver("mysql", new MysqlDatabaseDriverFactory(
            config.tableFactoryConfig().get(),
            config.columnFactoryConfig().get(),
            config.indexFactoryConfig().get(),
            config.loggerConfig(),
            config.columnRepositoryConfig().get()
        ));
    }
}
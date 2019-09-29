package migrator.phpphinx;

import org.junit.jupiter.api.Test;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import migrator.migration.ChangeCommand;
import migrator.migration.ColumnChange;
import migrator.migration.IndexChange;
import migrator.migration.SimpleColumnChange;
import migrator.migration.SimpleColumnProperty;
import migrator.migration.TableChange;
import migrator.phpphinx.PhinxMigration;
import migrator.phpphinx.mock.FileStorage;

public class PhinxMigrationTest {
    protected PhinxMigration migrator;
    protected FileStorage storage;

    protected Map<String, Observable> createArguments(Object[] ... argsPair) {
        Map<String, Observable> args = new Hashtable<>();
        for (Object[] pair : argsPair) {
            args.put((String) pair[0], new SimpleObjectProperty(pair[1]));
        }
        return args;
    }

    @BeforeEach
    public void setUp() {
        this.storage = new FileStorage();
        this.migrator = new PhinxMigration(this.storage, new PhpCommandFactory());
    }

    @Test public void testPhpMigrationCreateTableWithColumn() {
        TableChange change = new TableChange(
            "table_name", 
            new ChangeCommand("create"),
            Arrays.asList(
                new SimpleColumnChange(
                    "column_name",
                    new SimpleColumnProperty("column_name", "string", null, false),
                    new ChangeCommand("create")
                )
            )
        );
        this.migrator.create(change);
        assertEquals(
            "$this->table('table_name')\n" +
                "\t->addColumn('column_name', 'string')\n" +
                "\t->save();\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationUpdateTableRenameTable() {
        Map<String, Observable> args = this.createArguments(
            new Object[]{"name", "new_table_name"}
        );

        TableChange change = new TableChange(
            "table_name", 
            new ChangeCommand("update", args)
        );
        this.migrator.create(change);
        assertEquals(
            "$this->table('table_name')\n" +
                "\t->renameTable('new_table_name')\n" +
                "\t->update();\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationUpdateTableAddColumn() {
        TableChange change = new TableChange(
            "table_name", 
            new ChangeCommand("update"),
            Arrays.asList(
                new SimpleColumnChange(
                    "column_name",
                    new SimpleColumnProperty("column_name", "column_format", null, false),
                    new ChangeCommand("create")
                )
            )
        );
        this.migrator.create(change);
        assertEquals(
            "$this->table('table_name')\n" +
                "\t->addColumn('column_name', 'column_format')\n" +
                "\t->update();\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationUpdateTableRemoveColumn() {
        TableChange change = new TableChange(
            "table_name", 
            new ChangeCommand("update"),
            Arrays.asList(
                new SimpleColumnChange(
                    "column_name",
                    new SimpleColumnProperty("column_name", null, null, false),
                    new ChangeCommand("delete")
                )
            )
        );
        this.migrator.create(change);
        assertEquals(
            "$this->table('table_name')\n" + 
                "\t->removeColumn('column_name')\n" +
                "\t->update();\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationUpdateTableRenameColumn() {
        TableChange change = new TableChange(
            "table_name", 
            new ChangeCommand("update"),
            Arrays.asList(
                new SimpleColumnChange(
                    "column_name",
                    new SimpleColumnProperty("new_column_name", null, null, false),
                    new ChangeCommand("update")
                )
            )
        );
        this.migrator.create(change);
        assertEquals(
            "$this->table('table_name')\n" +
                "\t->renameColumn('column_name', 'new_column_name')\n" +
                "\t->update();\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationDeleteTable() {
        TableChange change = new TableChange("table_name", new ChangeCommand("delete"));
        this.migrator.create(change);
        assertEquals(
            "$this->dropTable('table_name');\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationCreateTableWithColumnAndIndex() {
        Map<String, Observable> args = this.createArguments(
            new Object[]{"columns", Arrays.asList("id", "name")}
        );

        TableChange change = new TableChange(
            "table_name", 
            new ChangeCommand("create"),
            Arrays.asList(
                new SimpleColumnChange(
                    "id",
                    new SimpleColumnProperty("id", "integer", null, false),
                    new ChangeCommand("create")
                ),
                new SimpleColumnChange(
                    "name",
                    new SimpleColumnProperty("name", "string", null, false),
                    new ChangeCommand("create")
                )
            ),
            Arrays.asList(
                new IndexChange("id_name", new ChangeCommand("create", args))
            )
        );
        this.migrator.create(change);
        assertEquals(
            "$this->table('table_name')\n" +
                "\t->addColumn('id', 'integer')\n" +
                "\t->addColumn('name', 'string')\n" +
                "\t->addIndex(['id', 'name'], ['name' => 'id_name'])\n" +
                "\t->save();\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationCreateTableWithColumnRemoveIndex() {
        TableChange change = new TableChange(
            "table_name", 
            new ChangeCommand("create"),
            new ArrayList(),
            Arrays.asList(
                new IndexChange("id_name", new ChangeCommand("delete"))
            )
        );
        this.migrator.create(change);
        assertEquals(
            "$this->table('table_name')\n" +
                "\t->removeIndexByName('id_name')\n" +
                "\t->save();\n",
            this.storage.load()
        );
    }
}

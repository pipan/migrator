package migrator.app.domain.table.service;

import java.util.Collection;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import migrator.app.domain.change.service.ChangeService;
import migrator.app.domain.table.model.Column;
import migrator.app.domain.table.model.Table;
import migrator.app.migration.model.ChangeCommand;
import migrator.app.migration.model.TableChange;

public class ColumnService {
    protected ChangeService changeService;
    protected ObjectProperty<Table> activeTable;
    protected ColumnFactory columnFactory;
    protected ObservableList<Column> list;
    protected ObjectProperty<Column> selected;

    public ColumnService(ColumnFactory columnFactory, ChangeService changeService, ObjectProperty<Table> activeTable) {
        this.columnFactory = columnFactory;
        this.changeService = changeService;
        this.activeTable = activeTable;
        this.list = FXCollections.observableArrayList();
        this.selected = new SimpleObjectProperty<>();

        this.list.addListener((Change<? extends Column> c) -> {
            this.onListChange();
        });
    }

    protected void onListChange() {
        if (this.selected.get() == null) {
            return;
        }
        if (this.list.contains(this.selected.get())) {
            return;
        }
        this.select(null);
    }

    public ObservableList<Column> getList() {
        return this.list;
    }

    public ObjectProperty<Column> getSelected() {
        return this.selected;
    }

    public void select(Column column) {
        this.selected.set(column);
    }

    public void remove(Column column) {
        if (column.getChangeCommand().isType(ChangeCommand.CREATE)) {
            this.list.remove(column);
        } else {
            column.delete();
        }
    }

    public void add(Column column) {
        this.list.add(column);
    }

    public void setAll(Collection<Column> columns) {
        this.list.setAll(columns);
    }

    public ColumnFactory getFactory() {
        return this.columnFactory;
    }

    protected void register(Column column, Table table) {
        this.add(column);
        String dbName = table.getDatabase().getConnection().getName() + "." + table.getDatabase().getDatabase();
        TableChange tableChange = this.changeService.getTableChange(dbName, table.getOriginalName());
        tableChange.getColumnsChanges().add(column.getChange());
    }

    public void register(Column column) {
        this.register(column, this.activeTable.get());
    }

    protected void unregister(Column column, Table table) {
        String dbName = table.getDatabase().getConnection().getName() + "." + table.getDatabase().getDatabase();
        TableChange tableChange = this.changeService.getTableChange(dbName, table.getOriginalName());
        tableChange.getColumnsChanges().remove(column.getChange());
        this.remove(column);
    }

    public void unregister(Column column) {
        this.unregister(column, this.activeTable.get());
    }
}
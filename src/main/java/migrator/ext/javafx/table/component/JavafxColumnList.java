package migrator.ext.javafx.table.component;

import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import migrator.app.Container;
import migrator.app.domain.change.service.ChangeService;
import migrator.app.domain.table.component.ColumnList;
import migrator.app.domain.table.model.Column;
import migrator.app.domain.table.service.ColumnService;
import migrator.app.router.ActiveRoute;
import migrator.ext.javafx.component.ViewComponent;
import migrator.ext.javafx.component.ViewLoader;

public class JavafxColumnList extends ViewComponent implements ColumnList {
    protected ColumnService columnService;
    protected ChangeService changeService;
    protected ActiveRoute activeRoute;

    @FXML protected TableView<Column> columns;

    public JavafxColumnList(ViewLoader viewLoader, Container container) {
        super(viewLoader);
        this.columnService = container.getColumnService();
        this.changeService = container.getChangeService();
        this.activeRoute = container.getActiveRoute();
        
        this.loadView("/layout/table/column/index.fxml");

        this.columnService.getList().addListener((Change<? extends Column> change) -> {
            this.draw();
        });
    }

    protected void draw() {
        if (this.columns == null) {
            return;
        }
        List<Column> columns = this.columnService.getList();
        this.columns.setPrefHeight((40) * (columns.size() + 1));
        this.columns.getItems().setAll(columns);
    }

    @FXML
    public void initialize() {
        this.columns.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.columns.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> obs, Number oldSelection, Number newSelection) -> {
            Column selectedColumn = this.columns.getSelectionModel().getSelectedItem();
            this.activeRoute.changeTo("column.edit", selectedColumn);
        });
        this.draw();
    }

    @FXML
    public void addColumn() {
        Column newColumn = this.columnService.getFactory()
            .createWithCreateChange("new_column");
        this.columnService.register(newColumn);
        this.columnService.select(newColumn);
        this.columns.getSelectionModel().select(newColumn);
        this.activeRoute.changeTo("column.edit", newColumn);
    }
}
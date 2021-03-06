package migrator.app.domain.project.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import migrator.app.domain.database.model.DatabaseConnection;

public class Project implements Serializable {
    private static final long serialVersionUID = 8438644134414504238L;

    protected DatabaseConnection databaseConnection;
    protected transient StringProperty name;
    protected transient StringProperty outputType;
    protected transient StringProperty folder;
    protected String id;

    protected transient BooleanProperty disabled;
    protected transient BooleanProperty focused;

    public Project(DatabaseConnection databaseConnection, String id, String name, String outputType, String folder) {
        this.databaseConnection = databaseConnection;
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.outputType = new SimpleStringProperty(outputType);
        this.folder = new SimpleStringProperty(folder);

        this.initialize();
    }

    private void initialize() {
        this.disabled = new SimpleBooleanProperty();
        this.focused = new SimpleBooleanProperty();
    }

    public String getId() {
        return this.id;
    }

    public DatabaseConnection getDatabase() {
        return this.databaseConnection;
    }

    public StringProperty nameProperty() {
        return this.name;
    }

    public String getName() {
        return this.nameProperty().get();
    }

    public StringProperty outputTypeProperty() {
        return this.outputType;
    }

    public String getOutputType() {
        return this.outputTypeProperty().get();
    }

    public StringProperty folderProperty() {
        return this.folder;
    }

    public String getFolder() {
        return this.folderProperty().get();
    }

    public void focus() {
        this.focused.set(true);
    }
    public void blur() {
        this.focused.set(false);
    }
    public BooleanProperty focusedProperty() {
        return this.focused;
    }

    public void enable() {
        this.disabled.set(false);
    }
    public void disable() {
        this.disabled.set(true);
    }
    public BooleanProperty disabledProperty() {
        return this.disabled;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeUTF(this.name.get());
        s.writeUTF(this.outputType.get());
        s.writeUTF(this.folder.get());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.name = new SimpleStringProperty(s.readUTF());
        this.outputType = new SimpleStringProperty(s.readUTF());
        this.folder = new SimpleStringProperty(s.readUTF());
        
        this.initialize();
    }
}
package migrator.ext.sql.database.column;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrecisionFormatType implements FormatType {
    protected String dbName;
    protected String appName;

    public PrecisionFormatType(String dbName, String appName) {
        this.dbName = dbName;
        this.appName = appName;
    }

    @Override
    public boolean matchesConcretize(String format) {
        return format.startsWith(this.dbName);
    }

    @Override
    public boolean matchesGeneralize(String format) {
        return format.startsWith(this.appName);
    }

    @Override
    public List<String> concretize(String item) {
        List<String> result = new LinkedList<>();
        result.add(this.appName);
        result.add(this.getLength(item));
        result.add(this.getPrecision(item));
        return result;
    }

    @Override
    public String generalize(List<String> item) {
        return this.dbName + "(" + item.get(1) + "," + item.get(2) + ")";
    }

    private String getLength(String dbFormat) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(dbFormat);
        if (m.find()) {
            return m.group(0);
        }

        return "";
    }

    private String getPrecision(String dbFormat) {
        Pattern p = Pattern.compile(",(\\d+)");
        Matcher m = p.matcher(dbFormat);
        if (m.find()) {
            return m.group(1);
        }

        return "";
    }
} 
package cucumber;

import gherkin.formatter.model.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cucumber.runtime.transformers.Transformer;

public class Table {

    private final List<List<String>> raw;
    private Map<String, Transformer<?>> columnTransformers = new HashMap<String, Transformer<?>>();

    public Table(List<Row> gherkinRows) {
        this.raw = new ArrayList<List<String>>();
        for (Row row : gherkinRows) {
            List<String> list = new ArrayList<String>();
            list.addAll(row.getCells());
            this.raw.add(list);
        }
    }

    /**
     * 
     * @return the headers of the table (first <i>raw</i> row with labels)
     */
    public List<String> getHeaders() {
        return this.raw.get(0);
    }

    public List<List<String>> raw() {
        return this.raw;
    }

    public List<List<String>> rows() {
        return this.raw.subList(1, this.raw.size());
    }
    
    public List<Map<String, Object>> hashes() {
        List<Map<String, Object>> hashes = new ArrayList<Map<String, Object>>();
        List<String> headers = getHeaders();
        List<List<String>> rows = rows();
        for (List<String> row : rows) {
            Map<String, Object> map = new HashMap<String, Object>();
            for(int i=0;i<row.size();i++) {
                String header = headers.get(i);
                Object hashValue = transformCellValue(header, row.get(i));
                map.put(header, hashValue);
            }
            hashes.add(map);
        }
        return hashes;
    }

    private Object transformCellValue(String header, String cellValue) {
        Object hashValue;
        Transformer<?> transformer = this.columnTransformers.get(header);
        if(transformer!=null) {
            //TODO: How to get Locale from here?
            hashValue = transformer.transform(Locale.getDefault(), cellValue);
        } else {
            hashValue = cellValue;
        }
        return hashValue;
    }
    
    public void mapColumn(String column, Transformer<?> transformer) {
        this.columnTransformers.put(column, transformer);
    }
}
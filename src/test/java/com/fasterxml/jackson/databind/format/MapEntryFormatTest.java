package com.fasterxml.jackson.databind.format;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.*;

public class MapEntryFormatTest extends BaseMapTest
{
    static class BeanWithMapEntry {
        // would work with any other shape than OBJECT, or without annotation:
        @JsonFormat(shape=JsonFormat.Shape.NATURAL)
        public Map.Entry<String,String> entry;

        protected BeanWithMapEntry() { }
        public BeanWithMapEntry(String key, String value) {
            Map<String,String> map = new HashMap<>();
            map.put(key, value);
            entry = map.entrySet().iterator().next();
        }
    }

    @JsonFormat(shape=JsonFormat.Shape.OBJECT)
    static class MapEntryAsObject implements Map.Entry<String,String> {
        protected String key, value;

        protected MapEntryAsObject() { }
        public MapEntryAsObject(String k, String v) {
            key = k;
            value = v;
        }
        
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String v) {
            value = v;
            return v; // wrong, whatever
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testAsNaturalRoundtrip() throws Exception
    {
        BeanWithMapEntry input = new BeanWithMapEntry("foo" ,"bar");
        String json = MAPPER.writeValueAsString(input);
        assertEquals(aposToQuotes("{'entry':{'foo':'bar'}}"), json);
        BeanWithMapEntry result = MAPPER.readValue(json, BeanWithMapEntry.class);
        assertEquals("foo", result.entry.getKey());
        assertEquals("bar", result.entry.getValue());
    }
    // should work via class annotation
    public void testAsObjectRoundtrip() throws Exception
    {
        MapEntryAsObject input = new MapEntryAsObject("foo" ,"bar");
        String json = MAPPER.writeValueAsString(input);
        assertEquals(aposToQuotes("{'key':'foo','value':'bar'}"), json);

        // 16-Oct-2016, tatu: Happens to work by default because it's NOT basic
        //   `Map.Entry` but subtype.
        
        MapEntryAsObject result = MAPPER.readValue(json, MapEntryAsObject.class);
        assertEquals("foo", result.getKey());
        assertEquals("bar", result.getValue());
    }
}

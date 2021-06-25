package com.example.demo.utils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CsvHandle {
    /**
     * 从csv文件读取实体类
     *
     * @param <T>
     * @param type
     * @param fileName
     * @return
     */
    public static <T> List<T> loadObjectList(Class<T> type, String fileName) throws Exception{
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();
        File file = new File(fileName);
        MappingIterator<T> readValues = mapper.readerFor(type).with(bootstrapSchema).readValues(file);
        return readValues.readAll();
    }

    /**
     * 把实体类写入到csv文件
     *
     * @param <T>
     * @param list
     * @param csvFile
     * @param clazz
     * @throws IOException
     */
    public static <T> void objectList2CSV(List<T> list, File csvFile, Class<T> clazz) throws Exception{
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(clazz);
        ObjectWriter writer = mapper.writer(schema.withLineSeparator("\n").withHeader());
        writer.writeValue(csvFile, list);
    }
}

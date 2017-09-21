package com.chh.dc.icp.parser.obd;

import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.parser.AbstractParser;
import com.chh.dc.icp.parser.obd.reader.ByteArrayReader;
import com.chh.dc.icp.parser.obd.reader.vk.*;
import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * vk 解析器
 * 版本 2.0.1.69
 * Created by 申卓 on 2017/9/4.
 */
public class VKGPSParser extends AbstractParser {

    public static final Logger log = LoggerFactory.getLogger(VKGPSParser.class);

    /**
     * 功能类型  对应reader
     */
    private Map<Character, ByteArrayReader> typeReaderMap = new HashMap();
    private List<ParsedRecord> list = null;

    {
        typeReaderMap.put('A',new CanReader());
        typeReaderMap.put('B',new LocateReader());
        typeReaderMap.put('D',new DynamicLoadReader());
        typeReaderMap.put('O',new OBDReader());
        typeReaderMap.put('G',new PhraseInfoReader());
    }

    @Override
    public void parse(TaskInfo taskInfo, DataPackage data) throws Exception {
        byte[] bs = (byte[]) data.getData();

        char featureId = VKReader.readFeatureId(bs);
        log.info("收取到VK数据，判断得到功能类型编码为{}", featureId);
        ByteArrayReader reader = typeReaderMap.get(featureId);
        if (reader == null) {
            log.info("没有找到{}的读取器" ,featureId);
            return;
        }
        this.list = reader.readRecord(bs);
        log.info("解析得到数据条数{}", list == null ? 0 : list.size());
    }

    @Override
    public ParsedRecord readRecord() throws Exception {
        if (list == null || list.isEmpty()) {
            return null;
        }
        //只能读取一次
        ParsedRecord record = list.remove(0);
        return record;
    }

    @Override
    public void beforeParse(TaskInfo taskInfo, DataPackage data) throws Exception {

    }

    @Override
    public void afterParse() throws Exception {

    }
}

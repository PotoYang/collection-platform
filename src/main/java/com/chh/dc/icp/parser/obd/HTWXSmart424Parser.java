package com.chh.dc.icp.parser.obd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chh.dc.icp.accessor.DataPackage;
import com.chh.dc.icp.db.pojo.TaskInfo;
import com.chh.dc.icp.parser.AbstractParser;
import com.chh.dc.icp.parser.obd.reader.ByteArrayReader;
import com.chh.dc.icp.parser.obd.reader.htwx.SnapReader;
import com.chh.dc.icp.warehouse.ParsedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chh.dc.icp.parser.obd.reader.htwx.AlarmReader;
import com.chh.dc.icp.parser.obd.reader.htwx.CANReader;
import com.chh.dc.icp.parser.obd.reader.htwx.FaultReader;
import com.chh.dc.icp.parser.obd.reader.htwx.GPSReader;
import com.chh.dc.icp.parser.obd.reader.htwx.HTWXReader;

/**
 * 航天无线 NEW OBD SMART  通讯 协议<br/>
 * 4.24版本，解析器<br/>
 * Created by Niow on 2016/8/26.
 */
public class HTWXSmart424Parser extends AbstractParser {

    public static final Logger log = LoggerFactory.getLogger(HTWXSmart424Parser.class);

    private Map<String, ByteArrayReader> readerMap = new HashMap<String, ByteArrayReader>();
    private List<ParsedRecord> list = null;

    {
        readerMap.put("4001", new GPSReader());
        readerMap.put("4002", new CANReader());
        readerMap.put("4005", new SnapReader());
        readerMap.put("4006", new FaultReader());
        readerMap.put("4007", new AlarmReader());
    }

    public void init() {
        //加载所需数据
//    	OBDAlarmCodeConverter.loadData();
    }

    @Override
    public void parse(TaskInfo taskInfo, DataPackage data) throws Exception {
        byte[] bs = (byte[]) data.getData();
        String featureId = HTWXReader.readFeatureId(bs);
        log.info("收取到HTWX数据，判断得到功能类型为{}", featureId);
        ByteArrayReader reader = readerMap.get(featureId);
        if (reader == null) {
            log.info("没有找到{}的读取器" ,featureId);
            return;
        }
        this.list = reader.readRecord(bs);
        log.info("解析得到数据条数{}", list == null ? 0 : list.size());
    }


    @Override
    public ParsedRecord readRecord() {
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

package com.chh.dc.icp.parser.obd.reader.dna;

import com.chh.dc.icp.parser.obd.reader.FieldInfo;
import com.chh.dc.icp.warehouse.ParsedRecord;

import java.util.*;

import static com.chh.dc.icp.util.ByteReaderUtil.readBit;

/**
 * Created by Niow on 2016/8/9.
 */
public class CANReader extends DNAReader {


    private Map<Integer, FieldInfo> statusFieldMap = new HashMap<Integer, FieldInfo>();
    private Map<Integer, FieldInfo> dsFieldMap = new HashMap<Integer, FieldInfo>();

    public CANReader() {
        init();
    }


    private void init() {
        statusFieldMap.put(4, new FieldInfo("security_status", 2, 0.1));
        statusFieldMap.put(5, new FieldInfo("lock_status", 2, 0.1));
        statusFieldMap.put(6, new FieldInfo("window_status", 2));
        statusFieldMap.put(7, new FieldInfo("door)_status", 1));

        dsFieldMap.put(1, new FieldInfo("lamp_status", 1));
        dsFieldMap.put(2, new FieldInfo("dtcs", 1));
        dsFieldMap.put(3, new FieldInfo("obd_requirement", 1));
        dsFieldMap.put(4, new FieldInfo("vehicle_voltage", 2, 0.1));
        dsFieldMap.put(5, new FieldInfo("engine_rpm", 2));
        dsFieldMap.put(6, new FieldInfo("vehicle_speed", 1));
        dsFieldMap.put(7, new FieldInfo("intake_air_temperature", 1));
        dsFieldMap.put(8, new FieldInfo("engine_coolant_temperature", 1));
        dsFieldMap.put(9, new FieldInfo("environment_temperature", 1));
        dsFieldMap.put(10, new FieldInfo("intake_manifold_pressure", 1));
        dsFieldMap.put(11, new FieldInfo("fuel_pressure", 2));
        dsFieldMap.put(12, new FieldInfo("barometric_pressure", 1));
        dsFieldMap.put(13, new FieldInfo("air_flow_sensor", 2, 0.1));
        dsFieldMap.put(14, new FieldInfo("throttle_position_sensor", 2, 0.1));
        dsFieldMap.put(15, new FieldInfo("accelerator_pedal_position", 2, 0.1));
        dsFieldMap.put(16, new FieldInfo("engine_run_time", 2));
        dsFieldMap.put(17, new FieldInfo("fault_vehicle_mileage", 4));
        dsFieldMap.put(18, new FieldInfo("oil_mass_fuel_level", 2, 0.1));
        dsFieldMap.put(19, new FieldInfo("calculated_engine_load", 1));
        dsFieldMap.put(20, new FieldInfo("long_term_fuel_trim_bank_1", 2, 0.1));
        dsFieldMap.put(21, new FieldInfo("spark_angle_before_tdc", 2, 0.1));
        dsFieldMap.put(22, new FieldInfo("panel_mileage", 4));
        dsFieldMap.put(34, new FieldInfo("tire_pressue_lf", 2));
        dsFieldMap.put(35, new FieldInfo("tire_pressue_rf", 2));
        dsFieldMap.put(36, new FieldInfo("tire_pressue_lb", 2));
        dsFieldMap.put(37, new FieldInfo("tire_pressue_rb", 2));
        dsFieldMap.put(38, new FieldInfo("mileage_fuel_flag", 1));
        dsFieldMap.put(39, new FieldInfo("total_mileage", 4));
        dsFieldMap.put(40, new FieldInfo("total_fuel_consumption", 4));

    }

    @Override
    public List<ParsedRecord> readRecord(byte[] bs) {
        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
        ParsedRecord record = new ParsedRecord();
        list.add(record);
        record.setType("dna_can");
        Map<String, Object> map = record.getRecord();
        String id = readDeviceId(bs);
        map.put("device_id", id);
        map.put("collection_time", new Date());
        int dataLength = readDataLength(bs);
        if (dataLength < 1) {
            return list;
        }
        Date date = readDateTime(bs, INDEX_DATA);
        map.put("time_stamp", date);

        //index = 数据开始位置+时间字节数+statusMask字节数+dsMash字节数+PackNumber字节数
        int index = INDEX_DATA + 6 + 1 + 5 + 1;
        index = readStatus(bs, index, map);
        readDS(bs, index, map);

        return list;
    }

    private List<Integer> readToBitArray(byte[] bs, boolean isReserve) {
        List<Integer> integerArrayList = new ArrayList<Integer>();
        for (byte b : bs) {
            if (isReserve) {
                for (int i = 7; i >= 0; i--) {
                    int bit = readBit(b, i);
                    integerArrayList.add(bit);
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    int bit = readBit(b, i);
                    integerArrayList.add(bit);
                }
            }
        }
        return integerArrayList;
    }

    protected int readStatus(byte[] bs, int index, Map<String, Object> map) {
        byte[] statusMask = Arrays.copyOfRange(bs, INDEX_DATA + 6, INDEX_DATA + 6);
        List<Integer> statusMaskList = readToBitArray(statusMask, false);
        for (int i = 0; i < statusMaskList.size(); i++) {
            Integer mask = statusMaskList.get(i);
            if (mask < 1) {
                continue;
            }
            FieldInfo dsField = statusFieldMap.get(i);
            switch (i) {
                case 4: {
                    readSecurityStatus(bs, index, map);
                    break;
                }
                case 5: {
                    readLockStatus(bs, index, map);
                    break;
                }
                case 6: {
                    readWindowStatus(bs, index, map);
                    break;
                }
                case 7: {
                    readDoorStatus(bs, index, map);
                    break;
                }
            }
            index += dsField.getType();
        }
        return index;
    }

    /**
     * 读取DS数据大项，根据DS MASK中的掩码来判断是否存在对于的字段数据
     *
     * @param bs
     * @param map
     */
    protected int readDS(byte[] bs, int index, Map<String, Object> map) {
        byte[] dsMask = Arrays.copyOfRange(bs, INDEX_DATA + 7, INDEX_DATA + 12);
        List<Integer> dsMaskList = readToBitArray(dsMask, false);
        for (int i = 0; i < dsMaskList.size(); i++) {
            Integer mask = dsMaskList.get(i);
            if (mask < 1) {
                continue;
            }
            FieldInfo dsField = dsFieldMap.get(i + 1);
            readData(bs, index, dsField, map);
            index += dsField.getType();
        }
        return index;
    }


    protected void readDoorStatus(byte[] bs, int index, Map<String, Object> map) {
        byte status = bs[index];
        map.put("dor_lf_status", readBit(status, 7));
        map.put("door_rf_status", readBit(status, 6));
        map.put("door_lb_status", readBit(status, 5));
        map.put("door_rb_status", readBit(status, 4));
        map.put("door_trunk_status", readBit(status, 3));
    }

    protected void readWindowStatus(byte[] bs, int index, Map<String, Object> map) {

        byte status = bs[index];
        map.put("window_lf_status", readBit(status, 7));
        map.put("window_rf_status", readBit(status, 6));
        map.put("window_lb_status", readBit(status, 5));
        map.put("window_rb_status", readBit(status, 4));
    }

    protected void readLockStatus(byte[] bs, int index, Map<String, Object> map) {
        byte status = bs[index];
        map.put("door_lf_status", readBit(status, 7));
        map.put("door_rf_status", readBit(status, 6));
        map.put("door_lb_status", readBit(status, 5));
        map.put("door_rb_status", readBit(status, 4));
        map.put("door_trunk_status", readBit(status, 3));
    }

    protected void readSecurityStatus(byte[] bs, int index, Map<String, Object> map) {
        byte status = bs[index];
        map.put("acc_status", readBit(status, 7));
        map.put("security_status", readBit(status, 6));
    }


}

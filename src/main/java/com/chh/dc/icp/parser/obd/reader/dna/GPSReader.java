package com.chh.dc.icp.parser.obd.reader.dna;

import com.chh.dc.icp.warehouse.ParsedRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.chh.dc.icp.util.ByteReaderUtil.readInt;

/**
 * Created by Niow on 2016/7/28.
 */
public class GPSReader extends DNAReader {

    @Override
    public List<ParsedRecord> readRecord(byte[] bs) {
        List<ParsedRecord> list = new ArrayList<ParsedRecord>();
        ParsedRecord record = new ParsedRecord();
        list.add(record);
        record.setType("dna_gps");
        Map<String, Object> map = record.getRecord();
        String id = readDeviceId(bs);
        map.put("id", id);
        map.put("collection_time", new Date());
        int dataLength = readDataLength(bs);
        if (dataLength < 1) {
            return list;
        }
        Date date = readDateTime(bs, INDEX_DATA);
        double latitude = readLatitude(bs);
        double longitude = readLongitude(bs);
        int altitude = readAltitude(bs);
        double speed = readSpeed(bs);
        double direction = readDirection(bs);
        int satellites = readSatellites(bs);
        boolean accStatus = readAccStatus(bs);
        double vehicleBattery = readVehicleBattery(bs);
        map.put("time_stamp", date);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("altitude", altitude);
        map.put("speed", speed);
        map.put("direction", direction);
        map.put("satellites", satellites);
        map.put("acc_status", accStatus);
        map.put("battery_vehicle", vehicleBattery);
        return list;
    }

    protected double readLatitude(byte[] bs) {
        return readU32(bs, INDEX_DATA + 6) * 0.000001;
    }

    protected double readLongitude(byte[] bs) {
        return readU32(bs, INDEX_DATA + 10) * 0.000001;
    }

    protected int readAltitude(byte[] bs) {
        int index = INDEX_DATA + 14;
        int b1 = readInt(bs[index]);
        int b2 = readInt(bs[++index]);
        int b3 = readInt(bs[++index]);
        int rs = (b1 << 8) + (b2 << 4) + b3;
        return rs;
    }

    protected double readSpeed(byte[] bs) {
        return readU16(bs, INDEX_DATA + 17) * 0.1;
    }

    protected double readDirection(byte[] bs) {
        return readU16(bs, INDEX_DATA + 19) * 0.1;
    }

    protected int readSatellites(byte[] bs) {
        return readInt(bs[INDEX_DATA + 21]);
    }

    protected boolean readAccStatus(byte[] bs) {
        int acc = bs[INDEX_DATA + 23] & 0x80;
        return acc > 0;
    }

    protected double readVehicleBattery(byte[] bs) {
        return readU16(bs, INDEX_DATA + 24) * 0.1;
    }

}

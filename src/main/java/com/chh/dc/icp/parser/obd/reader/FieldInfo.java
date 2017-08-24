package com.chh.dc.icp.parser.obd.reader;

public class FieldInfo {

    private String name;
    private int type;
    private Double coefficient;
    private boolean isReverse;
    /**
     * 数组长度<br>
     * 如果该字段值类型为数组类型，这里长度就是数组长度<br>
     * 例如：U8[2],这里长度就是2，默认为1
     */
    private int arrayLength = 1;

    public FieldInfo(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public FieldInfo(String name, int type, double coefficient) {
        this.name = name;
        this.type = type;
        this.coefficient = coefficient;
    }

    public FieldInfo(String name, int type, boolean isReverse) {
        this.name = name;
        this.type = type;
        this.isReverse = isReverse;
    }

    public FieldInfo(String name, int type, double coefficient, boolean isReverse) {
        this.name = name;
        this.type = type;
        this.coefficient = coefficient;
        this.isReverse = isReverse;
    }

    public FieldInfo(String name, int type, int arrayLength) {
        this.name = name;
        this.type = type;
        this.arrayLength = arrayLength;
    }

    public FieldInfo(String name, int type, boolean isReverse, int arrayLength) {
        this.name = name;
        this.type = type;
        this.isReverse = isReverse;
        this.arrayLength = arrayLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public int getArrayLength() {
        return arrayLength;
    }

    public void setArrayLength(int arrayLength) {
        this.arrayLength = arrayLength;
    }
}
package com.jonatasrocha.stock.inventory;

public enum MovementType {

    ENTRY((short) 0), EXIT((short) 1), RESERVE((short) 2);

    private final short value;

    private MovementType(short value) {
        this.value = value;
    }

    public short getValue() {
        return this.value;
    }

    public static MovementType fromValue(Short value) {
        switch (value) {
        case 0:
            return MovementType.ENTRY;
        case 1:
            return MovementType.EXIT;
        case 2:
            return MovementType.RESERVE;
        default:
            return null;
        }
    }

}

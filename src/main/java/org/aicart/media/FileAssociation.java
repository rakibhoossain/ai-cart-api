package org.aicart.media;

public enum FileAssociation {
    PRODUCT(1),
    CATEGORY(2);

    private final int value;

    FileAssociation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

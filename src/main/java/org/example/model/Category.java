package org.example.model;

public enum Category {
    FREEZING, COLD, MILD, WARM, HOT;

    @Override
    public String toString(){
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}

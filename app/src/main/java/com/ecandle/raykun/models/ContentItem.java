package com.ecandle.raykun.models;

/**
 * Created by Philipp Jahoda on 07/12/15.
 */
public class ContentItem {

    public String name;
    public String desc;
    public boolean isNew = false;

    public ContentItem(String n, String d) {
        name = n;
        desc = d;
    }
}

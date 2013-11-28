package dderrien.customportal.model;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import dderrien.common.model.AbstractAuthBase;

@Entity
@Cache
@Index
public class Category extends AbstractAuthBase<Category> {
    @Unindex String title;
    @Unindex String width;
    @Index Long order;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getWidth() {
        return width;
    }
    public void setWidth(String width) {
        this.width = width;
    }
    public Long getOrder() {
        return order;
    }
    public void setOrder(Long order) {
        this.order = order;
    }
}
package dderrien.customportal.model;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import dderrien.common.model.AbstractAuthBase;

@Entity
@Cache
@Index
public class Link extends AbstractAuthBase<Link> {
    Long categoryId;
    @Unindex String title;
    @Unindex String href;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHRef() {
        return href;
    }

    public void setHRef(String href) {
        this.href = href;
    }
}
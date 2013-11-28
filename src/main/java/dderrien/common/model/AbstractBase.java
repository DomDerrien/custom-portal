package dderrien.common.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Unindex;

import dderrien.common.exception.ServerErrorException;

@Index
public abstract class AbstractBase<T> implements Cloneable {

    @Id private Long id;
    @Unindex protected Date creation;

    public AbstractBase() {
        super();
    }

    @WriteOnceField
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @WriteOnceField
    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }
    
    @Override
    @SuppressWarnings({ "unchecked" })
    public String toString() {
        StringBuffer out = new StringBuffer("{ ");
        try {
            BeanUtilsBean beanUtilsInstance = BeanUtilsBean2.getInstance();
            Map<String, Object> describeThis = beanUtilsInstance.describe(this);
            for (String propertyName : describeThis.keySet()) {
                if ("class".equals(propertyName)) {
                    continue;
                }

                PropertyDescriptor propertyDescriptor = beanUtilsInstance.getPropertyUtils().getPropertyDescriptor(this, propertyName);
                Object propertyValue = propertyDescriptor.getReadMethod().invoke(this);

                if (propertyValue == null) {
                    continue;
                }

                out.append("\"").append(propertyName).append("\": ");
                if (propertyValue instanceof Date) {
                    out.append(((Date) propertyValue).getTime());
                }
                else if (propertyValue instanceof Boolean) {
                    out.append(Boolean.TRUE.equals((Boolean) propertyValue) ? "true" : "false");
                }
                else if (propertyValue instanceof Long) {
                    out.append((Long) propertyValue);
                }
                else if (propertyValue instanceof Double) {
                    out.append((Double) propertyValue);
                }
                else  {
                    out.append("\"").append(propertyValue).append("\"");
                }
                out.append(", ");
            }
        }
        catch (Exception ex) {
            out.append("\"ex\": \"").append(ex.getClass().getSimpleName()).append(" in ").append(getClass().getSimpleName()).append(".toString()").append("\", ");
        }
        int lastSeparator = out.lastIndexOf(", ");
        if (lastSeparator != -1) {
        	out.replace(lastSeparator, lastSeparator + ", ".length(), "");
        }
        return out.append(" }").toString();
    }
    
    @Override
    @SuppressWarnings({ "unchecked" })
    public T clone() throws CloneNotSupportedException{
        return (T) super.clone();
    }

    @SuppressWarnings({ "unchecked" })
    public boolean merge(AbstractBase<T> other) {
        try {
            BeanUtilsBean beanUtilsInstance = BeanUtilsBean2.getInstance();

            Map<String, Object> describeThis = beanUtilsInstance.describe(this);

            boolean merged = false;

            for (String propertyName : describeThis.keySet()) {
                PropertyDescriptor propertyDescriptor = beanUtilsInstance.getPropertyUtils().getPropertyDescriptor(this, propertyName);

                Object otherValue = propertyDescriptor.getReadMethod().invoke(other);
                Object thisValue = propertyDescriptor.getReadMethod().invoke(this);

                WriteOnceField writeOnceFieldAnnotation = propertyDescriptor.getReadMethod().getAnnotation(WriteOnceField.class);

                if (writeOnceFieldAnnotation == null && otherValue != null && !isSameValue(otherValue, thisValue)) {
                    propertyDescriptor.getWriteMethod().invoke(this, otherValue);
                    merged = true;
                }
            }

            return merged;

        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new ServerErrorException("Intropsection error when attempting to merge " + this.getClass().getSimpleName() + " entities", ex);
        }
    }

    protected static boolean isSameValue(Object otherValue, Object thisValue) {
        if (otherValue != null) {
            if (otherValue instanceof BigDecimal) { // Comparison because BigDecimal(5.0) != BigDecimal(5)
                return ((BigDecimal) otherValue).compareTo((BigDecimal) thisValue) == 0;
            }
            // Normal case
            return otherValue.equals(thisValue);
        }
        return thisValue == null;
    }

    @OnSave
    protected void prePersist() {
        if (creation == null) {
            creation = new DateTime().toDate();
        }
    }
}
package dderrien.model;

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

import dderrien.exception.ServerErrorException;

@Index
public abstract class AbstractBase<T> implements Cloneable {

    private static final Logger logger = Logger.getLogger(AbstractBase.class.getName());

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
                    out.append(((Date) propertyValue).getTime()).append(", ");
                }
                else if (propertyValue instanceof Boolean) {
                    out.append(Boolean.TRUE.equals((Boolean) propertyValue) ? "true" : "false").append(", ");
                }
                else if (propertyValue instanceof Long) {
                    out.append((Long) propertyValue).append(", ");
                }
                else if (propertyValue instanceof Double) {
                    out.append((Double) propertyValue).append(", ");
                }
                else  {
                    out.append("\"").append(propertyValue).append("\", ");
                }
            }
        }
        catch (Exception ex) {
            out.append("\"ex\": \"").append(ex.getClass().getSimpleName()).append(" -- ").append(ex.getMessage()).append("\", ");
        }
        return out.replace(out.lastIndexOf(", "), out.lastIndexOf(", ") + ", ".length(), "").append(" }").toString();
    }
    
    @Override
    @SuppressWarnings({ "unchecked" })
    public AbstractBase<T> clone() {
        try {
            return (AbstractBase<T>) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new ServerErrorException("Cannot clone entity: " + getClass().getName(), ex);
        }
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
                    try {
                        propertyDescriptor.getWriteMethod().invoke(this, otherValue);
                        beanUtilsInstance.setProperty(this, propertyName, otherValue);
                        merged = true;
                    }
                    catch (IllegalAccessException e) {
                        logger.warning("attempt to copy a non writable property when merging : '" + propertyName + "'");
                    }
                }
            }

            return merged;

        }
        catch (NoSuchMethodException e1) {
            throw new ServerErrorException("intropsection error when attempting to merge " + this.getClass().getSimpleName() + " entities");
        }
        catch (IllegalAccessException e1) {
            throw new ServerErrorException("intropsection error when attempting to merge " + this.getClass().getSimpleName() + " entities");
        }
        catch (InvocationTargetException e1) {
            throw new ServerErrorException("intropsection error when attempting to merge " + this.getClass().getSimpleName() + " entities");
        }
    }

    protected boolean isSameValue(Object otherValue, Object thisValue) {
        if (otherValue != null) {
            if (otherValue instanceof BigDecimal)
                // Comparison because BigDecimal(5.0) != BigDecimal(5)
                return thisValue != null && ((BigDecimal) otherValue).compareTo((BigDecimal) thisValue) == 0;

            // Normal case
            return otherValue.equals(thisValue);
        }
        else
            return thisValue == null;
    }

    @OnSave
    protected void prePersist() {
        if (creation == null) {
            creation = new DateTime().toDate();
        }
    }
}
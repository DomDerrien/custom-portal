package dderrien.common.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import dderrien.common.exception.ClientErrorException;
import dderrien.common.exception.InvalidRangeException;
import dderrien.common.model.AbstractBase;
import dderrien.common.util.IntrospectionHelper;
import dderrien.common.util.Range;

public abstract class AbstractDao<T extends AbstractBase<T>> {

    private static final Logger logger = Logger.getLogger(AbstractDao.class.getName());

    private static Pattern noOperatorPattern = Pattern.compile("^[a-zA-Z0-9_]+$");

    private Class<T> entityClass;

    public Class<T> getModelClass() {
        return entityClass;
    }

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        super();
        entityClass = (Class<T>) IntrospectionHelper.getFirstTypeArgument(this.getClass());
    }

    public Objectify getOfy() {
        return ofy();
    }

    public T get(Long id) {
        if (id == null) {
            throw new ClientErrorException("Cannot look for an entity of class " + getModelClass().getName() + " because the given identifier is null!");
        }
        return getQuery(getModelClass()).id(id).now();
    }

    public T filter(String condition, Object value) {
        if (StringUtils.isEmpty(condition)) {
            throw new ClientErrorException("Cannot look for an entity of class " + getModelClass().getName() + " because the given condition is null!");
        }
        return getQuery(getModelClass()).filter(condition, value).first().now();
    }

    public List<T> select(Map<String, Object> params, Range range, List<String> orders) {
        Query<T> query = getQuery(getModelClass());

        if (params == null) {
            params = new HashMap<String, Object>();
        }

        logger.finest("filters applied to select query on " + getModelClass().getSimpleName() + " : " + params);

        for (String key : params.keySet()) {
            boolean valueProcessed = false;
            Object value = params.get(key);

            if (value == null) {
                continue;
            }

            if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue.length() == 0) {
                    continue;
                }
                if (stringValue.endsWith("*")) {
                    if (stringValue.length() == 1) {
                        continue;
                    }
                    String truncatedStringValue = stringValue.substring(0, stringValue.length() - 1);
                    query = query.filter(key + " >= ", truncatedStringValue).filter(key + " < ", truncatedStringValue + "\uFFFD");
                    valueProcessed = true;
                }
            }
            else if (value instanceof List) {
                List<?> listValue = (List<?>) value;
                if (listValue.size() == 0) {
                    continue;
                }
                if (listValue.size() == 1) {
                    query = query.filter(key + " = ", listValue.get(0));
                }
                else {
                    query = query.filter(key + " IN ", listValue);
                }
                valueProcessed = true;
            }

            if (!valueProcessed) {
                if (noOperatorPattern.matcher(key).matches()) {
                    key = key + " = "; // required for Objectify 4 otherwise defaults to == which yields strange results
                }

                query = query.filter(key, value);
            }
        }

        if (range != null && range.isInitialized()) {
            query = applyRange(query, range);
        }

        if (orders != null && 0 < orders.size()) {
            for (String order : orders) {
                if (order.charAt(0) == '+') {
                    query = query.order(order.substring(1));
                }
                else {
                    query = query.order(order);
                }
            }
        }

        logger.finest("Query: " + query);
        List<T> list = query.list();

        if (range != null && range.isInitialized()) {
            range.setListSize(list.size());
        }

        return list;
    }

    public Key<AbstractBase<T>> save(AbstractBase<T> candidate) {
        return getOfy().save().entity(candidate).now();
    }

    public void delete(Long id) {
        if (id == null) {
            throw new ClientErrorException("Cannot delete an entity of class " + getModelClass().getName() + " because the given identifier is null!");
        }
        getOfy().delete().type(getModelClass()).id(id).now();
    }

    // =========================

    protected LoadType<T> getQuery(Class<T> modelClass) {
        return getOfy().load().type(modelClass);
    }

    protected Query<T> applyRange(Query<T> query, Range range) {
        // Get the total number of matching entity keys
        int total = query.keys().list().size();
        int startRow = range.getStartRow();
        if (startRow > 0 && startRow > total - 1) {
            throw new InvalidRangeException("range header mal formed for scrolling : result set shorter than expected");
        }
        range.setTotal(total);

        // Specify request boundaries
        query = query.offset(startRow);
        Integer count = range.getCount();
        if (count != null) {
            query = query.limit(count);
        }

        return query;
    }

}
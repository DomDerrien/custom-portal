package dderrien.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import dderrien.exception.ClientErrorException;
import dderrien.exception.InvalidRangeException;
import dderrien.model.AbstractBase;
import dderrien.util.IntrospectionHelper;
import dderrien.util.Range;

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

    public T get(Long key) {
        if (key == null) {
            throw new ClientErrorException("Cannot look for an entity of class " + getModelClass().getName() + " because the given key is null!");
        }
        return getQuery(getModelClass()).id(key).now();
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

            key = key.trim();
            if (noOperatorPattern.matcher(key).matches()) {
                key = key + " ="; // required for Objectify 4 otherwise defaults to == which yields strange results
            }

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
            if (!valueProcessed)
                query = query.filter(key, value);
        }

        if (range != null && range.isInitialized()) {
            query = applyRange(query, range);
        }

        if (orders != null && 0 < orders.size()) {
            for(String order: orders) {
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
        if (candidate.getId() == null) {
            candidate.setId(generateEntityId(candidate.getClass()));
            candidate.setCreation(new Date());
        }
        return saveInner(candidate);
    }

    public void delete(Long key) {
        if (key == null) {
            throw new ClientErrorException("Cannot delete an entity of class " + getModelClass().getName() + " because the given key is null!");
        }
        getOfy().delete().entities(get(key)).now();
    }

    /**
     * Id is set manually by reversing a non modulo 10 id provided by the datastore so that writing will very likely impact a different shard during write process
     */
    protected long generateEntityId(Class<?> modelClass) {

        long id;
        do {
            id = ObjectifyService.factory().allocateId(getModelClass()).getId();
        } while (id % 10 == 0);

        Long reversedId = Long.valueOf(StringUtils.reverse(String.valueOf(id)));

        return reversedId;
    }

    public LoadType<T> getQuery(Class<T> modelClass) {

        return getOfy().load().type(modelClass);
    }

    @SuppressWarnings("unchecked")
    public Key<AbstractBase<T>> saveInner(AbstractBase<T> candidate) {
        return getOfy().save().entities(candidate).now().keySet().iterator().next();
    }

    protected Query<T> applyRange(Query<T> query, Range range) {
        QueryKeys<T> countQuery = query.keys();

        // scrolling processing
        query = query.offset(range.getStartRow());

        if (range.getCount() != null) {
            query = query.limit(range.getCount());
        }

        int count = countQuery.list().size();
        range.setTotal(count);
        // if total is inconsistent with endrow, no need to continue
        if (range.getStartRow() > 0 && range.getStartRow() > range.getTotal() - 1)
            throw new InvalidRangeException("range header mal formed for scrolling : result set shorter than expected");

        return query;
    }

}
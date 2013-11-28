package dderrien.customportal.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dderrien.common.exception.ServerErrorException;
import dderrien.common.service.AbstractAuthService;
import dderrien.common.service.UserService;
import dderrien.common.util.Range;
import dderrien.customportal.dao.CategoryDao;
import dderrien.customportal.model.Category;

public class CategoryService extends AbstractAuthService<Category> {
    
    @Inject
    public CategoryService(CategoryDao dao, UserService userService) {
        super(dao, userService);
    }
    
    @Override
    public List<Category> select(Map<String, Object> filters, Range range, List<String> orders) {
        if (orders == null) {
            orders = Arrays.asList(new String[] { "+order" });
        }
        else {
            orders.add("+order");
        }
        return super.select(filters, range, orders);
    }
    
    @Override
    public Category update(Long id, Category entity) {
        
        // Special case of the category order update => other categories should be shifted accordingly
        if (entity.getOrder() != null) {
            Category existing = get(id);

            // Check if there's a move
            Long existingOrder = existing.getOrder() == null ? Long.valueOf(0L) : existing.getOrder();
            Long entityOrder = entity.getOrder();
            if (!existingOrder.equals(entityOrder)) {
                // Get the direction of the move
                boolean moveNearerFirst = existingOrder == null && entityOrder != null || 0 < existingOrder.compareTo(entityOrder);
                
                // Pass all categories in review and move them if they've been affected by the move
                try {
	                for (Category category: super.select(null, null, null)) {
	                    if (!category.getId().equals(id) && category.getOrder() != null) {
	                        Long categoryOrder = category.getOrder();
	                        if (moveNearerFirst) {
	                            // Move back the categories that were placed between the new place and the old place
	                            if (entityOrder <= categoryOrder && categoryOrder < existingOrder) {
	                                Category clone = (Category) category.clone();
	                                clone.setOrder(categoryOrder + 1);
	                                super.update(category, clone);
	                            }
	                        }
	                        else {
	                            // Move forward the categories that were placed between the old place and the new place
	                            if (existingOrder < categoryOrder && categoryOrder <= entityOrder) {
	                                Category clone = (Category) category.clone();
	                                clone.setOrder(categoryOrder - 1);
	                                super.update(category, clone);
	                            }
	                        }
	                    }
	                }
                }
                catch(CloneNotSupportedException ex) {
                	throw new ServerErrorException("Cannot update the category order because one of them is not cloneable!", ex);
                }
            }
            
            // Update the submitted entities, with all passed updated fields
            return super.update(existing, entity);
        }
        
        // Just the normal update
        return super.update(id, entity);
    }
}
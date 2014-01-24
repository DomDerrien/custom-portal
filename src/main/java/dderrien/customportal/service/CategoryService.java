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
    public Category update(Category existing, Long version, Category entity) {
        
        // Special case of the category order update => other categories should be shifted accordingly
        if (entity.getOrder() != null) {

            // Check if there's a move
            Long existingOrder = existing.getOrder() == null ? Long.valueOf(0L) : existing.getOrder();
            Long entityOrder = entity.getOrder(); // Entity order has already been identified as non null
            
            if (!existingOrder.equals(entityOrder)) {
                // Get the direction of the move
                boolean moveNearerFirst = 0 < existingOrder.compareTo(entityOrder);
                
                // Pass all categories in review and move them if they've been affected by the move
                try {
	                for (Category category: select(null, null, null)) { // Ordered selection defined above
	                    if (!category.getId().equals(existing.getId()) && category.getOrder() != null) {
	                        Long categoryOrder = category.getOrder();
	                        if (moveNearerFirst) {
	                            // Move back the categories that were placed between the new place and the old place
	                            if (entityOrder <= categoryOrder && categoryOrder < existingOrder) {
	                                Category clone = (Category) category.clone();
	                                clone.setOrder(categoryOrder + 1);
	                                super.update(category, version, clone);
	                            }
	                        }
	                        else {
	                            // Move forward the categories that were placed between the old place and the new place
	                            if (existingOrder < categoryOrder && categoryOrder <= entityOrder) {
	                                Category clone = (Category) category.clone();
	                                clone.setOrder(categoryOrder - 1);
	                                super.update(category, version, clone);
	                            }
	                        }
	                    }
	                }
                }
                catch(CloneNotSupportedException ex) {
                	throw new ServerErrorException("Cannot update the category order because one of them is not cloneable!", ex);
                }
            }
        }
        
        // Just the normal update
        return super.update(existing, version, entity);
    }
}
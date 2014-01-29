package dderrien.customportal.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CategoryTest {

    @Test
    public void testConstructor() {
        new Category();
    }

    @Test
    public void testAccessors() {
        Long order = 12345L;
        Long ownerId = 45678L;
        String title = "title";
        String width = "width_4";

        Category entity = new Category();

        entity.setOrder(order);
        entity.setOwnerId(ownerId);
        entity.setTitle(title);
        entity.setWidth(width);

        assertEquals(order, entity.getOrder());
        assertEquals(ownerId, entity.getOwnerId());
        assertEquals(title, entity.getTitle());
        assertEquals(width, entity.getWidth());
    }
}

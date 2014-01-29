package dderrien.common.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.googlecode.objectify.Key;

import dderrien.common.model.AbstractBase;
import dderrien.common.model.User;
import dderrien.common.service.UserService;
import dderrien.common.util.Range;

public class UserResourceTest {

    @Test
    public void testConstructor() {
        new UserResource(mock(UserService.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectAll() {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final ArrayList<User> selection = new ArrayList<User>();
        doAnswer(new Answer<List<User>>() {
            @Override
            public List<User> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
                assertEquals(0, filters.size());
                return selection;
            }
        }).when(service).select(anyMap(), any(Range.class), anyList());

        assertEquals(selection, resource.select(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectByName() {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final String name = "test";
        final ArrayList<User> selection = new ArrayList<User>();
        doAnswer(new Answer<List<User>>() {
            @Override
            public List<User> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
                assertEquals(1, filters.size());
                assertEquals(name, filters.get("name"));
                return selection;
            }
        }).when(service).select(anyMap(), any(Range.class), anyList());

        assertEquals(selection, resource.select(name));
        verify(service, times(1)).select(anyMap(), any(Range.class), anyList());
    }

    @Test
    public void testSelectAnnotations() {
        int annotationNb = 2;
        String methodName = "select";
        Class<UserResource> candidate = UserResource.class;

        Method method = null;
        for (Method m : candidate.getDeclaredMethods()) {
            if (methodName.equals(m.getName()))
                method = m;
        }
        if (method == null)
            fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

        Annotation[] annotations = method.getAnnotations();
        assertEquals(annotationNb, annotations.length);

        // @Get
        int adx = 0; // annotation index
        assertEquals(GET.class, annotations[adx].annotationType());
        // @Produces(MediaType.APPLICATION_JSON)
        adx++;
        assertEquals(Produces.class, annotations[adx].annotationType());
        assertEquals(1, ((Produces) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Produces) annotations[adx]).value()[0]);
    }

    @Test
    public void testGet() {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final Long id = 12345L;
        final User candidate = new User();
        when(service.get(id, 0L)).thenReturn(candidate);

        assertEquals(candidate, resource.get(id));
        verify(service, times(1)).get(id, 0L);
        verify(service, times(1)).get(anyLong(), anyLong());
    }

    @Test
    public void testGetVersioned() {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final Long id = 12345L;
        final User candidate = new User();
        final Long version = 4567L;
        when(service.get(id, version)).thenReturn(candidate);

        assertEquals(candidate, resource.getVersioned(id, version));
        verify(service, times(1)).get(id, version);
        verify(service, times(1)).get(anyLong(), anyLong());
    }

    @Test
    public void testGetAnnotations() {
        int annotationNb = 3;
        String methodName = "get";
        Class<UserResource> candidate = UserResource.class;

        Method method = null;
        for (Method m : candidate.getDeclaredMethods()) {
            if (methodName.equals(m.getName()))
                method = m;
        }
        if (method == null)
            fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

        Annotation[] annotations = method.getAnnotations();
        assertEquals(annotationNb, annotations.length);

        // @Get
        int adx = 0; // annotation index
        assertEquals(GET.class, annotations[adx].annotationType());
        // @Path("/{id:\\d+}")
        adx++;
        assertEquals(Path.class, annotations[adx].annotationType());
        assertEquals("/{id:\\d+}", ((Path) annotations[adx]).value());
        // @Produces(MediaType.APPLICATION_JSON)
        adx++;
        assertEquals(Produces.class, annotations[adx].annotationType());
        assertEquals(1, ((Produces) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Produces) annotations[adx]).value()[0]);
    }

    @Test
    public void testGetVersionedAnnotations() {
        int annotationNb = 3;
        String methodName = "getVersioned";
        Class<UserResource> candidate = UserResource.class;

        Method method = null;
        for (Method m : candidate.getDeclaredMethods()) {
            if (methodName.equals(m.getName()))
                method = m;
        }
        if (method == null)
            fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

        Annotation[] annotations = method.getAnnotations();
        assertEquals(annotationNb, annotations.length);

        // @Get
        int adx = 0; // annotation index
        assertEquals(GET.class, annotations[adx].annotationType());
        // @Path("/{id:\\d+}")
        adx++;
        assertEquals(Path.class, annotations[adx].annotationType());
        assertEquals("/{id:\\d+}/{version:\\d+}", ((Path) annotations[adx]).value());
        // @Produces(MediaType.APPLICATION_JSON)
        adx++;
        assertEquals(Produces.class, annotations[adx].annotationType());
        assertEquals(1, ((Produces) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Produces) annotations[adx]).value()[0]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateIdOnly() throws URISyntaxException {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final Long id = 12345L;
        final User candidate = new User();
        Key<AbstractBase<User>> storeKey = mock(Key.class);
        when(service.create(candidate)).thenReturn(storeKey);
        when(storeKey.getId()).thenReturn(id);

        String relativePath = "/api/user/";
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(relativePath);

        Response response = resource.createForIdOnly(candidate, uriInfo);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(relativePath + id, ((URI) response.getLocation()).toString());
        verify(service, times(1)).create(candidate);
        verify(service, times(1)).create(any(User.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateIdEntity() throws URISyntaxException {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final Long id = 12345L;
        final User candidate = new User();
        Key<AbstractBase<User>> storeKey = mock(Key.class);
        when(service.create(candidate)).thenReturn(storeKey);
        when(storeKey.getId()).thenReturn(id);

        String relativePath = "/api/user/";
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(relativePath);

        Response response = resource.createForIdAndEntity(candidate, uriInfo);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(relativePath + id, ((URI) response.getLocation()).toString());
        verify(service, times(1)).create(candidate);
        verify(service, times(1)).create(any(User.class));
    }

    @Test
    public void testCreateIdOnlyAnnotations() {
        int annotationNb = 3;
        String methodName = "createForIdOnly";
        Class<UserResource> candidate = UserResource.class;

        Method method = null;
        for (Method m : candidate.getDeclaredMethods()) {
            if (methodName.equals(m.getName()))
                method = m;
        }
        if (method == null)
            fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

        Annotation[] annotations = method.getAnnotations();
        assertEquals(annotationNb, annotations.length);

        // @Post
        int adx = 0; // annotation index
        assertEquals(POST.class, annotations[adx].annotationType());
        // @Consumes(MediaType.APPLICATION_JSON})
        adx++;
        assertEquals(Consumes.class, annotations[adx].annotationType());
        assertEquals(1, ((Consumes) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Consumes) annotations[adx]).value()[0]);
        // @Produces(MediaType.TEXT_PLAIN)
        adx++;
        assertEquals(Produces.class, annotations[adx].annotationType());
        assertEquals(1, ((Produces) annotations[adx]).value().length);
        assertEquals(MediaType.TEXT_PLAIN, ((Produces) annotations[adx]).value()[0]);
    }

    @Test
    public void testCreateIdEntityAnnotations() {
        int annotationNb = 3;
        String methodName = "createForIdAndEntity";
        Class<UserResource> candidate = UserResource.class;

        Method method = null;
        for (Method m : candidate.getDeclaredMethods()) {
            if (methodName.equals(m.getName()))
                method = m;
        }
        if (method == null)
            fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

        Annotation[] annotations = method.getAnnotations();
        assertEquals(annotationNb, annotations.length);

        // @Post
        int adx = 0; // annotation index
        assertEquals(POST.class, annotations[adx].annotationType());
        // @Consumes(MediaType.APPLICATION_JSON})
        adx++;
        assertEquals(Consumes.class, annotations[adx].annotationType());
        assertEquals(1, ((Consumes) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Consumes) annotations[adx]).value()[0]);
        // @Produces(MediaType.APPLICATION_JSON)
        adx++;
        assertEquals(Produces.class, annotations[adx].annotationType());
        assertEquals(1, ((Produces) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Produces) annotations[adx]).value()[0]);
    }

    @Test
    public void testUpdate() throws URISyntaxException {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final Long id = 12345L;
        final Long version = 4567L;
        final User candidate = new User();
        when(service.update(id, version, candidate)).thenReturn(candidate);

        assertEquals(candidate, resource.update(id, version, candidate));
        verify(service, times(1)).update(id, version, candidate);
        verify(service, times(1)).update(anyLong(), anyLong(), any(User.class));
    }

    @Test
    public void testUpdateAnnotations() {
        int annotationNb = 4;
        String methodName = "update";
        Class<UserResource> candidate = UserResource.class;

        Method method = null;
        for (Method m : candidate.getDeclaredMethods()) {
            if (methodName.equals(m.getName()))
                method = m;
        }
        if (method == null)
            fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

        Annotation[] annotations = method.getAnnotations();
        assertEquals(annotationNb, annotations.length);

        // @Put
        int adx = 0; // annotation index
        assertEquals(PUT.class, annotations[adx].annotationType());
        // @Path("/{id:\\d+}/{version:\\d+}")
        adx++;
        assertEquals(Path.class, annotations[adx].annotationType());
        assertEquals("/{id:\\d+}/{version:\\d+}", ((Path) annotations[adx]).value());
        // @Consumes(MediaType.APPLICATION_JSON})
        adx++;
        assertEquals(Consumes.class, annotations[adx].annotationType());
        assertEquals(1, ((Consumes) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Consumes) annotations[adx]).value()[0]);
        // @Produces(MediaType.APPLICATION_JSON)
        adx++;
        assertEquals(Produces.class, annotations[adx].annotationType());
        assertEquals(1, ((Produces) annotations[adx]).value().length);
        assertEquals(MediaType.APPLICATION_JSON, ((Produces) annotations[adx]).value()[0]);
    }

    @Test
    public void testDelete() throws URISyntaxException {
        UserService service = mock(UserService.class);
        UserResource resource = new UserResource(service);

        final Long id = 12345L;
        final Long version = 4567L;

        resource.delete(id, version);
        verify(service, times(1)).delete(id, version);
        verify(service, times(1)).delete(anyLong(), anyLong());
    }

    @Test
    public void testDeleteAnnotations() {
        int annotationNb = 2;
        String methodName = "delete";
        Class<UserResource> candidate = UserResource.class;

        Method method = null;
        for (Method m : candidate.getDeclaredMethods()) {
            if (methodName.equals(m.getName()))
                method = m;
        }
        if (method == null)
            fail("Method '" + methodName + "()' has not been found in class " + candidate.getCanonicalName());

        Annotation[] annotations = method.getAnnotations();
        assertEquals(annotationNb, annotations.length);

        // @Delete
        int adx = 0; // annotation index
        assertEquals(DELETE.class, annotations[adx].annotationType());
        // @Path("/{id:\\d+}/{version:\\d+}")
        adx++;
        assertEquals(Path.class, annotations[adx].annotationType());
        assertEquals("/{id:\\d+}/{version:\\d+}", ((Path) annotations[adx]).value());
    }
}

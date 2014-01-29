package dderrien.customportal.resource;

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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.googlecode.objectify.Key;

import dderrien.common.model.AbstractBase;
import dderrien.common.util.Range;
import dderrien.customportal.model.Link;
import dderrien.customportal.service.LinkService;

public class LinkResourceTest {

    @Test
    public void testConstructor() {
        new LinkResource(mock(LinkService.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectAll() {
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final ArrayList<Link> selection = new ArrayList<Link>();
        doAnswer(new Answer<List<Link>>() {
            @Override
            public List<Link> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
                assertEquals(0, filters.size());
                return selection;
            }
        }).when(service).select(anyMap(), any(Range.class), anyList());

        assertEquals(selection, resource.select(null, null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectByOnwerIdAndCategoryId() {
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final Long ownerId = 1234L;
        final Long categoryId = 5678L;
        final ArrayList<Link> selection = new ArrayList<Link>();
        doAnswer(new Answer<List<Link>>() {
            @Override
            public List<Link> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
                assertEquals(2, filters.size());
                assertEquals(ownerId, filters.get("ownerId"));
                assertEquals(categoryId, filters.get("categoryId"));
                return selection;
            }
        }).when(service).select(anyMap(), any(Range.class), anyList());

        assertEquals(selection, resource.select(ownerId, categoryId));
        verify(service, times(1)).select(anyMap(), any(Range.class), anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectByWrongOnwerIdAndWrongCategoryId() {
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final Long ownerId = 0L;
        final Long categoryId = 0L;
        final ArrayList<Link> selection = new ArrayList<Link>();
        doAnswer(new Answer<List<Link>>() {
            @Override
            public List<Link> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Object> filters = (Map<String, Object>) invocation.getArguments()[0];
                assertEquals(0, filters.size());
                return selection;
            }
        }).when(service).select(anyMap(), any(Range.class), anyList());

        assertEquals(selection, resource.select(ownerId, categoryId));
        verify(service, times(1)).select(anyMap(), any(Range.class), anyList());
    }

    @Test
    public void testSelectAnnotations() {
        int annotationNb = 2;
        String methodName = "select";
        Class<LinkResource> candidate = LinkResource.class;

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
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final Long id = 12345L;
        final Link candidate = new Link();
        when(service.get(id, 0L)).thenReturn(candidate);

        assertEquals(candidate, resource.get(id));
        verify(service, times(1)).get(id, 0L);
        verify(service, times(1)).get(anyLong(), anyLong());
    }

    @Test
    public void testGetVersioned() {
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final Long id = 12345L;
        final Long version = 4567L;
        final Link candidate = new Link();
        when(service.get(id, version)).thenReturn(candidate);

        assertEquals(candidate, resource.getVersioned(id, version));
        verify(service, times(1)).get(id, version);
        verify(service, times(1)).get(anyLong(), anyLong());
    }

    @Test
    public void testGetAnnotations() {
        int annotationNb = 3;
        String methodName = "get";
        Class<LinkResource> candidate = LinkResource.class;

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
        Class<LinkResource> candidate = LinkResource.class;

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
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final Long id = 12345L;
        final Link candidate = new Link();
        Key<AbstractBase<Link>> storeKey = mock(Key.class);
        when(service.create(candidate)).thenReturn(storeKey);
        when(storeKey.getId()).thenReturn(id);

        String relativePath = "/api/link/";
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(relativePath);

        Response response = resource.createForIdAndEntity(candidate, uriInfo);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(relativePath + id, ((URI) response.getLocation()).toString());
        verify(service, times(1)).create(candidate);
        verify(service, times(1)).create(any(Link.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateIdAndEntity() throws URISyntaxException {
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final Long id = 12345L;
        final Link candidate = new Link();
        Key<AbstractBase<Link>> storeKey = mock(Key.class);
        when(service.create(candidate)).thenReturn(storeKey);
        when(storeKey.getId()).thenReturn(id);

        String relativePath = "/api/link/";
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(relativePath);

        Response response = resource.createForIdAndEntity(candidate, uriInfo);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(relativePath + id, ((URI) response.getLocation()).toString());
        verify(service, times(1)).create(candidate);
        verify(service, times(1)).create(any(Link.class));
    }

    @Test
    public void testCreateIdOnlyAnnotations() {
        int annotationNb = 3;
        String methodName = "createForIdOnly";
        Class<LinkResource> candidate = LinkResource.class;

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
    public void testCreateIdAndEntityAnnotations() {
        int annotationNb = 3;
        String methodName = "createForIdAndEntity";
        Class<LinkResource> candidate = LinkResource.class;

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
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

        final Long id = 12345L;
        final Long version = 4567L;
        final Link candidate = new Link();
        when(service.update(id, version, candidate)).thenReturn(candidate);

        assertEquals(candidate, resource.update(id, version, candidate));
        verify(service, times(1)).update(id, version, candidate);
        verify(service, times(1)).update(anyLong(), anyLong(), any(Link.class));
    }

    @Test
    public void testUpdateAnnotations() {
        int annotationNb = 4;
        String methodName = "update";
        Class<LinkResource> candidate = LinkResource.class;

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
        LinkService service = mock(LinkService.class);
        LinkResource resource = new LinkResource(service);

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
        Class<LinkResource> candidate = LinkResource.class;

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

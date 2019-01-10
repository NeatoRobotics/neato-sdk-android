package com.neatorobotics.sdk.android;

import android.content.Context;
import com.neatorobotics.sdk.android.authentication.DefaultAccessTokenDatasource;
import com.neatorobotics.sdk.android.authentication.NeatoAuthentication;
import com.neatorobotics.sdk.android.beehive.BeehiveResponse;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NeatoUserTest {

    //class under test
    NeatoUser neatoUser;

    @Mock
    NeatoUser.AsyncCall mockBaseClient;

    @Mock
    NeatoAuthentication mockNeatoAutentication;

    @Mock
    NeatoCallback mockNeatoCallback;

    @Mock
    Context ctx;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        neatoUser = NeatoUser.getInstance(ctx);
        neatoUser.asyncCall = mockBaseClient;
        neatoUser.neatoAuthentication = mockNeatoAutentication;
    }

    @Test
    public void singletonTest() throws Exception {
        NeatoUser neatoUser1 = NeatoUser.getInstance(ctx);
        assertNotNull(neatoUser1);

        NeatoUser neatoUser2 = NeatoUser.getInstance(ctx);
        assertNotNull(neatoUser2);

        assertEquals(neatoUser1, neatoUser2);
    }

    @Test
    public void singletonWithCustomDatasourceTest() throws Exception {
        NeatoUser neatoUser1 = NeatoUser.getInstance(ctx, new DefaultAccessTokenDatasource(ctx));
        assertNotNull(neatoUser1);

        NeatoUser neatoUser2 = NeatoUser.getInstance(ctx);
        assertNotNull(neatoUser2);

        assertEquals(neatoUser1, neatoUser2);
    }


    @Test
    public void logout() throws Exception {
        final BeehiveResponse response = new BeehiveResponse(HttpURLConnection.HTTP_OK, new JSONObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).done(response);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.logout(mockNeatoCallback);

        verify(mockNeatoCallback).done(true);
    }

    @Test
    public void loadRobots_InvalidToken() throws Exception {
        final BeehiveResponse response = new BeehiveResponse(HttpURLConnection.HTTP_UNAUTHORIZED, new JSONObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).fail(NeatoError.INVALID_TOKEN);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.loadRobots(mockNeatoCallback);

        verify(mockNeatoCallback).fail(NeatoError.INVALID_TOKEN);
    }

    @Test
    public void loadRobots_Error() throws Exception {
        final BeehiveResponse response = new BeehiveResponse(HttpURLConnection.HTTP_NOT_FOUND, new JSONObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).fail(NeatoError.GENERIC_ERROR);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.loadRobots(mockNeatoCallback);

        verify(mockNeatoCallback).fail(NeatoError.GENERIC_ERROR);
    }

    @Test
    public void loadRobots_GenericError() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).fail(NeatoError.GENERIC_ERROR);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.loadRobots(mockNeatoCallback);

        verify(mockNeatoCallback).fail(NeatoError.GENERIC_ERROR);
    }

    @Test
    public void loadRobots_NullJSON() throws Exception {
        final BeehiveResponse response = new BeehiveResponse(HttpURLConnection.HTTP_OK, null);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).fail(NeatoError.GENERIC_ERROR);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.loadRobots(mockNeatoCallback);

        verify(mockNeatoCallback).fail(NeatoError.GENERIC_ERROR);
    }

    @Test
    public void loadRobots_OK_Robot_In_List() throws Exception {
        final BeehiveResponse response = new BeehiveResponse(HttpURLConnection.HTTP_OK, new JSONObject(MockJSON.loadJSON(this,"json/robots/robots_list.json")));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).done(response);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.loadRobots(mockNeatoCallback);

        verify(mockNeatoCallback, never()).fail(any(NeatoError.class));
        verify(mockNeatoCallback).done(any(ArrayList.class));
    }

    @Test
    public void getUserInfo() throws Exception {
        final BeehiveResponse response = new BeehiveResponse(HttpURLConnection.HTTP_OK, new JSONObject(MockJSON.loadJSON(this,"json/user/me.json")));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).done(response);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.getUserInfo(mockNeatoCallback);

        verify(mockNeatoCallback, never()).fail(any(NeatoError.class));

        ArgumentCaptor<JSONObject> argument = ArgumentCaptor.forClass(JSONObject.class);
        verify(mockNeatoCallback).done(argument.capture());
        assertEquals("marco@example.com", argument.getValue().getString("email"));
        assertEquals("Marco", argument.getValue().getString("first_name"));
        assertEquals("Uberti", argument.getValue().getString("last_name"));
    }

    @Test
    public void getUserInfoFail() throws Exception {
        final BeehiveResponse response = new BeehiveResponse(HttpURLConnection.HTTP_OK, new JSONObject(MockJSON.loadJSON(this,"json/user/me.json")));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((NeatoCallback) invocation.getArguments()[4]).fail(NeatoError.GENERIC_ERROR);
                return null;
            }
        }).when(mockBaseClient).executeCall(anyString(),anyString(),anyString(),any(JSONObject.class),any(NeatoCallback.class));

        neatoUser.getUserInfo(mockNeatoCallback);

        verify(mockNeatoCallback).fail(NeatoError.GENERIC_ERROR);
    }
}
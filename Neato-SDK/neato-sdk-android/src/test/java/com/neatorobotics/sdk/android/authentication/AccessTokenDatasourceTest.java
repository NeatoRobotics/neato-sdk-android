package com.neatorobotics.sdk.android.authentication;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenDatasourceTest {

    @Mock
    Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void customAccessTokenDatasourceTest() throws Exception {
        NeatoAuthentication auth = NeatoAuthentication.getInstance(context, new CustomAccessTokenDatasource());
        Date expires = new Date();
        auth.setOauth2AccessToken("123", expires);
        assertEquals("123", auth.getOauth2AccessToken());
        assertTrue(auth.isAuthenticated());
    }

    /**
     * Custom accessTokenDatasource for testing
     */
    private class CustomAccessTokenDatasource implements AccessTokenDatasource{

        public String token;
        public Date expires;

        @Override
        public void storeToken(String token, Date expires) {
            this.token = token;
            this.expires = expires;
        }

        @Override
        public String loadToken() {
            return token;
        }

        @Override
        public void clearToken() {}

        @Override
        public boolean isTokenValid() {
            return true;
        }
    }
}
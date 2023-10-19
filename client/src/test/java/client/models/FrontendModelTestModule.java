package client.models;

import client.contexts.SessionContext;
import client.di.BoardModule;
import com.google.inject.AbstractModule;

import static org.mockito.Mockito.mock;

public class FrontendModelTestModule extends AbstractModule {
    public SessionContext sessionMock = mock(SessionContext.class);

    @Override
    protected void configure() {
        bind(SessionContext.class).toInstance(sessionMock);
        install(new BoardModule());
    }
}

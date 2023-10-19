/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.di;

import client.App;
import client.SimpleFXMLLoader;
import client.components.ColorGenerator;
import client.contexts.SessionContext;
import client.contexts.UserContext;
import client.utils.AppPalette;
import client.utils.ServerUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AppModule extends AbstractModule {

    @Override
    public void configure() {
        bind(App.class).in(Scopes.SINGLETON);
        bind(MyFXML.class).in(Scopes.SINGLETON);
        bind(ColorGenerator.class).in(Scopes.SINGLETON);
        bind(AppPalette.class).in(Scopes.SINGLETON);
        bind(SimpleFXMLLoader.class).in(Scopes.SINGLETON);

        bind(UserContext.class).in(Scopes.SINGLETON);
        bind(SessionContext.class).in(Scopes.SINGLETON);
        bind(ServerUtils.class).in(Scopes.SINGLETON);

        install(new BoardModule());
        install(new WorkspaceModule());
    }
}
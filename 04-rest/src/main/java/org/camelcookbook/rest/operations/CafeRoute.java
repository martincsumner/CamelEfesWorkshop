/*
 * Copyright (C) Scott Cranton, Jakub Korab, and Christian Posta
 * https://github.com/CamelCookbook
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

package org.camelcookbook.rest.operations;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.camelcookbook.rest.common.MenuItem;

/**
 * Simple REST DSL example
 */
public class CafeRoute extends RouteBuilder {
    private int port1;

    public CafeRoute() {
    }

    public CafeRoute(int port) {
        this.port1 = port;
    }

    public void setPort1(int port1) {
        this.port1 = port1;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .component("undertow").port(port1)
            .bindingMode(RestBindingMode.json);

        rest("/cafe/menu")
            .get("/items").outType(MenuItem[].class)
                .to("bean:menuService?method=getMenuItems")
            .get("/items/{id}").outType(MenuItem.class)
                .to("bean:menuService?method=getMenuItem(${header.id})")
            .post("/items").type(MenuItem.class)
                .route().to("bean:menuService?method=createMenuItem").setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201)).endRest()
            .put("/items/{id}").type(MenuItem.class)
                .to("bean:menuService?method=updateMenuItem(${header.id}, ${body})")
            .delete("/items/{id}")
               .to("bean:menuService?method=removeMenuItem(${header.id})");
    }
}

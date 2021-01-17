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

package org.camelcookbook.transactions.rollback;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the use of the markRollbackOnly statement roll back the transaction without throwing a transaction.
 */
public class RollbackMarkRollbackOnlyRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:transacted")
            .transacted()
            .log("Processing message: ${body}")
            .setHeader("message", body())
            .to("sql:insert into audit_log (message) values (:#message)")
            .choice()
                .when(simple("${body} contains 'explode'"))
                    .log("Message cannot be processed further - rolling back insert")
                    .markRollbackOnly()
                .otherwise()
                    .log("Message processed successfully")
            .end()
            .to("mock:out");
    }
}

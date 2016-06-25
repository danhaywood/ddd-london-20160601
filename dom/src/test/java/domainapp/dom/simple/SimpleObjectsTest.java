/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package domainapp.dom.simple;

import java.util.List;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleObjectsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;
    
    Sessions sessions;

    @Before
    public void setUp() throws Exception {
        sessions = new Sessions();
        sessions.repositoryService = mockRepositoryService;
    }

    public static class Create extends SimpleObjectsTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final Session session = new Session();

            final Sequence seq = context.sequence("create");
            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).instantiate(Session.class);
                    inSequence(seq);
                    will(returnValue(session));

                    oneOf(mockRepositoryService).persist(session);
                    inSequence(seq);
                }
            });

            // when
            final Session obj = sessions.create("Foobar", null);

            // then
            assertThat(obj).isEqualTo(session);
            assertThat(obj.getName()).isEqualTo("Foobar");
        }

    }

    public static class ListAll extends SimpleObjectsTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<Session> all = Lists.newArrayList();

            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).allInstances(Session.class);
                    will(returnValue(all));
                }
            });

            // when
            final List<Session> list = sessions.listAll();

            // then
            assertThat(list).isEqualTo(all);
        }
    }
}

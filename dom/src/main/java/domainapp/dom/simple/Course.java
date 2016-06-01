/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.dom.simple;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.eventbus.PropertyDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.util.ObjectContracts;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "simple",
        table = "SimpleObject"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
//        strategy=VersionStrategy.VERSION_NUMBER,
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.simple.Course "),
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.simple.Course "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="SimpleObject_name_UNQ", members = {"name"})
@DomainObject(
        publishing = Publishing.ENABLED
)
public class Course implements Comparable<Course> {

    public static final int NAME_LENGTH = 40;

    @Persistent(mappedBy = "course", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<Session> sessions = new TreeSet<Session>();

    public TranslatableString title() {
        return TranslatableString.tr("Object: {name}", "name", getName());
    }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    public Session schedule(
            @ParameterLayout(named = "Session date")
            final LocalDate sessionDate,
            @ParameterLayout(named = "Are you sure?")
            final boolean areYouSure) {

        Session session = new Session();
        session.setSessionDate(sessionDate);
        getSessions().add(session);

        return session;
    }

    public String validateSchedule(
            final LocalDate sessionDate,
            final boolean areYouSure) {
        if(areYouSure) {
            return null;
        }

        LocalDate now = clockService.now();
        if(now.compareTo(sessionDate) >= 0) {
            return "Can't schedule in the past";
        }
        return null;
    }

    @Inject
    ClockService clockService;

    public static class NameDomainEvent extends PropertyDomainEvent<Course,String> {}
    @javax.jdo.annotations.Column(
            allowsNull="false",
            length = NAME_LENGTH
    )
    @Property(
        command = CommandReification.ENABLED,
        publishing = Publishing.ENABLED,
        domainEvent = NameDomainEvent.class
    )
    @Getter @Setter
    private String name;

    public TranslatableString validateName(final String name) {
        return name != null && name.contains("!")? TranslatableString.tr("Exclamation mark is not allowed"): null;
    }



    public static class UpdateNameDomainEvent extends ActionDomainEvent<Course> {}
    @Action(
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED,
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = UpdateNameDomainEvent.class
    )
    @MemberOrder(name="name", sequence = "1") // associate with 'name' property
    public Course updateName(@ParameterLayout(named="Name") final String name) {
        setName(name);
        return this;
    }
    public String default0UpdateName() {
        return getName();
    }
    public TranslatableString validate0UpdateName(final String name) {
        return validateName(name);
    }




    public static class DeleteDomainEvent extends ActionDomainEvent<Course> {}
    @Action(
            domainEvent = DeleteDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE
    )
    public void delete() {
        repositoryService.remove(this);
    }



    @Override
    public int compareTo(final Course other) {
        return ObjectContracts.compare(this, other, "name");
    }


    @javax.inject.Inject
    RepositoryService repositoryService;

}

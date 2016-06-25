package domainapp.dom.simple;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "simple",
        table = "Presenter"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.simple.Presenter "),
        @Query(
                name = "findByNameContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.simple.Presenter "
                        + "WHERE name.indexOf(:name) >= 0 "),
        @Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.simple.Presenter "
                        + "WHERE name == :name ")
})
@Unique(name = "Presenter_name_UNQ", members = { "name" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Presenter implements Comparable<Presenter> {



    @Column(allowsNull = "false")
    @Property()
    @Title
    @Getter @Setter
    private String name;

    @Persistent(mappedBy = "presenter", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<Session> sessions = new TreeSet<Session>();

    @Action()
    public Session proposeSession(
            @ParameterLayout(named = "Session name")
            final String name) {
        final Session session = sessionRepository.create(name, this);
        return session;
    }

    public String disableProposeSession() {
        return getSessions().size() > 2 ? "No more sessions": null;
    }


    //region > compareTo, toString
    @Override
    public int compareTo(final Presenter other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "name");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "name");
    }
    //endregion


    @Inject
    Sessions sessionRepository;

}

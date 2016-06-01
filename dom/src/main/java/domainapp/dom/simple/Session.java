package domainapp.dom.simple;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "simple",
        table = "Session"
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
                        + "FROM domainapp.dom.simple.Session "),
        @Query(
                name = "findBySessionDateContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.simple.Session "
                        + "WHERE sessionDate.indexOf(:sessionDate) >= 0 "),
        @Query(
                name = "findBySessionDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.simple.Session "
                        + "WHERE sessionDate == :sessionDate ")
})
@Unique(name = "Session_sessionDate_UNQ", members = { "sessionDate" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Session implements Comparable<Session> {

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private LocalDate sessionDate;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private Course course;

    //region > compareTo, toString
    @Override
    public int compareTo(final Session other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "sessionDate");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "sessionDate");
    }
    //endregion

}

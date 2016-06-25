package domainapp.dom.simple;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Presenters",
        menuOrder = "1"
)
public class PresenterMenu {

    @Action(
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            bookmarking = BookmarkPolicy.AS_ROOT
    )
    @MemberOrder(sequence = "1")
    public java.util.List<Presenter> listAll() {
        return repositoryService.allInstances(Presenter.class);
    }


    @Action(
    )
    @MemberOrder(sequence = "3")
    public Presenter create(
            @ParameterLayout(named="Name")
            final String name) {
        final Presenter presenter = repositoryService.instantiate(Presenter.class);
        presenter.setName(name);
        repositoryService.persist(presenter);
        return presenter;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}

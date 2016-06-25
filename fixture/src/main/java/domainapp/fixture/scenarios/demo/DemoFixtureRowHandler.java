package domainapp.fixture.scenarios.demo;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import domainapp.dom.simple.Session;
import domainapp.dom.simple.Sessions;
import lombok.Getter;
import lombok.Setter;

public class DemoFixtureRowHandler implements ExcelFixtureRowHandler {

    @Getter @Setter
    private String name;

    @Override
    public List<Object> handleRow(
            final FixtureScript.ExecutionContext executionContext,
            final ExcelFixture excelFixture,
            final Object previousRow) {

        final List<Session> matching = repository.findByName(name);
        if(matching.isEmpty()) {
            Session session = repository.create(name, null);
            executionContext.addResult(excelFixture, session);
        }
        return Collections.emptyList();
    }

    @Inject
    Sessions repository;

}

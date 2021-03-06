package domainapp.fixture.scenarios.demo;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import domainapp.dom.simple.SimpleObject;
import domainapp.dom.simple.SimpleObjects;
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

        final List<SimpleObject> matching = repository.findByName(name);
        if(matching.isEmpty()) {
            SimpleObject simpleObject = repository.create(name);
            executionContext.addResult(excelFixture, simpleObject);
        }
        return Collections.emptyList();
    }

    @Inject
    SimpleObjects repository;

}

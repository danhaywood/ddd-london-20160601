package domainapp.fixture.scenarios.demo;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import domainapp.dom.simple.Course;
import domainapp.dom.simple.Courses;
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

        final List<Course> matching = repository.findByName(name);
        if(matching.isEmpty()) {
            Course course = repository.create(name);
            executionContext.addResult(excelFixture, course);
        }
        return Collections.emptyList();
    }

    @Inject
    Courses repository;

}

package it.chalmers.gamma;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.dependencies.SliceRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    public static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("it.chalmers.gamma");
    }

    @Test
    public void testArchitecture() {
    }

    @Test
    public void layersShouldBeFreeOfCycles() {
        SliceRule rule = SlicesRuleDefinition.slices()
                .matching("it.chalmers.gamma.(*)..")
                .should().beFreeOfCycles();
        rule.check(classes);
    }

    @Test
    public void adaptersShouldNotDependOnEachOther() {
        SliceRule rule = SlicesRuleDefinition.slices()
                .matching("it.chalmers.gamma.adapter.(**)")
                .should().notDependOnEachOther();
        rule.check(classes);
    }

}

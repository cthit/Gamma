package it.chalmers.gamma;

import com.tngtech.archunit.core.domain.JavaClasses;

public class ArchitectureTest {

  private static JavaClasses classes;
  //
  //    @BeforeAll
  //    public static void setUp() {
  //        classes = new ClassFileImporter()
  //                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
  //                .importPackages("it.chalmers.gamma");
  //    }
  //
  //    @Test
  //    public void testArchitecture() {
  //    }
  //
  //    //Add test checking that primary adapter only uses *Facade.
  //
  //    @Test
  //    public void layersShouldBeFreeOfCycles() {
  //        SliceRule rule = SlicesRuleDefinition.slices()
  //                .matching("it.chalmers.gamma.(*)..")
  //                .should().beFreeOfCycles();
  //        rule.check(classes);
  //    }
  //
  //    @Test
  //    public void adaptersShouldNotDependOnEachOther() {
  //        SliceRule rule = SlicesRuleDefinition.slices()
  //                .matching("it.chalmers.gamma.adapter.(**)")
  //                .should().notDependOnEachOther();
  //        rule.check(classes);
  //    }

}

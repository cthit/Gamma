package it.chalmers.gamma.security.api;

import static org.assertj.core.api.Assertions.assertThat;

import it.chalmers.gamma.adapter.primary.api.v2.AllowListV2Controller;
import it.chalmers.gamma.adapter.primary.api.v2.ClientSelfV2Controller;
import it.chalmers.gamma.adapter.primary.api.v2.GroupV2Controller;
import it.chalmers.gamma.adapter.primary.api.v2.ProvisionV2Controller;
import it.chalmers.gamma.adapter.primary.api.v2.SuperGroupV2Controller;
import it.chalmers.gamma.adapter.primary.api.v2.UserV2Controller;
import it.chalmers.gamma.security.api.ScopeAuthorizationFilter.PathScopeRule;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Guards the deny-by-default behavior of the scope filter: every /api/v2 handler mapping must have
 * a matching scope rule, or the endpoint is unreachable for API keys.
 */
class V2ScopeRulesCoverageTest {

  private static final List<Class<?>> V2_CONTROLLERS =
      List.of(
          AllowListV2Controller.class,
          ClientSelfV2Controller.class,
          GroupV2Controller.class,
          ProvisionV2Controller.class,
          SuperGroupV2Controller.class,
          UserV2Controller.class);

  @Test
  void everyV2HandlerHasAMatchingScopeRule() {
    List<PathScopeRule> rules = ScopeAuthorizationFilter.defaultV2Rules();
    List<String> uncovered = new ArrayList<>();

    for (Class<?> controller : V2_CONTROLLERS) {
      RequestMapping classMapping =
          AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class);
      assertThat(classMapping).as("class-level @RequestMapping on %s", controller).isNotNull();
      String base = classMapping.value()[0];

      for (Method handler : controller.getDeclaredMethods()) {
        RequestMapping mapping =
            AnnotatedElementUtils.findMergedAnnotation(handler, RequestMapping.class);
        if (mapping == null) {
          continue;
        }
        String[] subPaths = mapping.value().length > 0 ? mapping.value() : new String[] {""};
        for (String subPath : subPaths) {
          String path = normalize(base + subPath);
          if (!hasRule(rules, mapping, path)) {
            uncovered.add(path + " " + describeMethods(mapping));
          }
        }
      }
    }

    assertThat(uncovered)
        .as("v2 handler mappings without a scope rule (denied by default)")
        .isEmpty();
  }

  private static boolean hasRule(List<PathScopeRule> rules, RequestMapping mapping, String path) {
    String samplePath = path.replaceAll("\\{[^}]+}", "sample");
    RequestMethod[] httpMethods = mapping.method();
    return rules.stream()
        .anyMatch(
            rule ->
                rule.pathPattern().matcher(samplePath).matches()
                    && (httpMethods.length == 0
                        || List.of(httpMethods).stream()
                            .anyMatch(m -> rule.method().equalsIgnoreCase(m.name()))));
  }

  private static String describeMethods(RequestMapping mapping) {
    if (mapping.method().length == 0) {
      return "(any method)";
    }
    List<String> methods = new ArrayList<>();
    for (RequestMethod method : mapping.method()) {
      methods.add(method.name());
    }
    return String.join(",", methods);
  }

  private static String normalize(String path) {
    return path.replaceAll("/{2,}", "/");
  }
}

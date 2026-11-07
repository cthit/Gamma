import type { Locator, Page } from "@playwright/test";

// Helpers for finding things the way assistive technology does. Tests only use
// accessible queries (roles, accessible names and labels), see eslint.config.mjs.

/** The card (`<article>`) whose level 2 heading is exactly `heading`. */
export function articleByHeading(
  page: Page,
  heading: string | RegExp,
): Locator {
  return page.getByRole("article").filter({
    has: page.getByRole("heading", { level: 2, name: heading, exact: true }),
  });
}

/** The table row that contains `text`. */
export function rowWithText(scope: Page | Locator, text: string): Locator {
  return scope.getByRole("row").filter({ hasText: text });
}

/**
 * The values the form that `control` belongs to would submit, hidden fields
 * such as the CSRF token and the optimistic locking version included. Find the
 * control with an accessible query (e.g. getByLabel) first.
 */
export async function formValuesOf(
  control: Locator,
): Promise<Record<string, string>> {
  // Typed structurally, the e2e project does not include the DOM library.
  type FormControl = {
    name: string;
    value: string;
    type: string;
    checked?: boolean;
    disabled?: boolean;
  };
  return control.evaluate((element: unknown) => {
    const form = (
      element as { form: { elements: ArrayLike<FormControl> } | null }
    ).form;
    if (!form) {
      throw new Error("The control does not belong to a form");
    }
    const values: Record<string, string> = {};
    for (const field of Array.from(form.elements)) {
      const unchecked =
        (field.type === "checkbox" || field.type === "radio") && !field.checked;
      const notSubmitted = ["button", "submit", "reset", "file"].includes(
        field.type,
      );
      if (!field.name || field.disabled || unchecked || notSubmitted) {
        continue;
      }
      if (!(field.name in values)) {
        values[field.name] = field.value;
      }
    }
    return values;
  });
}

/** The CSRF token of the form that `control` belongs to. */
export async function csrfTokenOf(control: Locator): Promise<string> {
  const token = (await formValuesOf(control))["_csrf"];
  if (!token) {
    throw new Error("The form has no CSRF token");
  }
  return token;
}

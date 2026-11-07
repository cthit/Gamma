import path from "node:path";
import { fileURLToPath } from "node:url";
import js from "@eslint/js";
import { defineConfig } from "eslint/config";
import tseslint from "typescript-eslint";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig(
  {
    ignores: [
      "node_modules/**",
      "playwright-report/**",
      "test-results/**",
      "fixtures/**",
      "eslint.config.mjs",
    ],
  },
  js.configs.recommended,
  tseslint.configs.recommendedTypeChecked,
  {
    files: ["**/*.ts"],
    languageOptions: {
      parserOptions: {
        project: "./tsconfig.json",
        tsconfigRootDir: __dirname,
      },
    },
    rules: {
      "@typescript-eslint/no-explicit-any": "error",
      "@typescript-eslint/restrict-template-expressions": "off",
    },
  },
  {
    // Tests find elements the way assistive technology does: by role and
    // accessible name (getByRole), label (getByLabel) or text (getByText).
    // If a test can't find something that way, fix the markup, not the test.
    files: ["**/*.ts"],
    rules: {
      "no-restricted-syntax": [
        "error",
        {
          selector:
            "CallExpression[callee.property.name=/^(locator|frameLocator|getByPlaceholder|getByTestId|waitForSelector|\\$|\\$\\$|\\$eval|\\$\\$eval)$/]",
          message:
            "Use accessible locators only: getByRole (preferred), getByLabel or getByText. CSS/XPath selectors, placeholders and test ids are not allowed.",
        },
        {
          // page.click("selector"), page.isChecked("selector"), ...
          selector:
            "CallExpression[callee.property.name=/^(click|dblclick|tap|hover|focus|check|uncheck|textContent|innerText|innerHTML|inputValue|isChecked|isVisible|isHidden|isEnabled|isDisabled|isEditable)$/][arguments.0.type=/^(Literal|TemplateLiteral)$/]",
          message:
            "Don't pass a selector string to page methods. Find the element with getByRole, getByLabel or getByText and call the method on that locator.",
        },
        {
          // page.fill("selector", value), page.selectOption("selector", value), ...
          selector:
            "CallExpression[callee.property.name=/^(fill|type|press|pressSequentially|selectOption|setChecked|setInputFiles|getAttribute)$/][arguments.0.type=/^(Literal|TemplateLiteral)$/][arguments.1][arguments.1.type!='ObjectExpression']",
          message:
            "Don't pass a selector string to page methods. Find the element with getByRole, getByLabel or getByText and call the method on that locator.",
        },
        {
          selector:
            "CallExpression[callee.property.name=/^(querySelector|querySelectorAll|getElementById|getElementsByClassName|getElementsByTagName)$/], Literal[value=/querySelector|getElementById/]",
          message:
            "Don't query the DOM to find elements. Use getByRole, getByLabel or getByText.",
        },
      ],
    },
  },
);

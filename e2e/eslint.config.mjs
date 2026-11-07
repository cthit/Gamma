import path from "node:path";
import { fileURLToPath } from "node:url";
import js from "@eslint/js";
import { defineConfig } from "eslint/config";
import tseslint from "typescript-eslint";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

// Page and Frame still have methods that take a selector string, like
// page.click("#save") or page.fill(selector, value). Locators have methods of
// the same names, so tell them apart by type rather than by the arguments.
const SELECTOR_METHODS = new Set([
  "check",
  "click",
  "dblclick",
  "dispatchEvent",
  "dragAndDrop",
  "fill",
  "focus",
  "getAttribute",
  "hover",
  "innerHTML",
  "innerText",
  "inputValue",
  "isChecked",
  "isDisabled",
  "isEditable",
  "isEnabled",
  "isHidden",
  "isVisible",
  "press",
  "selectOption",
  "setChecked",
  "setInputFiles",
  "tap",
  "textContent",
  "type",
  "uncheck",
]);

const accessibleLocators = {
  rules: {
    "no-page-selector-methods": {
      meta: {
        type: "problem",
        schema: [],
        messages: {
          selector:
            "Don't pass a selector to page.{{name}}(). Find the element with getByRole, getByLabel or getByText and call {{name}}() on that locator.",
        },
      },
      create(context) {
        const services = context.sourceCode.parserServices;
        const checker = services.program.getTypeChecker();
        // page.click and page["click"] (or page[`click`]) name the same method.
        const methodName = (node) => {
          if (!node.computed) {
            return node.property.type === "Identifier"
              ? node.property.name
              : null;
          }
          if (node.property.type === "Literal") {
            return typeof node.property.value === "string"
              ? node.property.value
              : null;
          }
          if (
            node.property.type === "TemplateLiteral" &&
            node.property.expressions.length === 0
          ) {
            return node.property.quasis[0].value.cooked;
          }
          return null;
        };
        // A value typed Page | Frame, or Page & Something, is still a page.
        const isPageOrFrame = (type) => {
          if (type.isUnionOrIntersection()) {
            return type.types.some(isPageOrFrame);
          }
          const typeName = (type.aliasSymbol ?? type.getSymbol())?.getName();
          return typeName === "Page" || typeName === "Frame";
        };
        return {
          "CallExpression > MemberExpression.callee"(node) {
            const name = methodName(node);
            if (name === null || !SELECTOR_METHODS.has(name)) {
              return;
            }
            const type = checker.getTypeAtLocation(
              services.esTreeNodeToTSNodeMap.get(node.object),
            );
            if (isPageOrFrame(type)) {
              context.report({ node, messageId: "selector", data: { name } });
            }
          },
        };
      },
    },
  },
};

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
    plugins: { "accessible-locators": accessibleLocators },
    rules: {
      "accessible-locators/no-page-selector-methods": "error",
      "no-restricted-syntax": [
        "error",
        {
          selector:
            "CallExpression[callee.property.name=/^(locator|frameLocator|getByPlaceholder|getByTestId|waitForSelector|\\$|\\$\\$|\\$eval|\\$\\$eval)$/]",
          message:
            "Use accessible locators only: getByRole (preferred), getByLabel or getByText. CSS/XPath selectors, placeholders and test ids are not allowed.",
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

import React from "react";
import { shallow } from "enzyme";
import generateId from "./id.generator";

describe("id.generator", () => {
  test("id.generator test", () => {
    expect(generateId("Asdf")).toBe("Asdf-1");
    expect(generateId()).toBe("id-2");
    expect(generateId()).toBe("id-3");
    expect(generateId("fdsa")).toBe("fdsa-4");
  });
});

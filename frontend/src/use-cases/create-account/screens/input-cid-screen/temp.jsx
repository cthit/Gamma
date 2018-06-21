import React from "react";
import styled from "styled-components";

import { GammaMenu } from "../../../../common/gui/gamma-menu";

export const Temp = ({}) => (
  <div>
    <GammaMenu
      onClick={value => {
        console.log(value + " has been selected");
      }}
      valueToTextMap={{
        first_option: "First option",
        second_option: "Second option"
      }}
    />
  </div>
);

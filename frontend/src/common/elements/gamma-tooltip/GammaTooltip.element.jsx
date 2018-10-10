import React from "react";

import { Tooltip } from "@material-ui/core";
import { Fill } from "../../../common-ui/layout";

const GammaTooltip = ({ children, text }) => (
  <Tooltip title={text}>
    <Fill>{children}</Fill>
  </Tooltip>
);

export default GammaTooltip;

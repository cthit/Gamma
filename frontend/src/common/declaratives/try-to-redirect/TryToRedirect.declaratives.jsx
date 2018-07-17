import React from "react";
import { Redirect } from "react-router-dom";

import IfElseRendering from "../if-else-rendering";

const TryToRedirect = ({ from, to, currentPath }) => (
  <IfElseRendering
    test={currentPath === from}
    ifRender={() => <Redirect to={to} />}
    elseRender={() => {}}
  />
);

export default TryToRedirect;

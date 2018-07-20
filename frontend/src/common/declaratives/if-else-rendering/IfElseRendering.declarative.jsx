import React from "react";
import PropTypes from "prop-types";
import { Fill } from "../../../common-ui/layout";

const IfElseRendering = ({ test, ifRender, elseRender }) => {
  return (test == null
  ? false
  : test)
    ? ifRender()
    : elseRender != null
      ? elseRender()
      : null;
};

IfElseRendering.propTypes = {
  test: PropTypes.bool.isRequired,
  ifRender: PropTypes.func.isRequired,
  elseRender: PropTypes.func
};

export default IfElseRendering;

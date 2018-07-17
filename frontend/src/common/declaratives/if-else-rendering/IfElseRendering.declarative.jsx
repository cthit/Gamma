import React from "react";
import PropTypes from "prop-types";
import { Fill } from "../../../common-ui/layout";

const IfElseRendering = ({ test, ifRender, elseRender }) => (
  <Fill>{test ? ifRender() : elseRender()}</Fill>
);

IfElseRendering.propTypes = {
  test: PropTypes.bool.isRequired,
  ifRender: PropTypes.func.isRequired,
  elseRender: PropTypes.func.isRequired
};

export default IfElseRendering;

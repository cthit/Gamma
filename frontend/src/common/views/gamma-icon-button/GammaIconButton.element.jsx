import React from "react";

import PropTypes from "prop-types";

import { IconButton } from "@material-ui/core";
import { getColor } from "../common/utils/color";

class GammaIconButton extends React.Component {
  render() {
    return (
      <IconButton
        disabled={this.props.disabled}
        onClick={this.props.onClick}
        color={getColor(this.props.primary, this.props.secondary)}
      >
        {this.props.children}
      </IconButton>
    );
  }
}

GammaIconButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  children: PropTypes.element.isRequired,
  primary: PropTypes.bool,
  secondary: PropTypes.bool,
  disabled: PropTypes.bool
};

export default GammaIconButton;

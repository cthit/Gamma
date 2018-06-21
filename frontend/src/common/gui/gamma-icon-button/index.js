import React from "react";

import PropTypes from "prop-types";

import { IconButton } from "@material-ui/core";

export class GammaIconButton extends React.Component {
  render() {
    return (
      <IconButton
        disabled={this.props.disabled}
        onClick={this.props.onClick}
        color={
          this.props.primary
            ? "primary"
            : this.props.secondary
              ? "secondary"
              : "default"
        }
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

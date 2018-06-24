import React from "react";

import PropTypes from "prop-types";

import { DownRightFABButton } from "./styles";
import { getColor } from "../common/utils/color";

export class GammaFABButton extends React.Component {
  render() {
    return (
      <DownRightFABButton
        variant="fab"
        disabled={this.props.disabled}
        onClick={this.props.onClick}
        color={getColor(this.props.primary, this.props.secondary)}
      >
        {this.props.children}
      </DownRightFABButton>
    );
  }
}

GammaFABButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  children: PropTypes.element.isRequired,
  disabled: PropTypes.bool,
  primary: PropTypes.bool,
  secondary: PropTypes.bool
};

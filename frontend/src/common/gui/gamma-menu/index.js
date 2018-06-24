import React from "react";

import PropTypes from "prop-types";

import { Menu, MenuItem } from "@material-ui/core";
import { MoreVert } from "@material-ui/icons";
import { GammaIconButton } from "../gamma-icon-button";
import generateId from "../../utils/generateId";

export class GammaMenu extends React.Component {
  state = {
    open: false,
    anchorElement: null,
    id: generateId()
  };

  _handleClose = () => {
    this.setState({
      ...this.state,
      open: false
    });
  };

  _handleClick = event => {
    this.setState({
      ...this.state,
      open: true,
      anchorElement: event.currentTarget
    });
  };

  render() {
    const { open } = this.state;

    return (
      <div>
        <GammaIconButton
          onClick={this._handleClick}
          aria-label="More"
          aria-owns={open ? this.state.id : null}
          aria-haspopup="true"
        >
          <MoreVert />
        </GammaIconButton>
        <Menu
          id={this.state.id}
          open={this.state.open}
          anchorEl={this.state.anchorElement}
          onClose={this._handleClose}
        >
          {Object.keys(this.props.valueToTextMap).map(value => {
            const text = this.props.valueToTextMap[value];
            return (
              <MenuItem
                key={value}
                onClick={() => {
                  this.props.onClick(value);
                  this._handleClose();
                }}
              >
                {text}
              </MenuItem>
            );
          })}
        </Menu>
      </div>
    );
  }
}

GammaMenu.propTypes = {
  valueToTextMap: PropTypes.object.isRequired,
  onClick: PropTypes.func.isRequired
};

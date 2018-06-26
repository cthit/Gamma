import React from "react";

import { ToastButton, StyledSnackbar } from "./GammaToast.view.styles";

class GammaToast extends React.Component {
  state = {
    open: false,
    toastClosed: true,
    currentText: "",
    currentDuration: 0,
    currentActionHandler: null,
    currentActionText: "",
    messages: []
  };

  handleClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({
      open: false
    });
  };

  handleExited = () => {
    this.setState({
      toastClosed: true
    });
  };

  componentDidUpdate(prevProps, prevState) {
    if (
      this.state.open &&
      this.state.messages.length > 0 &&
      !this.toastClosed
    ) {
      this.setState({
        open: false
      });
    } else if (
      prevState !== this.state &&
      !this.state.open &&
      this.state.messages.length > 0 &&
      this.state.toastClosed
    ) {
      const {
        text,
        duration,
        actionHandler,
        actionText
      } = this.state.messages.pop();

      this.setState({
        open: true,
        toastClosed: false,
        currentText: text,
        currentDuration: duration,
        currentActionHandler: actionHandler,
        currentActionText: actionText
      });
    }

    //A new message get added through props, one added, props will be ignored until next update
    else if (prevProps !== this.props) {
      const {
        text,
        duration,
        actionHandler,
        actionText
      } = this.props.toastOptions;

      this.setState(state => {
        messages: [
          state,
          {
            text: text,
            duration: duration == null ? 3000 : duration,
            actionText: actionText,
            actionHandler: actionHandler
          }
        ];
      });
    }
  }

  render() {
    return (
      <div>
        <StyledSnackbar
          anchorOrigin={{
            vertical: "bottom",
            horizontal: "left"
          }}
          autoHideDuration={this.state.currentDuration}
          open={this.state.open}
          onClose={this.handleClose}
          onExited={this.handleExited}
          ContentProps={{
            "aria-describedby": "message-id"
          }}
          message={<span id="message-id">{this.state.currentText}</span>}
          action={
            <ToastButton
              hide={this.state.currentActionText == null}
              key="undo"
              color="secondary"
              size="small"
              onClick={() => {
                this.state.actionHandler();
                this.handleClose();
              }}
            >
              {this.state.currentActionText == null //Text in a button must not be null
                ? ""
                : this.state.currentActionText}
            </ToastButton>
          }
        />
      </div>
    );
  }
}

export default GammaToast;

import React from "react";

import { ToastButton } from "./styles";

import { Button } from "styled-mdl";

class GammaToast extends React.Component {
  state = {
    text: "",
    duration: 0,
    isOpen: false,
    actionText: "",
    actionHandler: null
  };

  componentDidUpdate() {
    const {
      text,
      duration,
      actionHandler,
      actionText
    } = this.props.toastOptions;
    console.log(this.props);
    if (text != null) {
      var snackbarContainer = document.querySelector("#toast");
      var data = {
        message: text,
        timeout: duration,
        actionHandler:
          actionHandler != null
            ? () => {
                snackbarContainer.classList.remove("mdl-snackbar--active");
                actionHandler();
              }
            : null,
        actionText: actionText
      };
      snackbarContainer.MaterialSnackbar.showSnackbar(data);
    }
  }

  render() {
    return (
      <div id="toast" className="mdl-js-snackbar mdl-snackbar">
        <div className="mdl-snackbar__text" />
        <ToastButton accent className="mdl-snackbar__action" />
      </div>
    );
  }
}

export default GammaToast;

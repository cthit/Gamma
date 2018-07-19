import React from "react";
import { Redirect } from "react-router-dom";

import IfElseRendering from "../if-else-rendering";

class TryToRedirect extends React.Component {
  componentWillUpdate(prevProps) {
    const {
      redirectSuccessfull,
      currentPath,
      from,
      to,
      redirectTo,
      redirectFinished,
      redirectNow
    } = this.props;

    if (!prevProps.redirectFinished && redirectFinished) {
      if (redirectSuccessfull != null) {
        redirectSuccessfull();
      }
    } else if ((currentPath === from || redirectNow) && currentPath !== to) {
      redirectTo(to);
    }
  }

  render() {
    return null;
  }
}

export default TryToRedirect;

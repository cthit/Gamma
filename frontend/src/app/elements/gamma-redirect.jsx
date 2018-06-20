import React from "react";

import { Redirect } from "react-router-dom";

export class GammaRedirect extends React.Component {
  componentDidUpdate() {
    const { redirectPath, redirectFinished } = this.props;
    if (redirectPath != null && this.lastRedirectPath != redirectPath) {
      redirectFinished();
      this.lastRedirectPath = redirectPath;
    }
  }

  render() {
    const { redirectPath } = this.props;

    if (redirectPath != null) {
      return <Redirect to={redirectPath} push={true} />;
    }
    return null;
  }
}

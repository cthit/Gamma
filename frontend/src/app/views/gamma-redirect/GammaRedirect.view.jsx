import React from "react";
import { Redirect } from "react-router-dom";

class GammaRedirect extends React.Component {
  componentDidUpdate() {
    const { redirectPath, redirectFinished } = this.props;
    if (redirectPath != null) {
      redirectFinished();
    }
  }

  render() {
    const { redirectPath, currentPath } = this.props;

    if (redirectPath != null && currentPath !== redirectPath) {
      return <Redirect to={redirectPath} push={true} />;
    }
    return null;
  }
}

export default GammaRedirect;

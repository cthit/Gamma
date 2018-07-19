import React from "react";

class ContainUserToAllowedPages extends React.Component {
  componentDidUpdate(prevProps) {
    var {
      allowedBasePaths,
      allowedFullPaths,
      currentPath,
      redirectTo,
      to
    } = this.props;

    if (allowedFullPaths == null) {
      allowedFullPaths = [];
    }
    allowedFullPaths.push(to);

    var allowedOnThisPage = false;

    if (allowedBasePaths != null) {
      for (var i in allowedBasePaths) {
        const basePath = allowedBasePaths[i];
        if (currentPath.startsWith(basePath)) {
          allowedOnThisPage = true;
          break;
        }
      }
    }

    for (var i in allowedFullPaths) {
      const fullPath = allowedFullPaths[i];
      if (currentPath === fullPath) {
        allowedOnThisPage = true;
        break;
      }
    }

    if (!allowedOnThisPage) {
      redirectTo(to);
    }
  }

  render() {
    return null;
  }
}

export default ContainUserToAllowedPages;

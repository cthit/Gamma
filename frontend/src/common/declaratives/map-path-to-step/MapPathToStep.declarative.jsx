import React from "react";
import PropTypes from "prop-types";

const MapPathToStep = ({ currentPath, pathToStepMap, render }) => (
  <div>{render(getCurrentStep(currentPath, pathToStepMap))}</div>
);

function getCurrentStep(currentPath, pathToStepMap) {
  if (!pathToStepMap.hasOwnProperty(currentPath)) {
    console.log("WARNING: There isn't a step for the path: " + currentPath);
    return 0;
  } else {
    return pathToStepMap[currentPath];
  }
}

MapPathToStep.propTypes = {
  currentPath: PropTypes.string.isRequired,
  pathToStepMap: PropTypes.object.isRequired,
  render: PropTypes.func.isRequired
};

export default MapPathToStep;

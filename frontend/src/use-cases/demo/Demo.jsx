import React from "react";
import styled from "styled-components";
import { Add } from "@material-ui/icons";

import { Heading, Title, Subtitle } from "../../common-ui/text";

import GammaTable from "../../common/temp/GammaTable";

class Demo extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <StyledGammaTable />
      </div>
    );
  }
}

const StyledGammaTable = styled(GammaTable)``;

export default Demo;

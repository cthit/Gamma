import React from "react";
import { Fill } from "../../common-ui/layout";
import { Display } from "../../common-ui/text";

class Home extends React.Component {
  constructor(props) {
    super();

    props.gammaLoadingFinished();
  }

  render() {
    return <div>Hej</div>;
  }
}

export default Home;

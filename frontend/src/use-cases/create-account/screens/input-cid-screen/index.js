import React, { Component } from "react";
import { Textfield } from "styled-mdl";
import { Button } from "styled-mdl";

class InputCidScreen extends Component {
  render() {
    return (
      <div>
        <Textfield label="cid" />
        <Button primary raised>
          Skicka cid
        </Button>
      </div>
    );
  }
}
export default InputCidScreen;

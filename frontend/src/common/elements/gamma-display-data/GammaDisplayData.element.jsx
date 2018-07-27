import React from "react";
import { Fill, Padding } from "../../../common-ui/layout";

import { HorizontalFill, FixedWidth } from "./GammaDisplayData.element.styles";
import { Text } from "../../../common-ui/text";

const GammaDisplayData = ({ data, keysText, keysOrder }) => (
  <Fill>
    {keysOrder.map(key => (
      <Padding>
        <HorizontalFill>
          <FixedWidth>
            <Text bold text={keysText[key]} />
          </FixedWidth>
          <Fill>
            <Text text={data[key]} />
          </Fill>
        </HorizontalFill>
      </Padding>
    ))}
  </Fill>
);

export default GammaDisplayData;

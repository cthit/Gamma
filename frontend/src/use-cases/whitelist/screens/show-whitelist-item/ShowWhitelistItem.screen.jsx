import React from "react";
import styled from "styled-components";
import { Fill, Center, Spacing } from "../../../../common-ui/layout";

import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaLink
} from "../../../../common-ui/design";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Text } from "../../../../common-ui/text";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Edit } from "@material-ui/icons";

const WhitelistInformationOrder = ["id", "cid"];

const ShowWhitelistItem = ({ whitelistItem, text }) => (
  <IfElseRendering
    test={whitelistItem != null}
    ifRender={() => (
      <Fill>
        <Center>
          <GammaCard minWidth="300px" maxWidth="600px">
            <GammaCardBody>
              <HorizontalContainer>
                <RightAlignFill>
                  {WhitelistInformationOrder.map(prop => (
                    <Text key={prop} bold text={text[prop] + ": "} />
                  ))}
                </RightAlignFill>
                <Spacing />
                <LeftAlignFill>
                  {WhitelistInformationOrder.map(prop => (
                    <Text
                      key={prop}
                      text={
                        whitelistItem[prop] == null
                          ? "null"
                          : whitelistItem[prop]
                      }
                    />
                  ))}
                </LeftAlignFill>
              </HorizontalContainer>
            </GammaCardBody>
          </GammaCard>
        </Center>
        <GammaLink to={"/whitelist/" + whitelistItem.id + "/edit"}>
          <GammaFABButton component={Edit} secondary />
        </GammaLink>
      </Fill>
    )}
  />
);

export default ShowWhitelistItem;

const LeftAlignFill = styled(Fill)`
  text-align: left;
`;

const RightAlignFill = styled(Fill)`
  text-align: right;
`;

const HorizontalContainer = styled.div`
  display: flex;
  flex-direction: row;
`;

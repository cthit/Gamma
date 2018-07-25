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

const PostInformationOrder = ["id", "sv", "en"];

const ShowPostDetails = ({ post, text }) => (
  <IfElseRendering
    test={post != null}
    ifRender={() => (
      <Fill>
        <Center>
          <GammaCard minWidth="300px" maxWidth="600px">
            <GammaCardBody>
              <HorizontalContainer>
                <RightAlignFill>
                  {PostInformationOrder.map(prop => (
                    <Text key={prop} bold text={text[prop] + ": "} />
                  ))}
                </RightAlignFill>
                <Spacing />
                <LeftAlignFill>
                  {PostInformationOrder.map(prop => (
                    <Text
                      key={prop}
                      text={post[prop] == null ? "null" : post[prop]}
                    />
                  ))}
                </LeftAlignFill>
              </HorizontalContainer>
            </GammaCardBody>
          </GammaCard>
        </Center>
        <GammaLink to={"/posts/" + post.id + "/edit"}>
          <GammaFABButton component={Edit} secondary />
        </GammaLink>
      </Fill>
    )}
  />
);

export default ShowPostDetails;

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

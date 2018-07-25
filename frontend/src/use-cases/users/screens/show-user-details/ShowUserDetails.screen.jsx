import React from "react";
import styled from "styled-components";
import { Fill, Center, Spacing } from "../../../../common-ui/layout";
import {
  GammaCard,
  GammaCardDisplayTitle,
  GammaCardBody,
  GammaCardButtons,
  GammaLink
} from "../../../../common-ui/design";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Text } from "../../../../common-ui/text";
import GammaButton from "../../../../common/elements/gamma-button";

const UserInformationOrder = [
  "cid",
  "firstName",
  "lastName",
  "nick",
  "email",
  "acceptanceYear"
];

const ShowUserDetails = ({ user, text }) => (
  <IfElseRendering
    test={user != null}
    ifRender={() => (
      <Center>
        <GammaCard minWidth="300px" maxWidth="600px">
          <GammaCardDisplayTitle
            text={user.firstName + " '" + user.nick + "' " + user.lastName}
          />
          <GammaCardBody>
            <HorizontalContainer>
              <RightAlignFill>
                {UserInformationOrder.map(prop => (
                  <Text key={prop} bold text={text[prop] + ": "} />
                ))}
              </RightAlignFill>
              <Spacing />
              <LeftAlignFill>
                {UserInformationOrder.map(prop => (
                  <Text key={prop} text={user[prop]} />
                ))}
              </LeftAlignFill>
            </HorizontalContainer>
          </GammaCardBody>
          <GammaCardButtons reverseDirection>
            <GammaLink to={"/users/" + user.cid + "/edit"}>
              <GammaButton text={text.Edit} primary raised />
            </GammaLink>
          </GammaCardButtons>
        </GammaCard>
      </Center>
    )}
  />
);

export default ShowUserDetails;

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

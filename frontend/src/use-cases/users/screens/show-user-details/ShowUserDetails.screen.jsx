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
import GammaDisplayData from "../../../../common/elements/gamma-display-data";

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
            <GammaDisplayData
              data={user}
              keysText={{
                cid: text.cid,
                firstName: text.firstName,
                lastName: text.lastName,
                nick: text.nick,
                email: text.email,
                acceptanceYear: text.acceptanceYear
              }}
              keysOrder={[
                "cid",
                "firstName",
                "lastName",
                "nick",
                "email",
                "acceptanceYear"
              ]}
            />
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

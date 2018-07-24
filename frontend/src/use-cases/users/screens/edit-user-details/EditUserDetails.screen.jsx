import React from "react";
import {
  GammaCardDisplayTitle,
  GammaCardBody,
  GammaCard
} from "../../../../common-ui/design";
import GammaTextField from "../../../../common/elements/gamma-text-field";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Center } from "../../../../common-ui/layout";

const EditUserDetails = ({ user, text }) => (
  <IfElseRendering
    test={user != null}
    ifRender={() => (
      <Center>
        <GammaCard minWidth="300px" maxWidth="600px">
          <GammaCardDisplayTitle
            text={
              "Redigerar " +
              user.firstName +
              " '" +
              user.nick +
              "' " +
              user.lastName
            }
          />
          <GammaCardBody />
        </GammaCard>
      </Center>
    )}
  />
);

export default EditUserDetails;

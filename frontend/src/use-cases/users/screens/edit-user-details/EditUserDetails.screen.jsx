import React from "react";
import {
  GammaCardDisplayTitle,
  GammaCardBody,
  GammaCard
} from "../../../../common-ui/design";

import GammaTextField from "../../../../common/elements/gamma-text-field";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Center } from "../../../../common-ui/layout";
import EditUserInformation from "../common-views/edit-user-information/EditUserInformation.view";

const EditUserDetails = ({ user, text }) => (
  <IfElseRendering
    test={user != null}
    ifRender={() => (
      <Center>
        <EditUserInformation
          initialValues={{
            ...user,
            acceptanceYear: user.acceptanceYear + ""
          }}
          onSubmit={(values, actions) => {
            console.log(values);
          }}
        />
      </Center>
    )}
  />
);

export default EditUserDetails;

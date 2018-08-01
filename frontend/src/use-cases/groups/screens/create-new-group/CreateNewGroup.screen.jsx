import React from "react";

import { Fill, Center } from "../../../../common-ui/layout";

import GroupForm from "../common-views/group-form";

const CreateNewGroup = ({ groupsAdd }) => (
  <Fill>
    <Center>
      <GroupForm
        onSubmit={(values, actions) => {
          groupsAdd(values);
        }}
        initialValues={{
          name: "",
          description: {
            sv: "",
            en: ""
          },
          email: "",
          func: {
            sv: "",
            en: ""
          }
        }}
      />
    </Center>
  </Fill>
);

export default CreateNewGroup;
